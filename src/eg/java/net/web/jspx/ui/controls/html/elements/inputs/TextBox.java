/**
 *
 */
package eg.java.net.web.jspx.ui.controls.html.elements.inputs;

import eg.java.net.web.jspx.engine.RequestHandler;
import eg.java.net.web.jspx.engine.util.StringUtility;
import eg.java.net.web.jspx.engine.util.ui.Render;
import eg.java.net.web.jspx.engine.util.ui.RenderPrinter;
import eg.java.net.web.jspx.ui.controls.attrbs.Attribute;
import eg.java.net.web.jspx.ui.controls.attrbs.JspxAttribute;
import eg.java.net.web.jspx.ui.controls.html.elements.Input;
import eg.java.net.web.jspx.ui.controls.html.elements.Label;
import eg.java.net.web.jspx.ui.pages.Page;

/**
 * @author amr.eladawy
 *
 */
public class TextBox extends Input {
    /**
     *
     */
    private static final long serialVersionUID = -1451194388571261909L;

    @JspxAttribute
    protected static String onClientChangeKey = "onChange";

    @JspxAttribute
    protected static String onServerChangeKey = "onServerChange";
    @JspxAttribute
    private static final String inlineKey = "inline";
    @JspxAttribute
    private final String readonlyKey = "readonly";
    private final String size = "size";

    public TextBox() {
        super();
        setType(Input.TextBox);
    }

    public TextBox(Page page) {
        super(page);
        setType(Input.TextBox);
    }

    @Override
    protected void loadInternalAttributes() {
        super.loadInternalAttributes();
        internalAttribtes.put(onClientChangeKey.toLowerCase(), 0);
        internalAttribtes.put(onServerChangeKey.toLowerCase(), 0);
        internalAttribtes.put(inlineKey.toLowerCase(), 0);
    }

    @Override
    public void render(RenderPrinter outputStream) throws Exception {
        // [Mar 19, 2012 3:00:00 PM] [amr.eladawy] [render inline]
        if (getInline()) {
            getStyle().put("display", new Attribute("display", "none"));
            Label l = new Label(page);
            if (StringUtility.isNullOrEmpty(getValue())) {
                l.setValue(Render.NonBreakSpace + Render.NonBreakSpace + Render.NonBreakSpace + Render.NonBreakSpace + Render.NonBreakSpace
                        + Render.NonBreakSpace + Render.NonBreakSpace + Render.NonBreakSpace + Render.NonBreakSpace);
                l.getStyle().put("border-color", new Attribute("border-color", "#FFFFCD"));
                l.getStyle().put("border-style", new Attribute("border-style", "solid"));
                l.getStyle().put("border-width", new Attribute("border-width", "1px"));
            } else
                l.setValue(getValue());
            l.setCssClass("jspxInline");
            l.setId(getId() + "_label");
            l.render(outputStream);

            String script = getScript();
            page.addOnloadScript(script);
        } else {
            if (RequestHandler.THEME_TWITTER && !noSystemCss)
                setCssClass(getCssClass() + " input-medium");
        }
        super.render(outputStream);
    }

    private String getScript() {
        String document = "";
        if (page != null && page.isAjaxPostBack && page.isMultiPartForm)
            document = ",window.parent.document";

        String script = "$('#" + getId() + "_label'" + document +
                ").livequery('click',function (){$(this).hide();\n $('#" + getId() +
                "').show().focus().attr('value',jQuery.trim($(this).text()));});\n$('#" + getId() + "'" + document +
                ").livequery('blur',function (){$(this).hide();\n $('#" + getId() +
                "_label').show().html($(this).attr('value')==''?'&nbsp;&nbsp;&nbsp;':$(this).attr('value'));});";
        return script;
    }

    @Override
    protected void renderAttributes(RenderPrinter outputStream) throws Exception {
        String onChangeScript = createServerClientScript(getOnClientChange(), getOnServerChange(), getConfirmation());
        if (!StringUtility.isNullOrEmpty(onChangeScript))
            new Attribute(onClientChangeKey, onChangeScript).render(outputStream, page);
        // [28 Sep 2015 12:19:47] [aeladawy] [handle readonly]
        if (!getAttributeBooleanValue(readonlyKey))
            attributes.remove(readonlyKey);

        super.renderAttributes(outputStream);
    }

    public void setReadonly(boolean readonly) {
        if (readonly)
            setAttributeValue(readonlyKey, readonlyKey);
        else
            attributes.remove(readonlyKey);
    }

    public boolean isChecked() {
        return attributes.contains(readonlyKey);
    }

    public int getSize() {
        return getAttributeIntValue(size);
    }

    public void setSize(int sizeVal) {
        setAttributeIntValue(size, sizeVal);
    }

    public String getOnClientChange() {
        return getAttributeValue(onClientChangeKey);
    }

    public void setOnClientChange(String onClientChange) {
        setAttributeValue(onClientChangeKey, onClientChange);
    }

    public String getOnServerChange() {
        return getAttributeValue(onServerChangeKey);
    }

    public void setOnServerChange(String onChange) {
        setAttributeValue(onServerChangeKey, onChange);
    }

    public boolean getInline() {
        return getAttributeBooleanValue(inlineKey);
    }

    public void setInline(boolean inline) {
        setAttributeBooleanValue(inlineKey, inline);
    }
}
