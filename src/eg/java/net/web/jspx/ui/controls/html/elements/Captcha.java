package eg.java.net.web.jspx.ui.controls.html.elements;

import eg.java.net.web.jspx.engine.ResourceHandler;
import eg.java.net.web.jspx.engine.parser.TagFactory;
import eg.java.net.web.jspx.engine.util.Security;
import eg.java.net.web.jspx.engine.util.StringUtility;
import eg.java.net.web.jspx.engine.util.ui.Render;
import eg.java.net.web.jspx.engine.util.ui.RenderPrinter;
import eg.java.net.web.jspx.ui.controls.attrbs.Attribute;
import eg.java.net.web.jspx.ui.controls.attrbs.JspxAttribute;
import eg.java.net.web.jspx.ui.controls.html.GenericWebControl;
import eg.java.net.web.jspx.ui.controls.html.elements.inputs.TextBox;
import eg.java.net.web.jspx.ui.controls.html.elements.markers.IAjaxSubmitter;
import eg.java.net.web.jspx.ui.controls.html.elements.markers.IValidator;
import eg.java.net.web.jspx.ui.controls.html.elements.markers.InternalEventsListener;
import eg.java.net.web.jspx.ui.controls.html.elements.markers.InternalValueHolder;
import eg.java.net.web.jspx.ui.controls.validators.ValidationException;
import eg.java.net.web.jspx.ui.pages.Page;

import java.util.Hashtable;

public class Captcha extends GenericWebControl implements InternalValueHolder, IValidator, InternalEventsListener, IAjaxSubmitter {
    /**
     *
     */
    private static final long serialVersionUID = -6514795422339677880L;
    public static String Numeric = "Numeric";
    public static String Alpha = "Alpha";
    public static String AlphaNumeric = "AlphaNumeric";

    public static String CapatchaPrefix = "capatcha";
    @JspxAttribute
    protected static String GroupKey = "group";
    @JspxAttribute
    protected static String onRenderKey = "onrender";
    /**
     * true of the validation failed and the validator will be rendered visible.
     */
    public boolean invalid = false;
    protected String value = "";
    protected String secKey = "";
    boolean internalEvent;
    private final String length = "length";
    private final String type = "type";
    private final String messageKey = "message";
    private final String messageStyle = "messagestyle";
    private final String messageClass = "messageclass";

    public Captcha() {
        super(TagFactory.Captcha);

    }

    public Captcha(String tagName, Page page) {
        super(TagFactory.Captcha, page);
    }

    public void render(RenderPrinter outputStream) throws Exception {
        if (!isRendered() || !isAccessible())
            return;
        GenericWebControl cap = composeCapControl();

        cap.render(outputStream);
    }

    public void renderChildren(RenderPrinter outputStream) throws Exception {
        // [Mar 13, 2012 1:56:27 PM] [amr.eladawy] [render the content of the Cap Span only]
        GenericWebControl cap = composeCapControl();
        cap.renderChildren(outputStream);
    }

    /**
     * composes the control to be rendered.
     *
     * @return
     */
    private GenericWebControl composeCapControl() {
        page.request.getSession().removeAttribute(secKey);
        String milli = String.valueOf(System.currentTimeMillis());
        page.request.getSession().setAttribute(milli, Security.createPasscode(getLength(), getTypeInt(getType())));
        GenericWebControl cap = new GenericWebControl(TagFactory.Div);
        cap.setId(getId());
        cap.setPage(page);
        GenericWebControl imageDiv = new GenericWebControl(TagFactory.Div);
        imageDiv.setPage(page);
        imageDiv.getStyle().put("min-height", new Attribute("min-height", "46px"));
        imageDiv.getStyle().put("text-align", new Attribute("text-align", "center"));
        imageDiv.getStyle().put("max-width", new Attribute("max-width", "260px"));

        Image image = new Image();
        image.setSrc(ResourceHandler.ResourcePrefix + CapatchaPrefix + "/" + milli + ".jpg");

        imageDiv.addControl(image);
        cap.addControl(imageDiv);

        GenericWebControl div = new GenericWebControl(TagFactory.Div);
        div.setCssClass("input-prepend input-append");
        div.setPage(page);

        HyperLink a = new HyperLink(page);
        String call = "javascript:" + Render.composeAction(getId(), "refresh", milli, "", true, "", false, false, getMySubmitter(), this);
        a.setNavigationURL(call);
        // a.getStyle().put("text-decoration", new Attribute("text-decoration", "none"));
        a.setCssClass("add-on");

        GenericWebControl reloadImage = new GenericWebControl("i", page);
        reloadImage.setCssClass("icon-refresh");

        a.addControl(reloadImage);
        div.addControl(a);

        TextBox box = new TextBox();
        box.setId(getId() + nameSplitter + milli);
        div.addControl(box);

        // print the *
        GenericWebControl required = new GenericWebControl("i", page);
        required.setCssClass("icon-asterisk ");
        div.addControl(required);

        // print the error msg if invalid
        if (invalid) {
            box.setCssClass(box.getCssClass() + " error");
            String message = StringUtility.isNullOrEmpty(getMessage()) ? "Invalid Passcode" : getMessage();
            Label label = new Label();
            setStyleAndClass(label);
            label.setText(message);
            div.addControl(label);
        }

        cap.addControl(div);
        return cap;
    }

    private void setStyleAndClass(Label label) {
        boolean done = false;
        if (!StringUtility.isNullOrEmpty(getMessageClass())) {
            label.setCssClass(getMessageClass());
            done = true;
        }
        if (!StringUtility.isNullOrEmpty(getMessageStyle())) {
            label.setStyle(getMessageStyle());
            done = true;
        }
        if (!done)
            label.setStyle("color:red;");
    }

    public void setInternalInputValues(Hashtable<String, String> values) {
        // TO-DO here get the value of the session and the passed value.-- Done
        for (String key : values.keySet()) {
            secKey = key.substring(getId().length() + nameSplitter.length());
            value = values.get(key);
            break;
        }
    }

    public int getLength() {
        int len = getAttributeIntValue(length);
        if (len == 0)
            return 6;
        return len;
    }

    public void setLength(int len) {
        setAttributeIntValue(length, len);
    }

    public String getType() {
        return getAttributeValue(type);
    }

    public void setType(String format) {
        setAttributeValue(type, format);
    }

    protected int getTypeInt(String type) {
        if (type.equalsIgnoreCase(AlphaNumeric))
            return 2;
        else if (type.equalsIgnoreCase(Alpha))
            return 1;
        return 0;
    }

    public String getMessage() {
        return getAttributeValue(messageKey);
    }

    public void setMessage(String message) {
        setAttributeValue(messageKey, message);
    }

    public void validate() throws ValidationException {
        String savedPasscode = (String) page.request.getSession().getAttribute(secKey);
        // [Mar 13, 2012 2:31:05 PM] [amr.eladawy] [remove the old key]
        page.request.getSession().removeAttribute(secKey);
        if (StringUtility.isNullOrEmpty(savedPasscode) || !savedPasscode.equalsIgnoreCase(value)) {
            invalid = true;
            throw new ValidationException("Capatcha Value is invalid");
        }
    }

    public void refresh(String eventArgs) {
        // [Mar 13, 2012 2:00:24 PM] [amr.eladawy] [marking that this is an internal postback to refresh the screen]
        internalEvent = true;
    }

    public String getMessageStyle() {
        return getAttributeValue(messageStyle);
    }

    public void setMessageStyle(String style) {
        setAttributeValue(messageStyle, style);
    }

    public String getMessageClass() {
        return getAttributeValue(messageClass);
    }

    public void setMessageClass(String css) {
        setAttributeValue(messageClass, css);
    }

    public String getGroup() {
        return getAttributeValue(GroupKey);
    }

    public void setGroup(String group) {
        setAttributeValue(GroupKey, group);
    }

    public String getAjaxId() {
        return getId();
    }

    public String getOnRender() {
        return getAttributeValue(onRenderKey);
    }

    public void setOnRender(String onRender) {
        setAttributeValue(onRenderKey, onRender);
    }

    public void setInteralPostback(boolean isInternalPostback) {
    }

    /**
     * @return the action
     */
    public String getAction() {
        return getAttributeValue(action);
    }

    /**
     * @param action the action to set
     */
    public void setAction(String actionValue) {
        setAttributeValue(action, actionValue);
    }

    /**
     * overridden to change the src attribute by adding the absolute url.
     */
    @Override
    protected void renderAttributes(RenderPrinter outputStream) throws Exception {
        setAction(composeURL(getAction()));
        super.renderAttributes(outputStream);
    }
}
