/**
 *
 */
package eg.java.net.web.jspx.ui.controls.html.elements;

import eg.java.net.web.jspx.engine.parser.TagFactory;
import eg.java.net.web.jspx.engine.util.StringUtility;
import eg.java.net.web.jspx.engine.util.ui.RenderPrinter;
import eg.java.net.web.jspx.ui.controls.WebControl;
import eg.java.net.web.jspx.ui.controls.attrbs.JspxAttribute;
import eg.java.net.web.jspx.ui.controls.html.GenericWebControl;
import eg.java.net.web.jspx.ui.controls.html.elements.inputs.Hidden;
import eg.java.net.web.jspx.ui.controls.html.elements.markers.ISubmitter;
import eg.java.net.web.jspx.ui.pages.MasterPage;
import eg.java.net.web.jspx.ui.pages.Page;

/**
 * @author amr.eladawy
 *
 */
public class Form extends GenericWebControl implements ISubmitter {

    public static final String MultiPArtForm = "multipart/form-data";
    /**
     *
     */
    private static final long serialVersionUID = 55920665210022396L;
    public static String ViewState = "_ViewState";
    public static String Invoker = "_Invoker";
    public static String EventName = "_EventName";
    public static String EventArgs = "_EventArgs";
    public static String EventType = "_EventType";
    public static String Group = "_Group";
    public static String EventTarget = "_EventTarget";
    @JspxAttribute
    protected static String enctype = "enctype";
    /**
     * Event Inovker Name (Button, LinkCommand)
     */
    protected Hidden invoker = new Hidden();
    /**
     * Event Name (Java Method name to be invoked)
     */
    protected Hidden eventName = new Hidden();
    /**
     * Event Args (Extra Information passed to the server)
     */
    protected Hidden eventArgs = new Hidden();
    /**
     * Event Type (empty=>user, internal=>control, external=>control and handled by user)
     */
    protected Hidden eventType = new Hidden();
    protected Hidden group = new Hidden();
    /**
     * Event Args (empty=>normal,anyvalue=>ajax submitter.)
     */
    protected Hidden eventTarget = new Hidden();
    protected Hidden conversationId = new Hidden();
    protected String action = "action";
    protected String method = "method";

    public Form() {
        super(TagFactory.Form);
        createItems();
    }

    public Form(Page page) {
        super(TagFactory.Form, page);
        createItems();
    }

    /**
     * creates the page script as script items and add them to the page.
     */
    private void createItems() {
        createInvoker();
        createEventName();
        createEventArgs();
        createEventType();
        createGroup();
        createEventTarget();

        createConversationId();
    }

    /**
     *
     */
    private void createEventType() {
        eventType.setId(EventType);
        addControl(eventType);
    }

    private void createInvoker() {
        invoker.setId(Invoker);
        addControl(invoker);
    }

    private void createEventName() {
        eventName.setId(EventName);
        addControl(eventName);
    }

    private void createEventArgs() {
        eventArgs.setId(EventArgs);
        addControl(eventArgs);
    }

    private void createGroup() {
        group.setId(Group);
        addControl(group);
    }

    private void createEventTarget() {
        eventTarget.setId(EventTarget);
        addControl(eventTarget);
    }

    private void createConversationId() {
        conversationId.setId(Page.CONVERSATION_KEY);
        addControl(conversationId);
    }

    /**
     * implement it to render the view state.
     */
    @Override
    protected void preRenderEndTag(RenderPrinter outputStream) throws Exception {
        // Adding the view state in the end of the page.
        Hidden hidden = new Hidden();
        hidden.setValue(page.getViewStateString());
        hidden.setId(ViewState);
        hidden.render(outputStream);

    }

    public String getAction() {
        return getAttributeValue(action);
    }

    public void setAction(String actionString) {
        setAttributeValue(action, actionString);
    }

    public String getMethod() {
        return getAttributeValue(method);
    }

    public void setMethod(String methodString) {
        setAttributeValue(method, methodString);
    }

    /**
     * overriden in order to prevent duplicating the controls declared in the constructor.
     */
    protected void cloneChildrenControls(GenericWebControl newGenericWebControl) {
        WebControl control = null;
        for (int i = 0; i < controls.size(); i++) {
            control = controls.get(i);
            // Adawy changed this to add this instance as the submitter for this
            // children of this Form.
            if (control != invoker && control != eventName && control != eventArgs && control != group && !control.getId().equals(eventType.getId())
                    && !control.getId().equals(eventTarget.getId()) && control != conversationId)

                newGenericWebControl.getControls().add(
                        controls.get(i).clone(newGenericWebControl, newGenericWebControl.getPage(), (ISubmitter) newGenericWebControl,
                                newGenericWebControl.getMyAjaxSubmitter()));
        }

        // consider that an ajax panel is warping form, in this case this panel will not know its submitter.
        // for this issue, we need to know if there is an ajax submitter for the current form.
        // then we see if this ajax submitter does not have a submitter, then set its submitter to the current form
        if (newGenericWebControl.getMyAjaxSubmitter() != null && ((WebControl) newGenericWebControl.getMyAjaxSubmitter()).getMySubmitter() == null)
            ((WebControl) newGenericWebControl.getMyAjaxSubmitter()).setMySubmitter((ISubmitter) newGenericWebControl);

    }

    /*
     * (non-Javadoc)
     * @see java.net.web.jspx.ui.controls.WebControl#render(java.io.OutputStream)
     */
    @Override
    public void render(RenderPrinter outputStream) throws Exception {
        if (!isRendered() || !isAccessible())
            return;
        conversationId.setPage(page);
        conversationId.setValue(page.conversationId);
        String qString = page.request.getQueryString();
        String fullUrl;
        if (page instanceof MasterPage) {
            MasterPage master = (MasterPage) page;
            if (StringUtility.isNullOrEmpty(getAction()) && page.isDispatched) {
                fullUrl = master.getContentPage().getPageURL();
                if (!StringUtility.isNullOrEmpty(qString))
                    fullUrl += "?" + qString;
                setAction(fullUrl);
            }
        } else {
            if (StringUtility.isNullOrEmpty(getAction()) && page.isDispatched) {
                fullUrl = page.getPageURL();
                if (!StringUtility.isNullOrEmpty(qString))
                    fullUrl += "?" + qString;
                setAction(fullUrl);
            }
        }
        // [Oct 10, 2011 5:23:02 PM] [amr.eladawy] [if the page action does not start with context path, then add it]
        String path = getAction();
        if (!StringUtility.isNullOrEmpty(path)) {
            if (!path.startsWith("/"))
                path = "/" + path;
            if (!path.startsWith(page.request.getContextPath()))
                path = page.request.getContextPath() + path;

            setAction(path);
        }
        setMethod("post");
        super.render(outputStream);
    }

    public String getEnctype() {
        return getAttributeValue(enctype);
    }

    public void setEnctype(String enctypeVal) {
        setAttributeValue(enctype, enctypeVal);
    }
}
