/**
 *
 */
package eg.java.net.web.jspx.ui.controls.html.elements;

import eg.java.net.web.jspx.engine.parser.TagFactory;
import eg.java.net.web.jspx.engine.util.StringUtility;
import eg.java.net.web.jspx.ui.controls.html.StringLiteralContainer;
import eg.java.net.web.jspx.ui.controls.html.elements.markers.ValueHolder;
import eg.java.net.web.jspx.ui.pages.Page;

/**
 * @author amr.eladawy
 *
 */
public class Label extends StringLiteralContainer implements ValueHolder {

    /**
     *
     */
    private static final long serialVersionUID = 5732138566723834745L;
    private String valueBinding;

    public Label() {
        super(TagFactory.Label);
    }

    public Label(Page page) {
        super(TagFactory.Label, page);
    }

    public void setFor(String forValue) {
        String forKey = "for";
        setAttributeValue(forKey, forValue);
    }

    public String getValue() {
        return getText();
    }

    public void setValue(String valueString) {
        if (page.getPageStatus() != Page.PagePostBackData)
            setText(valueString);

    }

    public String getValueBinding() {
        if (StringUtility.isNullOrEmpty(valueBinding)) {
            String value = getText();
            if (StringUtility.containsEL(value))
                valueBinding = value;
        }
        return valueBinding;
    }

    public void setValueBinding(String valueBinding) {
        this.valueBinding = valueBinding;
    }
}
