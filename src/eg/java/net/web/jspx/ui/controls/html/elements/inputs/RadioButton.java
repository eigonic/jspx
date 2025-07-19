/**
 *
 */
package eg.java.net.web.jspx.ui.controls.html.elements.inputs;

import eg.java.net.web.jspx.engine.util.ui.RenderPrinter;
import eg.java.net.web.jspx.ui.controls.attrbs.JspxAttribute;
import eg.java.net.web.jspx.ui.controls.html.elements.Input;
import eg.java.net.web.jspx.ui.controls.html.elements.Label;

/**
 * @author amr.eladawy
 *
 */
public class RadioButton extends CheckBox {

    /**
     *
     */
    private static final long serialVersionUID = 1062880312995046870L;
    protected static String checkedKey = "checked";
    @JspxAttribute
    protected String valueProperty = "valueProperty";
    @JspxAttribute
    protected String textProperty = "textProperty";

    public RadioButton() {
        setType(Radio);
    }

    public String getName() {
        return getAttributeValue(nameKey);
    }

    public void setName(String name) {
        setAttributeValue(nameKey, name);
    }

    protected void renderLabel(RenderPrinter outputStream) throws Exception {
        Label label = new Label();
        label.setFor(getId());
        label.setText(getText());
        label.render(outputStream);
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

    @Override
    public String getValue() {
        return getAttributeValue(Input.value);
    }

    @Override
    public void setValue(String value) {
        super.setValue(value);
        setAttributeValue(Input.value, value);
    }

}
