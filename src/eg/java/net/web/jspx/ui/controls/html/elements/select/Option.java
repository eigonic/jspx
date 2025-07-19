package eg.java.net.web.jspx.ui.controls.html.elements.select;

import eg.java.net.web.jspx.engine.parser.TagFactory;
import eg.java.net.web.jspx.engine.util.ui.RenderPrinter;
import eg.java.net.web.jspx.ui.controls.attrbs.Attribute;
import eg.java.net.web.jspx.ui.controls.attrbs.JspxAttribute;
import eg.java.net.web.jspx.ui.controls.html.GenericWebControl;
import eg.java.net.web.jspx.ui.controls.html.StringLiteral;
import eg.java.net.web.jspx.ui.pages.Page;

import java.util.HashMap;

public class Option extends GenericWebControl {

    /**
     *
     */
    private static final long serialVersionUID = -4046712051420660923L;
    @JspxAttribute
    protected String valueProperty = "valueProperty";
    @JspxAttribute
    protected String textProperty = "textProperty";
    private final String selected = "selected";
    private final String value = "value";

    public Option() {
        super(TagFactory.Option);
    }

    public Option(Page page) {
        super(TagFactory.Option, page);
    }

    public String getText() {
        return getContentControl().getText();
    }

    public void setText(String text) {
        getContentControl().setText(text);
        if (page != null && page.PageStatus != Page.PageViewState && page.PageStatus != Page.PageCompose)
            viewstate.put(content, text);
    }

    protected StringLiteral getContentControl() {
        StringLiteral literal = null;
        if (!controls.isEmpty())
            literal = ((StringLiteral) controls.get(0));
        else {
            literal = new StringLiteral("");
            addControl(literal);
        }
        return literal;
    }

    /**
     * overrides the parent to obtain the content and then put it into the
     * string literal.
     */
    public void loadViewState(HashMap<String, String> viewState) {
        super.loadViewState(viewState);
        Attribute contentAtt = attributes.get(content);
        if (contentAtt != null) {
            getContentControl().setText(contentAtt.getValue(page));
            attributes.remove(content);
        }
    }

    public boolean getSelected() {
        return getAttributeValue(selected).equalsIgnoreCase("selected");
    }

    public void setSelected(boolean isSelected) {
        setAttributeValue(selected, isSelected ? "selected" : "");
    }

    /**
     * overridden to remove the selected attribute in case this option is not
     * selected.
     */
    protected void renderAttributes(RenderPrinter outputStream) throws Exception {
        if (!getSelected())
            attributes.remove(selected);
        super.renderAttributes(outputStream);
    }

    public String getValue() {
        return getAttributeValue(value);
    }

    public void setValue(String valueString) {
        setAttributeValue(value, valueString);
    }

    public String getValueProperty() {
        return getAttributeValue(valueProperty);
    }

    public void setValueProperty(String valueProperty) {
        setAttributeValue(this.valueProperty, valueProperty);
    }

    public String getTextProperty() {
        return getAttributeValue(textProperty);
    }

    public void setTextProperty(String textProperty) {
        setAttributeValue(this.textProperty, textProperty);
    }

}
