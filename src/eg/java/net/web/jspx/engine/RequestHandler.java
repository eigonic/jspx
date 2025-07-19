package eg.java.net.web.jspx.engine;

import eg.java.net.web.jspx.engine.error.JspxException;
import eg.java.net.web.jspx.engine.error.PageNotAccessibleException;
import eg.java.net.web.jspx.engine.error.PageNotFoundException;
import eg.java.net.web.jspx.engine.jmx.JspxAdmin;
import eg.java.net.web.jspx.engine.parser.PageModelComposer;
import eg.java.net.web.jspx.engine.util.bean.JMXBeanUtil;
import eg.java.net.web.jspx.ui.pages.ContentPage;
import eg.java.net.web.jspx.ui.pages.MasterPage;
import eg.java.net.web.jspx.ui.pages.Page;
import org.kxml.io.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.Locale;

/**
 * Servlet implementation class for Servlet: RequestHandeler
 */
public class RequestHandler extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
    public static final String JSPXVersion = "2.1";
    public static final String PoweredByHeader = "UI-X-PoweredBy";
    public static final String AjaxPanelHeader = "JSPX-AjaxPanel";
    public static final String AjaxPanelViewStateHeader = "JSPX-AjaxPanel-ViewState";
    /**
     * the Auto refresh script is saved in the response header under the following name.
     */
    public static final String AjaxPanelAutoRefreshHeader = "JSPX-AjaxPanel-Auto-Refresh";
    /**
     * The refresh script is saved in the response header under the following name.
     */
    public static final String AjaxPanelRefreshHeader = "JSPX-AjaxPanel-Refresh";
    public static final String AjaxPanelRenderedHeader = "JSPX-AjaxPanel-Rendered";
    public static final String APP_Ajax_RenderedScript = "APP-Ajax-RenderedScript";
    public static final String PageStatusHeader = "JSPX-Page-Status";
    public static final String AjaxPageLocation = "JSPX-Page-Location";
    public static final String PageAttribute = "JSPX-Page";
    public static final String OriginPageAttribute = "JSPX-Origin-Page";
    public static final String JSPX_USER_ROLES = "JSPX_USER_ROLES";
    public static final Date STARTED_AT = new Date();
    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    /**
     * boolean to use JEXL or JSPX EL Resolver.
     */
    public static boolean USE_JEXL = true;

    public static boolean THEME_TWITTER = true;
    public static boolean SHOW_SQL = true;
    public static int JSPX_BEAN_EXPIRATION_PERIOD_MINUTES = 20;
    public static String JSPX_FILES_PATH = "";
    protected static String charSet = "UTF-8";

    /**
     * starts serving the request by getting the corresponding page controller.
     *
     * @param request
     * @param response
     * @param post
     * @param pagePath
     * @param context
     * @param sender   if this call is a dispatch call, the sender page will not be a null.
     * @throws Exception
     */
    public static void start(HttpServletRequest request, HttpServletResponse response, boolean post, String pagePath, ServletContext context,
                             Page sender) throws Exception {
        long startTime = System.currentTimeMillis();

        response.setContentType("text/html");

        if (request.getAttribute("char_set") != null)
            charSet = (String) request.getAttribute("char_set");
        // 1- get the compiled page.
        // 2- create new instance
        // 3- populate the new instance with the cached page.
        String lang = (String) request.getSession().getAttribute("user-lang");
        Page composedPage = PageModelComposer.composePage(pagePath, context, lang, charSet, false);
        Page newInstance = clonePage(composedPage, request, response, post, context);

        // ///////////////////////////////////////////////////////////////
        // here we check for the sender page, if not null, this is a dispatch.
        if (sender != null)
            newInstance.setSender(sender);

        newInstance.setContext(context);
        newInstance.setPageURL(request.getContextPath() + pagePath);
        newInstance.setCharSet(charSet);
        // check for the master page.
        if (newInstance instanceof ContentPage) {
            MasterPage newMasterPage = (MasterPage) clonePage(((ContentPage) composedPage).getMasterPage(), request, response, post, context);
            newMasterPage.setContext(context);
            newMasterPage.setPageURL(request.getContextPath() + pagePath);
            newMasterPage.setCharSet(charSet);
            ((ContentPage) newInstance).setMasterPage(newMasterPage);
        }
        response.addHeader(PoweredByHeader, "BAY JSPX " + JSPXVersion);

        request.setAttribute(PageAttribute, newInstance);

        newInstance.continueServing();

        logger.debug("[" + request.getRequestURL() + "]jspx Trip Time= " + (System.currentTimeMillis() - startTime) + "ms");
    }

    /**
     * creates new page cloned from the given one.
     *
     * @param source
     * @param request
     * @param response
     * @param post
     * @param context  TODO
     * @return
     * @throws Exception
     */
    public static Page clonePage(Page source, HttpServletRequest request, HttpServletResponse response, boolean post, ServletContext context)
            throws Exception {
        Page newInstance = source.getClass().newInstance();
        newInstance.setContext(context);
        newInstance.request = request;
        newInstance.response = response;
        newInstance.isPostBack = post;
        newInstance.session = request.getSession();
        newInstance.setCharSet(source.getCharSet());
        newInstance.loadQueryString(request);
        newInstance.loadAnnotatedWebControls();
        newInstance.populateWith(source);
        newInstance.setFilePath(source.getFilePath());
        if (newInstance.session.getAttribute("user-lang") != null)
            newInstance.setInitialLocale(new Locale(newInstance.session.getAttribute("user-lang").toString()));

        newInstance.setHeaderText(source.getHeaderText());
        newInstance.setFooterText(source.getFooterText());

        return newInstance;
    }

    public static String getCharSet() {
        return charSet;
    }

    public static void setCharSet(String charSet) {
        RequestHandler.charSet = charSet;
    }

    public static String getJspxversion() {
        return JSPXVersion;
    }

    public void destroy() {
        super.destroy();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            start(request, response, false, request.getServletPath(), getServletContext(), null);
        } catch (Exception e) {
            if (e instanceof InvocationTargetException)
                logger.error("doGet()", e.getCause());
            else
                logger.error("doGet()", e);
            handleException(request, response, e);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            start(request, response, true, request.getServletPath(), getServletContext(), null);
        } catch (Exception e) {
            if (e instanceof InvocationTargetException)
                logger.error("doPost()", e.getCause());
            else
                logger.error("doPost()", e);
            handleException(request, response, e);
        }
    }

    /**
     * Handling thrown exceptions from the framework and user code.
     *
     * @param e
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    protected void handleException(HttpServletRequest request, HttpServletResponse response, Exception e) throws ServletException, IOException {
        if (e instanceof ParseException)
            throw new ServletException("JSPX Error: Failed to parse the model : " + e.getMessage(), e);
        if (e instanceof JspxException)
            throw new ServletException("JSPX Error: " + e.getMessage(), e);
        else if (e instanceof IllegalAccessException)
            throw new ServletException(e);
        else if (e instanceof PageNotFoundException)
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "JSPX Error: Page " + e.getMessage() + " was not found.");
        else if (e instanceof PageNotAccessibleException) {
            // [Apr 13, 2014 3:04:11 PM] [Amr.ElAdawy] [set the source of the page in the request parameters for extra use]
            request.setAttribute(OriginPageAttribute, e.getMessage());

            response.sendError(HttpServletResponse.SC_FORBIDDEN, "JSPX Error: Page " + e.getMessage() + " is not accessible.");
        } else if (e instanceof InvocationTargetException) {
            Throwable ex = ((InvocationTargetException) e).getTargetException();
            if (ex != null)
                handleException(request, response, (Exception) ex);
            else
                throw new ServletException(e.getCause());
        } else
            throw new ServletException("Generic Exception : ", e);
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try {
            JMXBeanUtil.regesiterMXBean(JspxAdmin.BEAN_NAME);
        } catch (Throwable e) {
            logger.error("failed to start JspxAdmin JMXBean", e);
        }
    }

}