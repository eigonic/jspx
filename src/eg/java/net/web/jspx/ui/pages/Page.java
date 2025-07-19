package eg.java.net.web.jspx.ui.pages;

import eg.java.net.web.jspx.engine.RequestHandler;
import eg.java.net.web.jspx.engine.annotation.JspxBean;
import eg.java.net.web.jspx.engine.annotation.JspxBeanValue;
import eg.java.net.web.jspx.engine.annotation.JspxClientMethod;
import eg.java.net.web.jspx.engine.annotation.JspxWebControl;
import eg.java.net.web.jspx.engine.el.HttpRequestAttributeMap;
import eg.java.net.web.jspx.engine.el.HttpRequestHeaderMap;
import eg.java.net.web.jspx.engine.el.HttpRequestParameterMap;
import eg.java.net.web.jspx.engine.el.HttpSessionMap;
import eg.java.net.web.jspx.engine.error.JspxException;
import eg.java.net.web.jspx.engine.error.PageNotAccessibleException;
import eg.java.net.web.jspx.engine.message.JspxMessage;
import eg.java.net.web.jspx.engine.parser.PageModelComposer;
import eg.java.net.web.jspx.engine.util.*;
import eg.java.net.web.jspx.engine.util.ui.Render;
import eg.java.net.web.jspx.engine.util.ui.RenderPrinter;
import eg.java.net.web.jspx.ui.controls.WebControl;
import eg.java.net.web.jspx.ui.controls.attrbs.Attribute;
import eg.java.net.web.jspx.ui.controls.html.elements.Calendar;
import eg.java.net.web.jspx.ui.controls.html.elements.Form;
import eg.java.net.web.jspx.ui.controls.html.elements.Label;
import eg.java.net.web.jspx.ui.controls.html.elements.ResourceBundle;
import eg.java.net.web.jspx.ui.controls.html.elements.ajax.AjaxPanel;
import eg.java.net.web.jspx.ui.controls.html.elements.inputs.CheckBox;
import eg.java.net.web.jspx.ui.controls.html.elements.inputs.FileUpload;
import eg.java.net.web.jspx.ui.controls.html.elements.inputs.RadioButton;
import eg.java.net.web.jspx.ui.controls.html.elements.markers.*;
import eg.java.net.web.jspx.ui.controls.html.elements.select.Select;
import eg.java.net.web.jspx.ui.controls.html.w3.Head;
import eg.java.net.web.jspx.ui.controls.html.w3.Script;
import eg.java.net.web.jspx.ui.controls.html.w3.Title;
import eg.java.net.web.jspx.ui.controls.validators.ValidationException;
import eg.java.net.web.jspx.ui.controls.validators.ValidationScript;
import eg.java.net.web.jspx.ui.controls.validators.Validator;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.jexl2.MapContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.*;

/**
 * Main controller for All JSPX files. <br />
 * used as default for pages without controller. <br />
 * the content and master pages inherits from.
 *
 * @author amr.eladawy
 */
public class Page {
    public static final String DispatchedPage = "DispatchedPage";
    public static final String RedirectPage = "RedirectPage";
    public static final String JspxBeanNamePrefix = "jspxBean:";
    public static final String RepeaterNamePrefix = "repeater:";
    public static final String DBBeanNamePrefix = "dbbean:";
    public static final String Controller = "controller";
    public static final String MasterKey = "master";
    public static final String TitleKey = "title";
    public static final String AccessTypeKey = "accessType";
    public static final String AllowedRolesKey = "allowedRoles";

    // [Oct 14, 2009 8:18:40 AM] [Amr.ElAdawy] [conversation scope ID]
    public static final String DeniedRolesKey = "deniedRoles";
    public static final String JSPX_HIDDEN_AJAX_PANEL = "JSPX_HIDDEN_AJAX_PANEL";
    private static final Logger logger = LoggerFactory.getLogger(Page.class);
    public static String CONVERSATION_KEY = "JSPX_CONVERSATION_SCOPE_ID";
    public static int PageCompose = -1;
    public static int PageViewState = -2;
    public static int PageLoad = -3;
    public static int PagePostBackData = -4;
    public static int PreValidating = -5;
    public static int PostValidating = -6;
    public static int PageWireEvents = -7;
    public static int PageSavingViewState = -8;
    public static int PagePreRendering = -9;
    public static int PageRendering = -10;
    public static int PageRendered = -11;
    public static int PageDispatched = -12;
    public HttpServletRequest request;
    public HttpServletResponse response;
    public HttpSession session;
    public String conversationId;
    public boolean isPostBack;
    public boolean isDispatched;
    public boolean isAjaxPostBack;
    /**
     * carriage to save common and shared data across the page.
     */
    public PageCarriage carriage = new PageCarriage();
    /**
     * contains all annotated Web Controls
     */
    public HashMap<String, Field> annotatedWebControls = new HashMap<String, Field>();
    /**
     * contains all annotated JSPX Beans
     */
    public HashMap<String, Field> annotatedBeans = new HashMap<String, Field>();
    public int PageStatus = Page.PageCompose;
    /**
     * tells whether this is a multi part from or not.
     */
    public boolean isMultiPartForm = false;
    public boolean isSendRedirect;
    public boolean isSendDispatch;
    public String newLocation;
    protected ServletContext context;
    protected String charSet;
    protected String filePath;
    protected Hashtable<String, String> queryString = new Hashtable<String, String>();
    protected boolean stopped = false;
    protected List<WebControl> controls = new ArrayList<WebControl>();
    protected List<WebControl> declaredControls = new ArrayList<WebControl>();
    protected HashMap<String, WebControl> undeclaredControls = new HashMap<String, WebControl>();
    protected HashMap<String, HashMap<String, String>> viewState = new HashMap<String, HashMap<String, String>>();
    protected HashMap<String, WebControl> internalEventsControls = new HashMap<String, WebControl>();
    protected HashMap<String, InternalValueHolder> internalValueHolders = new HashMap<String, InternalValueHolder>();
    protected HashMap<String, WebControl> valueHolderControls = new HashMap<String, WebControl>();
    protected List<String> valueHolderControlKeys = new ArrayList<String>();
    protected HashMap<String, WebControl> itemDataBoundControls = new HashMap<String, WebControl>();
    protected HashMap<String, WebControl> itemDataBoundChildControls = new HashMap<String, WebControl>();
    protected HashMap<String, ResourceBundle> bundles = new HashMap<String, ResourceBundle>();
    protected HashMap<String, IAjaxSubmitter> ajaxSubmitters = new HashMap<String, IAjaxSubmitter>();
    protected List<JspxMessage> messages = new ArrayList<JspxMessage>();
    protected List<String> clientMethods = new ArrayList<String>();
    /**
     * boolean to render the onload script in case it was not loaded at the start of a body
     */
    protected boolean isOnLoadScriptRendered;
    /**
     * buffer to attach the onload scripts to be executed
     */
    protected StringBuffer onloadScript = new StringBuffer();
    /**
     * Map of all ID in the page
     */
    protected HashMap<String, String> uniqueID = new HashMap<String, String>();
    /**
     * list of user pages kept in this page.
     */
    protected List<PortletPage> portletPages = new ArrayList<PortletPage>();
    /**
     * The Ajax Submitter that will be only rendered in case of Ajax call.
     */
    protected IAjaxSubmitter activeAjaxSubmitter = null;
    protected String headerText = "";
    protected String footerText = "";
    protected Page sender;
    protected ValidationScript validationScript;
    protected Locale locale = new Locale("en");
    /**
     * in case of multi/part-from, this collection will have the posted data.
     */
    protected HashMap<String, List<FileItem>> fileItemsMap = new HashMap<String, List<FileItem>>();
    protected List<String> scriptNames = new ArrayList<String>();
    /**
     * collection for the validators, used latter to validate controls after post back.
     */
    protected List<IValidate> validators = new ArrayList<IValidate>();
    protected String title;
    protected String accessType;
    protected String codeBehinde;
    protected String allowedRoles;
    protected String deniedRoles;
    protected Title titleControl = new Title();
    /**
     * JEXL context for this page
     */

    MapContext pageJEXLContext = new MapContext();
    private String pageURL;
    private Head head = null;

    /**
     * loads the query String data into the queryString collection.
     *
     * @param request
     */
    public void loadQueryString(HttpServletRequest request) {
        String fullQueryString = request.getQueryString();
        if (!StringUtility.isNullOrEmpty(fullQueryString)) {
            String decodedQueryString = fullQueryString;
            try {
                decodedQueryString = URLDecoder.decode(fullQueryString, "UTF-8");
            } catch (UnsupportedEncodingException e) {
            }

            String[] queryPairs = decodedQueryString.split("&");
            String[] queryItem;
            for (int i = 0; i < queryPairs.length; i++) {
                queryItem = queryPairs[i].split("=");
                if (queryItem.length == 1)
                    queryString.put(queryItem[0], "");
                else if (queryItem.length == 2)
                    queryString.put(queryItem[0], queryItem[1]);
            }
        }
        pageInit();
    }

    protected void pagePreLoad() {

    }

    /**
     * continues the execution of the page serving.
     *
     * @throws Exception
     */
    public void continueServing() throws Exception {
        String threadName = Thread.currentThread().getName();
        if (!Security.isAccessible(allowedRoles, deniedRoles, this)) {
            logger.error("Page is not accessible , sending 403");
            throw new PageNotAccessibleException(this.pageURL);
        }
        // [Nov 10, 2013 8:14:44 AM] [Amr.ElAdawy] [remove expired Conversation Scope beans]
        clearExpiredBeans(RequestHandler.JSPX_BEAN_EXPIRATION_PERIOD_MINUTES);
        if (this instanceof ContentPage)
            ((ContentPage) this).getMasterPage().clearExpiredBeans(RequestHandler.JSPX_BEAN_EXPIRATION_PERIOD_MINUTES);
        if (this instanceof ContentPage)
            ((ContentPage) this).getMasterPage().initializeJexlContext();
        initializeJexlContext();
        for (PortletPage portletPage : portletPages)
            portletPage.initializeJexlContext();

        if (this instanceof ContentPage)
            ((ContentPage) this).getMasterPage().loadAnnotatedBeans();
        loadAnnotatedBeans();
        for (PortletPage portletPage : portletPages)
            portletPage.loadAnnotatedBeans();

        if (this instanceof ContentPage)
            ((ContentPage) this).getMasterPage().loadAnnotatedClientMethods();
        loadAnnotatedClientMethods();
        for (PortletPage portletPage : portletPages)
            portletPage.loadAnnotatedClientMethods();

        defineConversationId();

        initializeJspxBeans(true);
        for (PortletPage portletPage : portletPages)
            portletPage.initializeJspxBeans(true);

        // see if this is an ajax call or normal request
        checkAjax();
        for (PortletPage userPage : portletPages)
            userPage.checkAjax();

        // page pre loading phase
        pagePreLoad();
        for (PortletPage userPage : portletPages)
            userPage.pagePreLoad();

        initializeJspxBeans(false);
        for (PortletPage userPage : portletPages)
            userPage.initializeJspxBeans(false);

        if (getSender() != null) {
            setPageStatus(PageDispatched);
            isDispatched = true;
            if (this instanceof ContentPage)
                ((ContentPage) this).getMasterPage().isDispatched = true;
            pageDispatched();
            for (PortletPage userPage : portletPages)
                userPage.pageDispatched();
        }
        if (isPostBack) {
            // [Oct 14, 2009 10:17:40 AM] [Amr.ElAdawy] [commented as it is
            // called from the conversation scop id define method]
            Security.setThreadName(threadName + " - Loading ViewState");
            setPageStatus(PageViewState);
            loadViewState();
            for (PortletPage userPage : portletPages)
                userPage.loadViewState();
            Security.setThreadName(threadName + " - Loading Postback Data");
            setPageStatus(PagePostBackData);
            // [Jun 26, 2011 4:03:38 PM] [amr.eladawy] [support beans in master
            // page and interoperability]
            if (this instanceof ContentPage)
                ((ContentPage) this).getMasterPage().loadJspxBeanData();
            loadJspxBeanData();
            for (PortletPage userPage : portletPages)
                userPage.loadJspxBeanData();
            loadPostBackData();
            for (PortletPage userPage : portletPages)
                userPage.loadPostBackData();
            initializeJspxBeans(true);
            for (PortletPage userPage : portletPages)
                userPage.initializeJspxBeans(true);
        }
        setPageStatus(PageLoad);
        if (!stopped) {
            Security.setThreadName(threadName + " - Page Loaded");
            if (this instanceof ContentPage)
                ((ContentPage) this).getMasterPage().pageLoaded();
            pageLoaded();
            for (PortletPage userPage : portletPages)
                userPage.pageLoaded();
        }
        // Now work on the post back
        if (!stopped && isPostBack)
            try {
                Security.setThreadName(threadName + " - Validating Postback Data");
                validatePostbackData();
                for (PortletPage userPage : portletPages)
                    userPage.validatePostbackData();
                setPageStatus(PageWireEvents);
                Security.setThreadName(threadName);
                wireEventHandelers();
                for (PortletPage userPage : portletPages)
                    userPage.wireEventHandelers();
            } catch (ValidationException e) {
                // do nothing as the exception has skipped the event handling
                // wiring ..
                logger.error("continueServing()", e);
            }

        setPageStatus(PageSavingViewState);
        Security.setThreadName(threadName + " - Composing View State");
        composeViewState();
        for (PortletPage userPage : portletPages)
            userPage.composeViewState();
        setPageStatus(PageRendering);
        if (!stopped) {
            Security.setThreadName(threadName + " - Pre-Rendering Page");
            if (this instanceof ContentPage)
                ((ContentPage) this).getMasterPage().pagePreRender();
            pagePreRender();
            for (PortletPage userPage : portletPages)
                userPage.pagePreRender();
        }
        Security.setThreadName(threadName + " - Rendering Page");
        // syncronize data modified in wireEvent to the data models
        if (!stopped) {
            initializeJspxBeans(false);
            for (PortletPage userPage : portletPages)
                userPage.initializeJspxBeans(false);
            syncronizeDataModels();
            for (PortletPage userPage : portletPages)
                userPage.syncronizeDataModels();
        }
        if (!stopped)
            renderPage();
        if (!stopped) {
            setPageStatus(PageRendered);
            Security.setThreadName(threadName + " - Page is Rendered");
            if (this instanceof ContentPage)
                ((ContentPage) this).getMasterPage().pageRendered();
            pageRendered();
        }
        Security.setThreadName(threadName);
    }

    protected void checkAjax() {
        String eventTarget = getPostBackData(Form.EventTarget);
        // checking on Ajax Call
        if (!StringUtility.isNullOrEmpty(eventTarget) && !eventTarget.equals("null") && !eventTarget.equals("undefined")) {
            if (eventTarget.equals(JSPX_HIDDEN_AJAX_PANEL))
                activeAjaxSubmitter = Render.createHiddenAjaxPane(this);
            else
                activeAjaxSubmitter = getAjaxSubmitter(eventTarget);
            // [Jun 22, 2009 8:23:34 PM] [amr.eladawy] [if the active submitter
            // is not found here, dont mark this as ajax call]
            if (activeAjaxSubmitter != null)
                isAjaxPostBack = true;
            // [Jun 22, 2009 8:19:58 PM] [amr.eladawy] [in this stage, we should
            // invoke the Master page checkAjax() in
            // order to all Ajax panels on a master]
            if (this instanceof ContentPage)
                ((ContentPage) this).getMasterPage().checkAjax();
        }
    }

    /**
     * Initialize the JEXL context.
     */
    protected void initializeJexlContext() {
        if (RequestHandler.USE_JEXL) {
            pageJEXLContext.set("this", this);
            pageJEXLContext.set("request", request);
            pageJEXLContext.set("request.parameters", new HttpRequestParameterMap(request));
            pageJEXLContext.set("session.parameters", new HttpSessionMap(session));
            pageJEXLContext.set("request.attributes", new HttpRequestAttributeMap(request));
            pageJEXLContext.set("request.headers", new HttpRequestHeaderMap(request));
            //			pageJEXLContext.set("request.uri", request.getRequestURI());
            // [Aug 23, 2013 8:28:38 AM] [Amr.ElAdawy] [set this page as a super
            // of the portletpages.]
            for (PortletPage portlet : portletPages)
                portlet.pageJEXLContext.set("super", this);
            // [Jul 16, 2013 9:45:34 AM] [Amr.ElAdawy] [if this is a Content
            // page, add the bundles from the Master Page to the
            // jexl context of
            // this page
            // in order to avail the resource bundle to the content page as
            // well.]
            if (this instanceof ContentPage) {
                MasterPage mpage = ((ContentPage) this).getMasterPage();
                if (mpage != null) {
                    // [Aug 20, 2013 4:32:42 PM] [Amr.ElAdawy] [Add the Master
                    // page under the name super]
                    pageJEXLContext.set("super", mpage);
                    for (String key : mpage.bundles.keySet()) {
                        pageJEXLContext.set(key, mpage.getBundle(key).getBundle());
                        // [Aug 23, 2013 8:28:51 AM] [Amr.ElAdawy] [add the
                        // bundle to all the portlet]
                        for (PortletPage portlet : portletPages)
                            portlet.pageJEXLContext.set(key, mpage.getBundle(key).getBundle());
                    }
                }
            }

        }
    }

    /**
     * iterate through validators and
     */
    protected void validatePostbackData() throws ValidationException {
        String groupName = getPostBackData(Form.Group);
        if (StringUtility.isNullOrEmpty(groupName))
            return;
        for (IValidate validateItem : validators) {
            if (validateItem instanceof IValidator) {
                IValidator validator = (IValidator) validateItem;
                // [Jan 24, 2012 5:13:14 PM] [amr.eladawy] [split groupname to
                // support multi groups]
                String[] parts = groupName.split(",");
                for (String name : parts) {
                    if (validator.getGroup().equalsIgnoreCase(name) && ((WebControl) validateItem).isIncluded()
                            && ((WebControl) validateItem).isRendered() && ((WebControl) validateItem).isAccessible())
                        validator.validate();
                }
            } else if (validateItem instanceof IControlValidate && ((WebControl) validateItem).isIncluded() && ((WebControl) validateItem).isRendered()) {
                validateItem.validate();
            }
        }
    }

    /**
     * Redirect the user to new page, used instead of response.sendRedirect() in order to stop parsing the page nor rendering.
     *
     * @param location
     */
    protected void redirect(String location) {
        isSendRedirect = true;
        // [Sep 15, 2012 10:14:00 AM] [Amr.ElAdawy] [if this is an ajax postback
        // on multipart form, dont stop the rendering
        // as the AjaxPenl has to render special script in the hidden iframe to
        // change the parent]
        if (!(isAjaxPostBack && isMultiPartForm))
            stopped = true;
        try {
            initializeJspxBeans(false);
            // if the URL contains :// like http:// https:// ftp:// this is
            // absolute url
            if (!location.contains("://")) {
                if (!location.startsWith("/"))
                    location = "/" + location;
                location = request.getContextPath() + location;
            }
            newLocation = location;
            if (isAjaxPostBack) {
                response.addHeader(RequestHandler.PageStatusHeader, RedirectPage);
                response.addHeader(RequestHandler.AjaxPageLocation, newLocation);
            }
            if (!(isAjaxPostBack))
                response.sendRedirect(location);
        } catch (IOException e) {
            logger.warn("redirect() - exception ignored", e);
        }
    }

    /**
     * Transfers the execution from this page to another page.
     *
     * @param newPage the name of the page execution will be transfered to .e.g. // * PageCalss.getClass().getName()
     */
    public void dispatch(String newPage) {
        isSendDispatch = true;
        // [Sep 15, 2012 10:14:00 AM] [Amr.ElAdawy] [if this is an ajax postback
        // on multipart form, dont stop the rendering
        // as the AjaxPenl has to render special script in the hidden iframe to
        // change the parent]
        if (!isAjaxPostBack || !isMultiPartForm)
            stopped = true;
        try {
            initializeJspxBeans(false);
            if (newPage.indexOf("/") != 0)
                newPage = "/" + newPage;
            RequestHandler.start(request, response, false, newPage, context, this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Stops the normal execution of this page. used in the following cases
     * <ol>
     * <li>
     * Redirect</li>
     * <li>
     * Dispatch</li>
     * <li>
     * Export To Excel</li>
     * </ol>
     */
    public void skip() {
        stopped = true;
    }

    /**
     * checks whether this form is a multipart or not. then loads the form data.
     */
    protected void loadMultiPartForm() {
        // if there is a master page, load the mulit part form variable.
        if (this instanceof ContentPage)
            ((ContentPage) this).getMasterPage().loadMultiPartForm();
        isMultiPartForm = ServletFileUpload.isMultipartContent(request);

        if (!(isMultiPartForm && this instanceof MasterPage)) {
            if (isMultiPartForm)
                fileItemsMap = RequestUtil.getParameters(request);
            if (isMultiPartForm && this instanceof ContentPage)
                ((ContentPage) this).getMasterPage().fileItemsMap = fileItemsMap;
        }
    }

    /**
     * iterates through all declated controls and get their viewstate.
     */
    protected void composeViewState() {
        for (int i = 0; i < declaredControls.size(); i++)
            declaredControls.get(i).saveViewState(viewState);
        for (String name : internalEventsControls.keySet())
            internalEventsControls.get(name).saveViewState(viewState);
        for (WebControl webControl : itemDataBoundChildControls.values())
            webControl.saveViewState(viewState);

    }

    protected void syncronizeDataModels() {
        for (WebControl webControl : itemDataBoundControls.values())
            ((ItemDataBound) webControl).reverseBinding();
    }

    /**
     * loads the view state data.
     */
    protected void loadViewState() {
        try {
            String vs = getPostBackData(Form.ViewState);
            if (!StringUtility.isNullOrEmpty(vs))
                viewState = StringUtility.split(vs, Render.mostOuterJoiner, Render.innerJoiner, Render.outerJoiner, Render.mostInnerJoiner);
            // iterate through all declared control and give them their
            // viewstate
            for (int i = 0; i < declaredControls.size(); i++)
                declaredControls.get(i).loadViewState(viewState.get(declaredControls.get(i).getId()));
            for (String key : internalEventsControls.keySet())
                internalEventsControls.get(key).loadViewState(viewState.get(internalEventsControls.get(key).getId()));
            for (WebControl webControl : itemDataBoundChildControls.values())
                webControl.loadViewState(viewState.get(webControl.getId()));
        } catch (Exception e) {
            logger.error("loadViewState()", e);
        }
    }

    /**
     * extracts the posted parameter by the key; change by Mohamed.IHassan
     *
     * @param key
     * @return
     */
    public String getPostBackData(String key) {
        // [Dec 30, 2010 1:19:48 PM] [Amr.ElAdawy] [checking if this key is the
        // id of a control that was marked as a safe control
        // or not]
        boolean safe = false;
        try {
            WebControl control = getControl(key);
            safe = (control != null && control.getIsSafe());
        } catch (Exception e) {
        }
        String result = null;

        if (isMultiPartForm)
            result = getFileItemString(key);
        else {
            String[] resultsParams = request.getParameterValues(key);
            if (resultsParams != null && resultsParams.length > 1)
                result = StringUtility.join(resultsParams, ",");
            else
                result = request.getParameter(key);
            try {
                // [Jul 10, 2009 6:20:18 PM] [amr.eladawy] [if this is an ajax
                // call, then the data will be sent UTF-8 encoded;
                // so here we need to extract them using UTF-8 encoding]
            } catch (Exception e) {
            }
        }
        if (!safe)
            return StringUtility.sanitizedData(result);
        return result;
    }

    /**
     * returns true if the passed key is part of the request parameteres
     * [4 Jun 2015 17:08:39]
     *
     * @param key
     * @return
     * @author aeladawy
     */
    public boolean requestHasParameter(String key) {
        if (isMultiPartForm) {
            return fileItemsMap.containsKey(key);
        } else {
            Enumeration<String> params = request.getParameterNames();

            while (params.hasMoreElements()) {
                String name = params.nextElement();
                if (name.equals(key))
                    return true;
            }
        }
        return false;
    }

    /**
     * loads the given file upload with values from the file item.
     *
     * @param fileItem
     */
    protected void loadFile(FileUpload fileUpload, List<FileItem> fileItems) {
        if (fileItems == null || fileItems.isEmpty())
            return;
        FileItem fileItem = fileItems.get(0);
        // [Oct 22, 2009 5:44:00 PM] [Amr.ElAdawy] [setting the full name to the
        // posted value]
        fileUpload.setFileFullName(fileItem.getName());
        fileUpload.setFileName(StringUtility.getFileName(fileUpload.getFileFullName()));
        fileUpload.setValue(fileItem.getName());
        fileUpload.setFileType(fileItem.getContentType());
        fileUpload.setFileData(fileItem.get());
        fileUpload.setFileItem(fileItem);
    }

    protected String getFileItemString(String key) {
        StringBuilder value = new StringBuilder();
        List<FileItem> items = getFileItem(key);
        try {
            for (FileItem fileItem : items)
                value.append(fileItem.getString(getCharSet())).append(",");
            if (value.length() > 0)
                value.deleteCharAt(value.length() - 1);
        } catch (UnsupportedEncodingException e) {
            for (FileItem fileItem : items)
                value.append(fileItem.getString()).append(",");
            if (value.length() > 0)
                value.deleteCharAt(value.length() - 1);
        } catch (NullPointerException e) {
            return null;
        }
        return value.toString();
    }

    /**
     * gets the file item from the items map.
     *
     * @param key
     * @return
     */
    public List<FileItem> getFileItem(String key) {
        return fileItemsMap.get(key);
    }

    /**
     * loads the post back data into the member controls.
     */
    protected void loadPostBackData() {
        // load Master page controls data.
        if (this instanceof ContentPage)
            ((ContentPage) this).getMasterPage().loadPostBackData();
        // for (WebControl webControl : valueHolderControls.values()) {
        for (String key : valueHolderControlKeys) {
            WebControl webControl = valueHolderControls.get(key);
            if (webControl.isIncluded()) {
                loadControlPostBackData(webControl);
            } else {
                logger.debug(webControl + " is NOT Rendered.");
            }
        }
        // load internal value holder
        for (InternalValueHolder webControl : internalValueHolders.values())
            webControl.setInternalInputValues(getInternalParameters(getInterlPostedDataKeys(((WebControl) webControl).getId())));
        // Load Item data bound controls Children.
        // for (WebControl webControl : itemDataBoundChildControls.values())
        // loadControlPostBackData(webControl);

        // bind the item data bound control.
        for (WebControl webControl : itemDataBoundControls.values())
            ((ItemDataBound) webControl).dataBind();
    }

    protected void loadControlPostBackData(WebControl control) {
        if (control instanceof FileUpload) {
            loadFile((FileUpload) control, getFileItem(control.getName()));
            return;
        }
        String postedValue = getPostBackData(control.getId());

        if (control instanceof ValueHolder)
            ((ValueHolder) control).setValue(postedValue);
        else if (control instanceof InternalValueHolder) {
            // get all posted data prefixed with the data table id .
            ((InternalValueHolder) control).setInternalInputValues(getInternalParameters(getInterlPostedDataKeys(control.getId())));
        }
    }

    /**
     * finds the cause of the post back, and fires the server events for him.
     *
     * @throws Exception
     */
    protected void wireEventHandelers() throws Exception {
        // if there is a master page, fire its events first.
        if (this instanceof ContentPage)
            ((ContentPage) this).getMasterPage().wireEventHandelers();
        String invoker = getPostBackData(Form.Invoker);
        String eventName = getPostBackData(Form.EventName);
        String eventArgs = getPostBackData(Form.EventArgs);
        String eventType = getPostBackData(Form.EventType);

        if (!StringUtility.isNullOrEmpty(invoker) && !StringUtility.isNullOrEmpty(eventName)) {
            WebControl invokerControl = getControl(invoker);
            if (invokerControl == null && this instanceof MasterPage) {
                if (((MasterPage) this).contentPage != null) {
                    invokerControl = ((MasterPage) this).contentPage.getControl(invoker);
                }
            }
            if (invokerControl != null && !invokerControl.isAccessible()) {
                logger.error("The Control [" + invoker + "] is firing event [" + eventName + "] while it is accessable for roles ["
                        + invokerControl.getAllowedRoles() + "] only");
                return;
            }
            if (eventType.equalsIgnoreCase("internal")) {
                // TO-DO here check if the invokerControl is null, look into
                // the Collection -- done
                if (invokerControl == null)
                    invokerControl = internalEventsControls.get(invoker);
                if (invokerControl != null) {
                    ((InternalEventsListener) invokerControl).setInteralPostback(true);
                    if (invokerControl instanceof IAjaxSubmitter)
                        ((IAjaxSubmitter) invokerControl).setOnRender("");
                    PropertyAccessor.invokeWebContorlMethod(invokerControl, invokerControl.getClass(), eventName, eventArgs);
                }
            } else {
                PropertyAccessor.invokePageMethod(this, getClass(), eventName, invokerControl, eventArgs);
            }
        }
    }

    /**
     * populates this instance with the controls in the passed page. it is used while getting cached parsed page from the caching
     * management.
     *
     * @param page
     */
    public void populateWith(Page page) {
        codeBehinde = page.codeBehinde;
        accessType = page.accessType;
        title = page.title;
        allowedRoles = page.allowedRoles;
        deniedRoles = page.deniedRoles;
        for (int i = 0; i < page.controls.size(); i++)
            controls.add(page.controls.get(i).clone(null, this, null, null));
    }

    public void addControl(WebControl control) {
        control.setPage(this);
        controls.add(control);
    }

    public void addUndeclaredControl(WebControl control) {
        undeclaredControls.put(control.getId(), control);
    }

    protected void renderPage() {
        if (this instanceof ContentPage)
            ((ContentPage) this).getMasterPage().renderPage();
        else
            renderContent();
    }

    /**
     * renders the content controls of this page.
     */
    protected void renderContent() {
        try {
            response.setCharacterEncoding(getCharSet());
            RenderPrinter outputStream = new RenderPrinter(response.getWriter());
            if (isDispatched) {
                response.addHeader(RequestHandler.PageStatusHeader, DispatchedPage);
            }
            if (isAjaxPostBack) {
                // this is ajax Call, render only the content of the Active Ajax
                // Submitter.
                renderActiveAjaxSubmitter(outputStream);
                return;
            }
            // [Jun 25, 2009 10:44:36 AM] [amr.eladawy] [if this a master, then
            // check if the content has ajax call or not]
            if (this instanceof MasterPage) {
                if (((MasterPage) this).getContentPage().isAjaxPostBack) {
                    ((MasterPage) this).getContentPage().renderActiveAjaxSubmitter(outputStream);
                    return;
                }
                if (!((MasterPage) this).getContentPage().portletPages.isEmpty()) {
                    for (PortletPage portletPage : ((MasterPage) this).getContentPage().portletPages)
                        if (portletPage.isAjaxPostBack) {
                            portletPage.renderActiveAjaxSubmitter(outputStream);
                            return;
                        }
                }
            }
            if (!portletPages.isEmpty()) {
                for (PortletPage portletPage : portletPages)
                    if (portletPage.isAjaxPostBack) {
                        portletPage.renderActiveAjaxSubmitter(outputStream);
                        return;
                    }
            }
            {
                outputStream.write(headerText);
                for (WebControl control : controls)
                    control.render(outputStream);
                outputStream.write(footerText);
            }
        } catch (Exception e) {
            if (e instanceof JspxException)
                throw (JspxException) e;
            logger.warn("renderContent() - exception ignored", e);
        }
    }

    protected void renderActiveAjaxSubmitter(RenderPrinter outputStream) throws Exception {
        if (activeAjaxSubmitter != null) {
            // get the view state for the ajax panel.
            String vs = ((WebControl) activeAjaxSubmitter).getPage().getViewStateString();
            response.addHeader(RequestHandler.AjaxPanelViewStateHeader, vs);
            response.addHeader(RequestHandler.AjaxPanelHeader, (activeAjaxSubmitter).getAjaxId());
            if (!StringUtility.isNullOrEmpty((activeAjaxSubmitter).getOnRender()))
                response.addHeader(RequestHandler.AjaxPanelRenderedHeader, (activeAjaxSubmitter).getOnRender());
            if (!isMultiPartForm && activeAjaxSubmitter instanceof AjaxPanel) {
                String refreshScript = ((AjaxPanel) activeAjaxSubmitter).getAutoRefreshScript();
                if (!StringUtility.isNullOrEmpty(refreshScript))
                    response.addHeader(RequestHandler.AjaxPanelAutoRefreshHeader, refreshScript);

            }
            activeAjaxSubmitter.renderChildren(outputStream);
        }

        // [Aug 23, 2013 9:44:38 PM] [Amr.ElAdawy] [check ajax request on all
        // portlet]
        for (PortletPage portletPage : portletPages)
            portletPage.renderActiveAjaxSubmitter(outputStream);
        // [Oct 8, 2013 1:44:25 PM] [Amr.ElAdawy] [dont render the onload script if this is ajax panel and multi part as it will
        // be called from the Ajax Panel]
        if (!(isMultiPartForm && activeAjaxSubmitter instanceof AjaxPanel))
            renderOnloadScripts(outputStream);
        // // [May 28, 2012 6:58:23 PM] [amr.eladawy] [render the messages of
        // this page]
        // renderMessages(outputStream);
    }

    public void renderOnloadScripts(RenderPrinter outputStream) throws Exception {
        if (validationScript != null)
            validationScript.render(outputStream);
        if (this instanceof ContentPage && ((ContentPage) this).getMasterPage().validationScript != null)
            ((ContentPage) this).getMasterPage().validationScript.render(outputStream);
        if (!isOnLoadScriptRendered) {
            if (onloadScript.length() != 0) {
                Script script = new Script(this);
                script.setScriptCode("jQuery( function($) {$(document).ready(function (){" + onloadScript + "});" + "} );");
                script.render(outputStream);
            }
            isOnLoadScriptRendered = true;
            // [May 28, 2012 6:57:51 PM] [amr.eladawy] [render the messages
            // script]
            renderMessages(outputStream);
            renderClientMethods(outputStream);
            if (this instanceof MasterPage)
                ((MasterPage) this).getContentPage().renderOnloadScripts(outputStream);
            else if (this instanceof ContentPage)
                ((ContentPage) this).getMasterPage().renderOnloadScripts(outputStream);
        }
    }

    /**
     * Renders the JspxMessages in this page.
     *
     * @param outputStream
     * @throws Exception
     * @author amr.eladawy May 28, 2012 6:54:24 PM
     */
    private void renderMessages(RenderPrinter outputStream) throws Exception {
        if (messages.isEmpty())
            return;
        StringBuffer sb = new StringBuffer();
        for (Iterator<JspxMessage> mi = messages.iterator(); mi.hasNext(); ) {
            JspxMessage message = mi.next();
            message.setPage(this);
            sb.append(message);
            mi.remove();
        }
        Script script = new Script(this);
        script.setScriptCode(sb.toString());
        script.render(outputStream);
    }

    /**
     * Renders the JspxMessages in this page.
     *
     * @param outputStream
     * @throws Exception
     * @author amr.eladawy May 28, 2012 6:54:24 PM
     */
    private void renderClientMethods(RenderPrinter outputStream) throws Exception {
        if (clientMethods.isEmpty())
            return;
        StringBuffer sb = new StringBuffer();
        for (Iterator<String> cm = clientMethods.iterator(); cm.hasNext(); ) {
            String name = cm.next();
            sb.append("function ");
            sb.append(name);
            sb.append("(invokerId,args,submitterId,ajaxSubmitterId){"
                    + " if(ajaxSubmitterId == null ||ajaxSubmitterId =='')ajaxSubmitterId = 'JSPX_HIDDEN_AJAX_PANEL'; JSPX_INVOKE_SERVER_METHOD('");
            sb.append(name);
            sb.append("',invokerId,args,submitterId,ajaxSubmitterId);}");
            cm.remove();
        }
        Script script = new Script(this);
        script.setScriptCode(sb.toString());
        script.render(outputStream);
    }

    public void addDeclaredControl(WebControl control) {
        declaredControls.add(control);
    }

    public WebControl findControl(String id, boolean recursive) {
        for (WebControl control : controls)
            if (control.getId().equals(id))
                return control;
            else if (recursive)
                return control.findControl(id, true);
        return null;
    }

    /**
     * Adds internal Events control to the internalEventsControls collection
     *
     * @param id
     * @param control
     */
    public void addInternalEventsControl(String id, WebControl control) {
        internalEventsControls.put(id, control);
    }

    /**
     * Adds internal Events control to the internalEventsControls collection
     *
     * @param id
     * @param control
     */
    public void addInternalValueHolder(String id, InternalValueHolder control) {
        internalValueHolders.put(id, control);
    }

    /**
     * Adds control to the valueHolderControlls collection
     *
     * @param id
     * @param control
     */
    public void addValueHolder(String id, WebControl control) {
        valueHolderControls.put(id, control);
        valueHolderControlKeys.add(id);
    }

    /**
     * Adds Item Data Bound control to the itemDataBoundControls collection
     *
     * @param id
     * @param control
     */
    public void addItemDataBoundControl(String id, WebControl control) {
        itemDataBoundControls.put(id, control);
    }

    /**
     * Adds item Data Bound Child Control to the itemDataBoundChildControls collection
     *
     * @param id
     * @param control
     */
    public void addItemDataBoundChildControl(String id, WebControl control) {
        itemDataBoundChildControls.put(id, control);
    }

    /**
     * Adds Resource Bundle Control to the resource bundles collection
     *
     * @param id
     * @param control
     */
    public void addResourceBundle(String id, ResourceBundle control) {
        bundles.put(id, control);
        // [Jul 15, 2013 6:39:00 PM] [Amr.ElAdawy] [add the bundle to this page
        // and to the child content in case this is a
        // master.]
        pageJEXLContext.set(id, control.getBundle());
    }

    public ResourceBundle getBundle(String id) {
        ResourceBundle resourceBundle = bundles.get(id);
        if (resourceBundle == null && this instanceof ContentPage && ((ContentPage) this).getMasterPage() != null)
            resourceBundle = ((ContentPage) this).getMasterPage().getBundle(id);
        return resourceBundle;
    }

    public void addAjaxSubmitter(String id, IAjaxSubmitter ajaxSubmitter) {
        ajaxSubmitters.put(id, ajaxSubmitter);
    }

    public IAjaxSubmitter getAjaxSubmitter(String id) {
        return ajaxSubmitters.get(id);
    }

    /**
     * Gets undeclared control
     * [6 Jul 2015 09:08:46]
     *
     * @param id
     * @return
     * @throws IllegalAccessException
     * @author aeladawy
     */
    public WebControl getUndeclaredControl(String id) throws IllegalAccessException {
        return undeclaredControls.get(id);
    }

    /**
     * gets a declared Control by its ID
     *
     * @param id
     * @return
     * @throws IllegalAccessException
     */
    public WebControl getControl(String id) throws IllegalAccessException {
        try {
            // search in value holders collection
            for (WebControl cont : valueHolderControls.values()) {
                if (cont.getId().equals(id))
                    return cont;
            }
            for (WebControl cont : itemDataBoundChildControls.values()) {
                if (cont.getId().equals(id))
                    return cont;
            }
            // [Sep 1, 2009 5:43:31 PM] [amr.eladawy] [checking on the control
            // in the annotated controls]
            Field field = annotatedWebControls.get(id);
            if (field != null) {
                field.setAccessible(true);
                return (WebControl) field.get(this);
            }
            return (WebControl) PropertyAccessor.getProperty(this, id);
        } catch (Exception e) {
            logger.error(
                    "Cannot find a control with ID [" + id + "], please make sure you that this ID is the same on JSPX page and in the controller");
            return null;
        }
    }

    public void setControl(String id, WebControl control) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        Field field = annotatedWebControls.get(id);
        if (field != null) {
            field.setAccessible(true);
            field.set(this, control);
        } else
            PropertyAccessor.setProperty(this, id, control);
    }

    /**
     * gets the String format representation of the view state.
     *
     * @return
     */
    public String getViewStateString() {
        return StringUtility.join(viewState, Render.mostOuterJoiner, Render.innerJoiner, Render.outerJoiner, Render.mostInnerJoiner);
    }

    /**
     * whenever a validator need to add his control to the page validator, it call this method to add that control.
     *
     * @param controlId
     * @param group
     * @param validationType
     * @param validator      TODO
     */
    public void addControlToValidation(String controlId, String group, String validationType, Validator validator) {
        if (validationScript == null) {
            validationScript = new ValidationScript();
            validationScript.setPage(this);
        }
        validationScript.addControlToValidate(controlId, group, validationType, validator);
    }

    /**
     * Adds a Validator to the collection of the validators.
     *
     * @param validator
     */
    public void addValidator(IValidate validator) {
        validators.add(validator);
    }

    /**
     * finds the names of the posted data that is related to a certain control. controls like DataControl, has complex internal
     * input data. this method searches for such parameters. for example, control like capatcha has id cap1 will have parameter
     * named "cap1$$1234567"in postback, this method find this name.
     *
     * @param controlId
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<String> getInterlPostedDataKeys(String controlId) {
        List<String> ipdk = new ArrayList<String>();
        if (isMultiPartForm) {
            for (String string : fileItemsMap.keySet()) {
                if (string.startsWith(controlId))
                    ipdk.add(string);
            }
        } else {
            Enumeration<String> i = request.getParameterNames();
            while (i.hasMoreElements()) {
                String string = i.nextElement();
                if (string.startsWith(controlId))
                    ipdk.add(string);
            }
        }
        return ipdk;
    }

    /**
     * returns a hashtable indexed by the keys given along with values in the posted request.
     *
     * @param keys
     * @return
     */
    public Hashtable<String, String> getInternalParameters(List<String> keys) {
        Hashtable<String, String> params = new Hashtable<String, String>();
        for (String key : keys)
            params.put(key, getPostBackData(key));
        return params;
    }

    /**
     * sets the attributes of the page.
     *
     * @param attributes
     */
    public void setPageProperties(List<Attribute> attributes) {
        Attribute att = null;
        for (int i = 0; i < attributes.size(); i++) {
            att = attributes.get(i);
            if (att.getKey().equalsIgnoreCase(Controller))
                codeBehinde = att.getValue().trim();
            else if (att.getKey().equalsIgnoreCase(MasterKey) && this instanceof ContentPage)
                ((ContentPage) this).setMaster(att.getValue().trim());
            else if (att.getKey().equalsIgnoreCase(TitleKey))
                title = att.getValue();
            else if (att.getKey().equalsIgnoreCase(AccessTypeKey))
                accessType = att.getValue();
            else if (att.getKey().equalsIgnoreCase(AllowedRolesKey))
                allowedRoles = att.getValue();
            else if (att.getKey().equalsIgnoreCase(DeniedRolesKey))
                deniedRoles = att.getValue();
        }
    }

    public List<WebControl> getControls() {
        return controls;
    }

    public String getCharSet() {
        return charSet;
    }

    public void setCharSet(String cSet) {
        charSet = cSet;
    }

    /**
     * The Basic members are loaded ,but the page model still empty.
     */
    protected void pageInit() {
    }

    /**
     * The Page model is loaded .
     */
    protected void pageLoaded() {
    }

    /**
     * before validating posted back data
     */
    protected void preValidate() {

    }

    /**
     * after validating posted back data
     */
    protected void postValidation() {

    }

    /**
     * The post back events were fired. now the response is rendered
     */
    protected void pagePreRender() {
    }

    /**
     * The page has been rendered to the output.
     */
    protected void pageRendered() {
    }

    /**
     * The Page is dispatched
     */
    protected void pageDispatched() {
    }

    public String getHeaderText() {
        return headerText;
    }

    public void setHeaderText(String headerText) {
        this.headerText = headerText;
    }

    public String getFooterText() {
        return footerText;
    }

    public void setFooterText(String footerText) {
        this.footerText = footerText;
    }

    public int getPageStatus() {
        return PageStatus;
    }

    /**
     * sets the status of this page and any other related page with the given status.
     *
     * @param status
     */
    protected void setPageStatus(int status) {
        PageStatus = status;
        if (this instanceof ContentPage)
            ((ContentPage) this).getMasterPage().setPageStatus(status);
    }

    public Page getSender() {
        return sender;
    }

    public void setSender(Page sender) {
        this.sender = sender;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        session.setAttribute("user-lang", locale.toString());
        this.locale = locale;
        reloadBundles();
    }

    public void setInitialLocale(Locale locale) {
        session.setAttribute("user-lang", locale.toString());
        this.locale = locale;
    }

    public String getPageURL() {
        return pageURL;
    }

    public void setPageURL(String pageURL) {
        this.pageURL = pageURL;
    }

    public ServletContext getContext() {
        return context;
    }

    public void setContext(ServletContext context) {
        this.context = context;
    }

    public Object getSenderAttribute(String attName) {
        return PropertyAccessor.getProperty(getSender(), attName);
    }

    public WebControl getDataModelChild(String controlId) {
        return itemDataBoundChildControls.get(controlId);
    }

    public void reloadBundles() {

        for (ResourceBundle bundle : bundles.values()) {
            bundle.reloadBundle();
        }
    }

    protected void localizeMasterPage() {
        try {
            if (this instanceof MasterPage) {
                MasterPage newInstance = getLocalizedMaster(filePath);
                ((MasterPage) this).getContentPage().setMasterPage(newInstance);
                ((MasterPage) this).getContentPage().setLocale(this.getLocale());
            } else {
                MasterPage newInstance = getLocalizedMaster(((ContentPage) this).getMaster());
                ((ContentPage) this).setMasterPage(newInstance);
                this.setLocale(this.getLocale());
            }
        } catch (Exception e) {
        }
    }

    private MasterPage getLocalizedMaster(String masterPage) throws Exception {
        MasterPage master = (MasterPage) PageModelComposer.getPage(masterPage, context, locale.getLanguage(), true, charSet, false);
        MasterPage newInstance = (MasterPage) RequestHandler.clonePage(master, request, response, false, context);
        newInstance.setContext(context);
        newInstance.setPageURL(request.getContextPath() + masterPage);
        return newInstance;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * ** check if the controller has session jspxBeans: <br />
     * - get them from session by their name and inject them in the controller variables <br />
     * - if the bean don't exist in session create a new one and save it in session and in the controller check if the controller
     * has request jspxBeans: <br />
     * - get them from request by their name and inject them in the controller variables <br />
     * - if donot exist in request create new one and store it the JspxRequestBeans collection in the page and in the controller
     */
    protected void initializeJspxBeans(boolean initialize) {
        for (Field field : annotatedBeans.values()) {
            try {
                field.setAccessible(true);
                JspxBean bean = field.getAnnotation(JspxBean.class);
                Object fieldValue = field.get(this);
                String beanName = bean.name();
                Class fieldClass = field.getType();
                String fieldName = field.getName();
                JspxBeanValue value = new JspxBeanValue(bean, fieldValue);
                if (bean.scope() == JspxBean.REQUEST) {
                    if (initialize)
                        value.setValue(getRequestJspxBean(beanName, fieldValue, fieldClass));
                    saveRequestJspxBeans(beanName, value);
                } else if (bean.scope() == JspxBean.SESSION) {
                    if (initialize)
                        value.setValue(getSessionJspxBean(beanName, fieldValue, fieldClass));
                    saveSessionJspxBeans(beanName, value);
                } else if (bean.scope() == JspxBean.CONVERSATION) {
                    if (initialize)
                        value.setValue(getConversaitonJspxBean(beanName, fieldValue, fieldClass));
                    saveConversationJspxBeans(beanName, value);

                }
                // [Sep 13, 2009 1:14:34 AM] [amr.eladawy] [now set the page
                // bean property]
                if (value != null && value.getValue() != null)
                    field.set(this, value.getValue());

                // [Jul 16, 2013 10:30:43 AM] [Amr.ElAdawy] [add the bean to the
                // JEXL context.]
                pageJEXLContext.set(beanName, value.getValue());
            } catch (Exception e) {
                logger.error("initializeJspxBeans(initialize=" + initialize + ")", e);
            }
        }
    }

    protected void saveRequestJspxBeans(String beanName, JspxBeanValue value) {
        if (!StringUtility.isNullOrEmpty(beanName) && value != null) {
            // requestJspxBeans.put(beanName, fieldValue);
            request.setAttribute(JspxBeanNamePrefix + beanName, value);
        }
    }

    protected Object getRequestJspxBean(String beanName, Object fieldValue, Class fieldClass) {
        Object requestValue = request.getAttribute(JspxBeanNamePrefix + beanName);
        if (requestValue == null || ((JspxBeanValue) requestValue).getValue() == null) {
            // fieldValue = ((JspxBeanValue) requestValue).getValue();
            // if the value in the controller is also null, then create a new
            // instance
            if (fieldValue == null) {
                try {
                    fieldValue = fieldClass.newInstance();
                } catch (Exception e) {
                }
            }
        } else {
            fieldValue = ((JspxBeanValue) requestValue).getValue();
        }
        return fieldValue;
    }

    protected void saveSessionJspxBeans(String beanName, JspxBeanValue value) {
        if (!StringUtility.isNullOrEmpty(beanName) && value != null) {
            session.setAttribute(JspxBeanNamePrefix + beanName, value);
        }
    }

    protected Object getSessionJspxBean(String beanName, Object fieldValue, Class fieldClass) {
        Object sessionValue = session.getAttribute(JspxBeanNamePrefix + beanName);
        if (sessionValue == null || ((JspxBeanValue) sessionValue).getValue() == null) {
            // fieldValue = ((JspxBeanValue) sessionValue).getValue();

            // if the value in the controller is also null, then create a new
            // instance
            if (fieldValue == null) {
                try {
                    fieldValue = fieldClass.newInstance();
                } catch (Exception e) {
                }
            }
        } else {
            fieldValue = ((JspxBeanValue) sessionValue).getValue();
        }
        return fieldValue;
    }

    protected void saveConversationJspxBeans(String beanName, JspxBeanValue value) {
        if (!StringUtility.isNullOrEmpty(beanName) && value != null) {
            session.setAttribute(JspxBeanNamePrefix + conversationId + beanName, value);
        }
    }

    protected Object getConversaitonJspxBean(String beanName, Object fieldValue, Class fieldClass) {
        Object sessionValue = session.getAttribute(JspxBeanNamePrefix + conversationId + beanName);
        if (sessionValue == null || ((JspxBeanValue) sessionValue).getValue() == null) {

            // if the value in the controller is also null, then create a new
            // instance
            if (fieldValue == null) {
                try {
                    fieldValue = fieldClass.newInstance();
                } catch (Exception e) {
                }
            }
        } else {
            fieldValue = ((JspxBeanValue) sessionValue).getValue();
        }
        return fieldValue;
    }

    protected void loadJspxBeanData() {
        // for (WebControl control : valueHolderControls.values()){
        for (String key : valueHolderControlKeys) {
            WebControl control = valueHolderControls.get(key);
            try {
                if (control instanceof Label || !control.isIncluded() || !control.isFinallyRendered())
                    continue;
                String valueBinding = ((ValueHolder) control).getValueBinding();
                loadJspxValue(valueBinding, control);
            } catch (Exception e) {

            }
        }
    }

    protected void loadJspxValue(String bind, WebControl control) {
        if (!StringUtility.isEL(bind))
            return;
        int index = bind.indexOf('.');
        String beanName = bind.substring(2, index).trim();
        String propertyName = bind.substring(index + 1, bind.length() - 1).trim();
        JspxBeanValue jspxBeanValue = getJspxBeanByName(beanName);
        if (jspxBeanValue != null) {
            setJspxBeanValue(control, propertyName, jspxBeanValue);
        }
    }

    protected void setJspxBeanValue(WebControl control, String propertyName, JspxBeanValue jspxBeanValue) {
        Object object = jspxBeanValue.getValue();
        if (control instanceof FileUpload) {
            try {
                loadFile((FileUpload) control, getFileItem(control.getName()));
                PropertyAccessor.setProperty(object, propertyName, ((FileUpload) control).getFileItem());
            } catch (Exception e) {
            }
        } else {
            String value = getPostBackData(control.getId());
            try {
                if (control instanceof Calendar) {
                    PropertyAccessor.setProperty(object, propertyName, value, ((Calendar) control).getDateFormat());
                } else if (control instanceof Select) {
                    if (!((Select) control).bindValueToObject(object, value)) {
                        PropertyAccessor.setProperty(object, propertyName, value);
                    }
                }
                // [Feb 22, 2011 8:48:30 AM] [Amr.ElAdawy] [fix the binding from
                // checkbox to jspxbean]
                else if (control instanceof CheckBox && !(control instanceof RadioButton)) {
                    PropertyAccessor.setProperty(object, propertyName, value != null);
                } else
                    PropertyAccessor.setProperty(object, propertyName, value);
            } catch (Exception e) {
                logger.error("e", e);
            }
        }
    }

    public JspxBeanValue getJspxBeanByName(String beanName) {
        for (Field field : annotatedBeans.values()) {
            try {
                field.setAccessible(true);
                JspxBean bean = field.getAnnotation(JspxBean.class);
                if (bean.name().equals(beanName)) {
                    if (bean.scope() == JspxBean.SESSION)
                        return (JspxBeanValue) session.getAttribute(JspxBeanNamePrefix + beanName);
                    else if (bean.scope() == JspxBean.REQUEST)
                        return (JspxBeanValue) request.getAttribute(JspxBeanNamePrefix + beanName);
                    else if (bean.scope() == JspxBean.CONVERSATION)
                        return (JspxBeanValue) session.getAttribute(JspxBeanNamePrefix + conversationId + beanName);

                }
            } catch (Exception e) {
                logger.error("getJspxBeanByName(beanName=" + beanName + ")", e);
            }
        }
        return null;
    }

    protected void clearJspxBean(String jspxBeanName) {
        int scope = getJspxBeanByName(jspxBeanName).getJspxBean().scope();
        if (scope == JspxBean.SESSION)
            session.removeAttribute(JspxBeanNamePrefix + jspxBeanName);
        if (scope == JspxBean.CONVERSATION)
            session.removeAttribute(JspxBeanNamePrefix + conversationId + jspxBeanName);
    }

    protected void clearExpiredBeans(int time) {
        if (request.getMethod().equalsIgnoreCase("GET"))
            clearExpiredJspxBean(time);
    }

    /**
     * clears the beans that have not been accessed since the time specified (in minutes).
     *
     * @param time in minutes
     * @Author Amr.ElAdawy Nov 8, 2013 9:39:37 PM
     */
    public void clearExpiredJspxBean(long time) {
        try {
            @SuppressWarnings("unchecked")
            Enumeration<String> names = session.getAttributeNames();
            // logger.debug(names.(type[]) collection.toArray(new
            // type[collection.size()]))
            while (names.hasMoreElements()) {
                String jspxBeanName = names.nextElement();
                logger.debug("try to clean " + jspxBeanName);
                JspxBeanValue beanvalue = null;
                try {
                    if (jspxBeanName.startsWith(JspxBeanNamePrefix)) {
                        beanvalue = (JspxBeanValue) session.getAttribute(jspxBeanName);
                        // if (beanvalue.getJspxBean().scope() == scope)
                        // {
                        System.currentTimeMillis();

                        long diff = System.currentTimeMillis() - beanvalue.getJspxBeanLastAccess();

                        if ((diff / (1000 * 60)) >= time) {
                            logger.info("removing Bean {} from session", jspxBeanName);
                            session.removeAttribute(jspxBeanName);
                            logger.debug(jspxBeanName + " is removed");
                        }
                        // }
                    }
                } catch (Exception e) {
                    logger.error("cannot covert session value to JspxBeanValue");
                }
            }
        } catch (Exception e) {
            logger.error("clearExpiredJspxBean(time=" + time + ")", e);
        }
    }

    protected void modifyJspxBean(String jspxBeanName, Object object) {
        JspxBeanValue jspxBeanValue = getJspxBeanByName(jspxBeanName);
        jspxBeanValue.setValue(object);
        if (jspxBeanValue.getJspxBean().scope() == JspxBean.REQUEST)
            saveRequestJspxBeans(jspxBeanName, jspxBeanValue);
        else if (jspxBeanValue.getJspxBean().scope() == JspxBean.SESSION)
            saveSessionJspxBeans(jspxBeanName, jspxBeanValue);
        else if (jspxBeanValue.getJspxBean().scope() == JspxBean.CONVERSATION)
            saveConversationJspxBeans(jspxBeanName, jspxBeanValue);
    }

    public Head getHead() {
        return head;
    }

    public void setHead(Head head) {
        this.head = head;
    }

    /**
     * writes a file on the output stream to the user.
     *
     * @param file     the file data as a byte [].
     * @param fileName the name of the file that is suggested for saving.
     */
    public void writeFile(byte[] file, String fileName, String mimeType) {
        try {
            this.response.setHeader("Content-disposition", "attachment; filename=" + fileName);
            this.response.setContentType(mimeType);
            // [May 23, 2010 4:51:08 PM] [Amr.ElAdawy] [removing the caching
            // control headers
            // in order to solve the https problem]
            this.response.setHeader("Pragma", null);
            this.response.setHeader("Cache-Control", null);
            OutputStream out = this.response.getOutputStream();
            out.write(file);
            out.flush();
            out.close();
        } catch (IOException e) {
            logger.error("writeFile(file=" + file + ", fileName=" + fileName + ", mimeType=" + mimeType + ")", e);
        } catch (Exception e) {
            logger.error("Excpetion while writing file file ", e);
        }
        this.skip();
    }

    /**
     * loads jspx portlet page from a file
     *
     * @param path the path of the jspx portlet file relative to the webroot folder
     * @return
     * @throws Exception
     */
    public PortletPage loadPortletPage(String jspxFileName) throws Exception {
        Page userPage = PageModelComposer.composePage(jspxFileName, context, locale.getLanguage(), charSet, true);
        Page newInstance = RequestHandler.clonePage(userPage, request, response, isPostBack, context);
        newInstance.setContext(context);
        newInstance.setPageURL(getPageURL());
        newInstance.setCharSet(charSet);

        if (!StringUtility.isNullOrEmpty(userPage.codeBehinde) && !(userPage instanceof PortletPage)) {
            logger.error("Controller [" + userPage.getClass().getName() + "] Is not found or not of type PortletPage");
            return new PortletPage();
        }

        return (PortletPage) newInstance;
    }

    public void addPortletPage(PortletPage userPage) {
        portletPages.add(userPage);
    }

    public Title getTitleControl() {
        return titleControl;
    }

    public void setTitleControl(Title titleControl) {
        this.titleControl = titleControl;
    }

    public String getTitle() {
        return titleControl.getValue();
    }

    public void setTitle(String title) {
        this.titleControl.setValue(title);
    }

    /**
     * searches for annotated controls within the page.
     */
    public void loadAnnotatedWebControls() {
        Field[] allFields = getClass().getDeclaredFields();
        Field field;
        for (int i = 0; i < allFields.length; i++) {
            field = allFields[i];
            JspxWebControl jspxWebControl = field.getAnnotation(JspxWebControl.class);
            if (jspxWebControl != null) {
                String name = jspxWebControl.name();
                if (!StringUtility.isNullOrEmpty(name))
                    annotatedWebControls.put(name, field);
                else
                    annotatedWebControls.put(field.getName(), field);
            }
        }
    }

    /**
     * searches for annotated JSPX Bean within the page.
     */
    public void loadAnnotatedBeans() {
        Field[] allFields = getClass().getDeclaredFields();
        Field field;
        for (int i = 0; i < allFields.length; i++) {
            field = allFields[i];
            JspxBean jspxBean = field.getAnnotation(JspxBean.class);
            if (jspxBean != null) {
                String name = jspxBean.name();
                annotatedBeans.put(name, field);
                pageJEXLContext.set(name, field);
            }
        }
    }

    /**
     * searches for annotated Client Methods within the page.
     */
    public void loadAnnotatedClientMethods() {
        Method[] allmethods = getClass().getDeclaredMethods();
        for (Method method : allmethods) {
            JspxClientMethod clientMethod = method.getAnnotation(JspxClientMethod.class);
            if (clientMethod != null)
                clientMethods.add(method.getName());
        }
    }

    /**
     * gets a value of the jspx bean
     *
     * @param beanName
     * @return
     */
    public Object getJspxBeanValue(String beanName) {
        Field field = annotatedBeans.get(beanName);
        if (field != null) {
            field.setAccessible(true);
            try {
                return field.get(this);
            } catch (Exception e) {
                logger.error("getJspxBeanValue(beanName=" + beanName + ")", e);
            }
        }
        return null;
    }

    /**
     * sets the conversation Id for this page
     *
     * @param conversationId
     */
    public void setConversationId(String conversationId) {
        if (StringUtility.isNullOrEmpty(conversationId))
            conversationId = String.valueOf(System.currentTimeMillis());
        this.conversationId = conversationId;
        for (PortletPage userPage : portletPages)
            userPage.setConversationId(conversationId);
        if (this instanceof ContentPage)
            ((ContentPage) this).getMasterPage().setConversationId(conversationId);
    }

    /**
     * calculate the conversation id based on the status of the page.
     */
    private void defineConversationId() {
        if (getSender() != null)
            setConversationId(getSender().conversationId);
        else if (isPostBack) {
            loadMultiPartForm();
            setConversationId(getPostBackData(CONVERSATION_KEY));
        } else
            setConversationId(null);

    }

    /**
     * removes this instant of Page from the cached pages.
     */
    protected void removeMeFromCache() {
        PageModelComposer.removeChachedPage(filePath);
    }

    protected void clearAllCachedPages() {
        PageModelComposer.clearChachedPages();
    }

    /**
     * validates that the given ID is unique across this page
     *
     * @param id
     * @param tagPlace
     */
    public void validateUniqueControlID(String id, String tagPlace) {
        if (this instanceof MasterPage)
            ((MasterPage) this).getContentPage().validateUniqueControlID(id, tagPlace);
        if (uniqueID.get(id) == null)
            uniqueID.put(id, tagPlace);
        else
            throw new JspxException(
                    "Control [id=\"" + id + "\"] placed at [" + tagPlace + "] is duplicate with control placed at [" + uniqueID.get(id) + "].");
    }

    /**
     * appends the given script to be executed at document load.
     *
     * @param script
     */
    public void addOnloadScript(String script) {
        onloadScript.append(script).append("\n");
    }

    public void addMessage(JspxMessage message) {
        messages.add(message);
    }

    public MapContext getPageJEXLContext() {
        return pageJEXLContext;
    }

    /**
     * registers the given value in the Page EL Context under the given name
     *
     * @param name
     * @param value
     */
    public void registerElVariable(String name, Object value) {
        checkELName(name);
        if (this instanceof MasterPage)
            ((MasterPage) this).getContentPage().registerElVariable(name, value);
        pageJEXLContext.set(name, value);
    }

    /**
     * validates that the given name is reserved
     *
     * @param name
     * @Author Amr.ElAdawy Aug 29, 2013 5:32:33 PM
     */
    protected void checkELName(String name) {
        if (name.equals("this") || name.equals("request.parameters") || name.equals("session.parameters") || name.equals("request.attributes")
                || name.equals("request.headers") || name.equals("super"))
            throw new JspxException("[" + name + "] is a reserved keyword, please use diffrent name");
    }

    /**
     * @return the activeAjaxSubmitter
     */
    public IAjaxSubmitter getActiveAjaxSubmitter() {
        return activeAjaxSubmitter;
    }

    /**
     * Causes the target Panel to issue post back event to get updated look from the server.
     * [21 May 2015 08:37:09]
     *
     * @param panelId
     * @param eventName
     * @param args
     * @author aeladawy
     */
    public void refreshAjaxPanel(String panelId, String eventName, String args) {
        if (StringUtility.isNullOrEmpty(eventName))
            eventName = "JSPX_Refresh";
        if (StringUtility.isNullOrEmpty(args))
            args = "";
        String submitter = "null";
        WebControl wc = null;
        try {
            wc = getControl(panelId);
            if (wc != null) {
                WebControl wcSumbit = (WebControl) wc.getMySubmitter();
                if (wcSumbit != null)
                    submitter = wcSumbit.getId();
            }
        } catch (Exception e) {
        }
        String s = "postBack('" + panelId + "','" + eventName + "','" + submitter + "','" +
                panelId + "',false,'" + args + "','');";
        response.setHeader(RequestHandler.AjaxPanelRefreshHeader, s);
    }

}
