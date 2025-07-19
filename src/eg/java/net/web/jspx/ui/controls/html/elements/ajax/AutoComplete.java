package eg.java.net.web.jspx.ui.controls.html.elements.ajax;

import eg.java.net.web.jspx.engine.parser.TagFactory;
import eg.java.net.web.jspx.engine.util.ui.Render;
import eg.java.net.web.jspx.engine.util.ui.RenderPrinter;
import eg.java.net.web.jspx.ui.controls.WebControl;
import eg.java.net.web.jspx.ui.controls.attrbs.JspxAttribute;
import eg.java.net.web.jspx.ui.controls.html.GenericWebControl;
import eg.java.net.web.jspx.ui.controls.html.elements.HyperLink;
import eg.java.net.web.jspx.ui.controls.html.elements.Input;
import eg.java.net.web.jspx.ui.controls.html.elements.Panel;
import eg.java.net.web.jspx.ui.controls.html.elements.dataitem.ListAutoComplete;
import eg.java.net.web.jspx.ui.controls.html.elements.dataitem.SqlAutoComplete;
import eg.java.net.web.jspx.ui.controls.html.elements.inputs.TextBox;
import eg.java.net.web.jspx.ui.controls.html.elements.markers.IAjaxSubmitter;
import eg.java.net.web.jspx.ui.controls.html.elements.markers.InternalEventsListener;
import eg.java.net.web.jspx.ui.controls.html.elements.markers.InternalValueHolder;
import eg.java.net.web.jspx.ui.controls.html.elements.markers.ValueHolder;
import eg.java.net.web.jspx.ui.controls.html.w3.BreakLine;
import eg.java.net.web.jspx.ui.pages.Page;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Ajax update panel
 *
 * @author amr.eladawy
 */
public class AutoComplete extends GenericWebControl implements IAjaxSubmitter, ValueHolder, InternalValueHolder, InternalEventsListener {
    /**
     *
     */
    private static final long serialVersionUID = -5622174505901542873L;
    @JspxAttribute
    public static String value = "value";
    @JspxAttribute
    protected static String onRenderKey = "onrender";
    boolean internalEvent;
    private String valueBinding;

    public AutoComplete() {
        super(TagFactory.AutoComplete);
    }

    public AutoComplete(Page page) {
        super(TagFactory.AutoComplete, page);
    }

    public String getTageName() {
        return TagFactory.Span;
    }

    @Override
    protected void postRenderStartTag(RenderPrinter outputStream) throws Exception {
        // this is span, it should be rendered as a content <span></span> even there are no controls within.
        isContentControl = true;
    }

    protected void loadInternalAttributes() {
        super.loadInternalAttributes();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.net.web.jspx.ui.controls.WebControl#render(java.io.OutputStream)
     */
    @Override
    public void render(RenderPrinter outputStream) throws Exception {
        if (!isRendered() || !isAccessible())
            return;
        rebindValueHolderControl();
        TextBox box = createTextField();
        // [Jul 5, 2011 6:12:42 PM] [amr.eladawy] [here I render the input before the ajax panel]
        if (!internalEvent) {
            box.render(outputStream);
            // [12 Mar 2015 16:11:51] [aeladawy] [remove on render before internal events]
            getAttributes().remove(getAttribute(onRenderKey));

        }
        outputStream.write(Render.startTag);
        outputStream.write(getTageName());

        renderAttributes(outputStream);
        renderStyle(outputStream);

        outputStream.write(Render.endTag);
        postRenderStartTag(outputStream);
        Render.renderNewLine(outputStream);
        // [Jul 6, 2011 11:25:27 AM] [amr.eladawy] [render only if this is the internal event.]
        if (internalEvent)
            renderChildren(outputStream);

        preRenderEndTag(outputStream);
        outputStream.write(Render.startEndTag);
        outputStream.write(getTageName());
        outputStream.write(Render.endTag);
        outputStream.write(Render.newLine);
    }

    /**
     * @return
     * @Author etisalat 12 Mar 2015 16:30:10
     */
    protected TextBox createTextField() {
        TextBox box = new TextBox();
        box.setId(getId() + nameSplitter + "text");
        // [Jul 6, 2011 10:44:39 AM] [amr.eladawy] [set the name of the text box to the name of the control in order to
        // retrieve the data]
        box.setAttributeValue("name", getId());
        box.setPage(page);
        box.setMySubmitter(getMySubmitter());
        box.setMyAjaxSubmitter(this);
        box.setValue(getValue());
        box.setStyle(getStyle());
        String call = Render.composeAction(getId(), "search", "", "", true, "", false, false, getMySubmitter(), this);
        box.setAttributeValue("onkeyup", call);
        box.setAttributeValue("onblur", "setTimeout(function(){var c=document.getElementById('" + getId() + nameSplitter
                + "panel'); if (c!=null)c.style.display='none';},300)");
        return box;
    }

    @Override
    public void renderChildren(RenderPrinter outputStream) throws Exception {
        if (!internalEvent) {
            // [12 Mar 2015 16:11:51] [aeladawy] [remove on render before internal events]
            getAttributes().remove(onRenderKey);

        }
        Panel panel = new Panel();
        panel.setId(getId() + nameSplitter + "panel");
        panel.setPage(page);
        panel.setMySubmitter(getMySubmitter());
        panel.setMyAjaxSubmitter(this);
        panel.setCssClass("jspxAutoComplete");
        TextBox box = createTextField();
        String onchangeVal = box.createServerClientScript(getAttributeValue("onChange"), getAttributeValue("onServerChange"), null);
        onchangeVal = onchangeVal == null ? "" : onchangeVal.replace("'", "\\'");
        // [Jul 5, 2011 5:15:33 PM] [amr.eladawy] [render the data only if this is a postback]
        if (page.isPostBack) {
            // [Jul 5, 2011 5:09:07 PM] [amr.eladawy] [call the sql Auto complete to get the data]

            List<Object> data = new ArrayList<Object>();
            for (WebControl control : controls) {
                if (control instanceof SqlAutoComplete) {
                    data = ((SqlAutoComplete) control).getData(getValue());
                    break;
                }
                if (control instanceof ListAutoComplete) {
                    data = ((ListAutoComplete) control).getData(getValue());
                    break;
                }
            }
            for (Object item : data) {
                HyperLink link = new HyperLink();
                link.setNavigationURL("#");
                link.setAttributeValue(Input.onClientClickKey, "jspxAutoComplete('" + panel.getId() + "','" + getId() + nameSplitter + "text','"
                        + String.valueOf(item).replace("'", "\\'").replace("\"", "&qout;") + "','" + onchangeVal + "');");
                link.setText(String.valueOf(item));
                link.setCssClass("jspxAutoCompleteLink");
                panel.addControl(link);
                panel.addControl(new BreakLine());
            }

            panel.render(outputStream);
        }
    }

    public String getValue() {
        return getAttributeValue(value);
    }

    public void setValue(String valueString) {
        setAttributeValue(value, valueString);
    }

    public String getValueBinding() {
        return Input.calculateValueBinding(valueBinding, getAttribute(value));
    }

    public void setValueBinding(String valueBinding) {
        this.valueBinding = valueBinding;
    }

    public void setInternalInputValues(Hashtable<String, String> values) {
        for (String key : values.keySet())
            setValue(values.get(key));

    }

    public void search(WebControl inovker, String args) {
        // [Jul 6, 2011 11:48:53 AM] [amr.eladawy] [in case the internal event is fired set the internal to true.]
        internalEvent = true;
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
