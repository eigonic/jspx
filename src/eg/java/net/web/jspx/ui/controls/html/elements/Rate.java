package eg.java.net.web.jspx.ui.controls.html.elements;

import eg.java.net.web.jspx.engine.ResourceHandler;
import eg.java.net.web.jspx.engine.parser.TagFactory;
import eg.java.net.web.jspx.engine.util.StringUtility;
import eg.java.net.web.jspx.engine.util.ui.Render;
import eg.java.net.web.jspx.engine.util.ui.RenderPrinter;
import eg.java.net.web.jspx.ui.controls.attrbs.JspxAttribute;
import eg.java.net.web.jspx.ui.controls.html.GenericWebControl;
import eg.java.net.web.jspx.ui.controls.html.elements.markers.ValueHolder;
import eg.java.net.web.jspx.ui.controls.html.w3.Script;

/**
 * Star Rating.
 *
 * @author amr.eladawy
 * May 22, 2012 9:54:33 AM
 */
public class Rate extends GenericWebControl implements ValueHolder {

    /**
     *
     */
    private static final long serialVersionUID = 4888298883127142530L;
    @JspxAttribute
    protected static String onServerClickKey = "onServerClick";
    @JspxAttribute
    protected static String commandArgs = "commandargs";
    @JspxAttribute
    private final String readonly = "readonly";
    private String valueBinding;

    public Rate() {
        super(TagFactory.Rate);
        isContentControl = false;
    }

    public void render(RenderPrinter outputStream) throws Exception {
        if (!isRendered() || !isAccessible())
            return;

        GenericWebControl panel = new GenericWebControl(TagFactory.Span, page);
        panel.setId(getId() + "RateDiv");
        panel.render(outputStream);

        Script script = new Script(page);

        StringBuilder js = new StringBuilder("$jspx('#").append(panel.getId()).append("').raty({path:'").append(page.request.getContextPath())
                .append(ResourceHandler.ResourcePrefix).append("imgs/rate").append("',scoreName:'").append(getId()).append("'");
        if (!StringUtility.isNullOrEmpty(getValue()))
            js.append(",score:").append(getValue());
        if (getReadonly())
            js.append(",readOnly:true");

        if (!StringUtility.isNullOrEmpty(getOnServerClick()))
            js.append(",click: function(score, evt) {")
                    .append(Render.composeAction(getId(), getOnServerClick(), getCommandArgs(), "", false, "", false, false, getMySubmitter(),
                            getMyAjaxSubmitter())).append("}");
        js.append("});");

        script.setScriptCode(js.toString());

        script.render(outputStream);

    }

    public String getOnServerClick() {
        return getAttributeValue(onServerClickKey);
    }

    public void setOnServerClick(String onClick) {
        setAttributeValue(onServerClickKey, onClick);
    }

    public String getCommandArgs() {
        return getAttributeValue(commandArgs);
    }

    public void setCommandArgs(String commandArgsValue) {
        setAttributeValue(commandArgs, commandArgsValue);
    }

    public boolean getReadonly() {
        return !StringUtility.isNullOrEmpty(getAttributeValue(readonly));
    }

    public void setReadonly(boolean Readonly) {
        setAttributeValue(readonly, Readonly ? "readonly" : "");
    }

    public String getValue() {
        return getAttributeValue(Input.value);
    }

    public void setValue(String valueString) {
        setAttributeValue(Input.value, valueString);
    }

    public String getValueBinding() {
        return Input.calculateValueBinding(valueBinding, getAttribute(Input.value));
    }

    public void setValueBinding(String valueBinding) {
        this.valueBinding = valueBinding;
    }

}
