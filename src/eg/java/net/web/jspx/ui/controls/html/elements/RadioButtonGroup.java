/**
 *
 */
package eg.java.net.web.jspx.ui.controls.html.elements;

import eg.java.net.web.jspx.engine.parser.TagFactory;
import eg.java.net.web.jspx.engine.util.ELUtility;
import eg.java.net.web.jspx.engine.util.PropertyAccessor;
import eg.java.net.web.jspx.engine.util.StringUtility;
import eg.java.net.web.jspx.engine.util.ui.RenderPrinter;
import eg.java.net.web.jspx.ui.controls.WebControl;
import eg.java.net.web.jspx.ui.controls.attrbs.JspxAttribute;
import eg.java.net.web.jspx.ui.controls.html.GenericWebControl;
import eg.java.net.web.jspx.ui.controls.html.elements.inputs.RadioButton;
import eg.java.net.web.jspx.ui.controls.html.elements.markers.ValueHolder;
import eg.java.net.web.jspx.ui.pages.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 *
 * @author amr.eladawy
 *
 */
public class RadioButtonGroup extends GenericWebControl implements ValueHolder {

    /**
     *
     */
    private static final long serialVersionUID = -4856777304447179744L;
    private static final Logger logger = LoggerFactory.getLogger(RadioButtonGroup.class);
    @JspxAttribute
    protected String list = "list";
    @JspxAttribute
    protected String onClientClickKey = "onClick";
    private boolean valueFound = false;
    private final String value = "value";
    private String valueBinding;

    public RadioButtonGroup() {
        super(TagFactory.RadioButtonGroup);
    }

    public RadioButtonGroup(Page page) {
        super(TagFactory.RadioButtonGroup, page);
    }

    @SuppressWarnings("unchecked")
    public Collection<Object> getListObject() {
        try {
            Collection c = (Collection<Object>) getAttributeValueObject(list);
            if (c == null) {
                // get the list from the controller
                c = (Collection) ELUtility.evaluateEL("${this." + getList() + "}", page);
            }
            return c;
        } catch (Exception e) {
            logger.debug("Error while getting list for the select item>>> listname: " + list + ", error: " + e);
            return null;
        }

    }

    @Override
    public void render(RenderPrinter outputStream) throws Exception {
        rebindValueHolderControl();
        renderChildren(outputStream);
    }

    @Override
    public void renderChildren(RenderPrinter outputStream) throws Exception {

        Collection<Object> radioList = null;
        if (!StringUtility.isNullOrEmpty(getList())) {
            radioList = getListObject();
        }
        for (WebControl control : controls) {
            if (control instanceof RadioButton) {
                RadioButton radio = (RadioButton) control;
                if (radioList != null && !StringUtility.isNullOrEmpty(radio.getValueProperty())
                        && !StringUtility.isNullOrEmpty(radio.getTextProperty())) {
                    renderListOptin(radio, outputStream, radioList);
                } else {

                    renderRadio(radio, outputStream);
                }
            } else
                control.render(outputStream);
        }
        // when setting the value of the control from Java , developer can set it to a value
        // that is not in the options items.
        // so , just logging this issue and do nothing.
        if (!StringUtility.isNullOrEmpty(getValue()) && !valueFound) {
            logger.error("Selected Value is not in the model...");
        }
    }

    private void renderListOptin(RadioButton radio, RenderPrinter outputStream, Collection<Object> optionList) throws Exception {

        for (Object object : optionList) {
            RadioButton r = new RadioButton();
            r.setName(getName());

            r.setValue(String.valueOf(PropertyAccessor.getProperty(object, radio.getValueProperty())));
            r.setText(String.valueOf(PropertyAccessor.getProperty(object, radio.getTextProperty())));
            if (isSelectedRadio(r.getValue())) {
                r.setChecked(true);
                valueFound = true;
            } else
                r.setChecked(false);
            r.render(outputStream);
        }

    }

    private boolean isSelectedRadio(String optionValue) {
        if (StringUtility.isNullOrEmpty(getValue()))
            return false;
        String[] values = getValue().trim().split(",");
        for (String value : values) {
            if (value.equals(optionValue))
                return true;
        }
        return false;
    }

    private void renderRadio(RadioButton radio, RenderPrinter outputStream) throws Exception {
        radio.setName(getName());
        radio.setOnClientClick(getOnClientClick());
        radio.setChecked(isSelectedRadio(radio.getValue()));
        radio.render(outputStream);
        valueFound = true;
    }

    public String getList() {
        return getAttributeValue(list);
    }

    public void setList(String list) {
        setAttributeValue(this.list, list);
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

    public String getOnClientClick() {
        return getAttributeValue(onClientClickKey);
    }

    public void setOnClientClick(String onClientClick) {
        setAttributeValue(onClientClickKey, onClientClick);
    }

}
