/**
 *
 */
package eg.java.net.web.jspx.ui.controls.html.elements.inputs;

import eg.java.net.web.jspx.ui.controls.html.elements.Input;
import eg.java.net.web.jspx.ui.pages.Page;

/**
 * @author amr.eladawy
 *
 */
public class Submit extends Input {
    /**
     *
     */
    private static final long serialVersionUID = -4240362689672087925L;

    public Submit() {
        super();
        // [Mar 18, 2012 7:11:16 PM] [amr.eladawy] [to prevent double submit, this will be rendered as button not submit]
        setType(Input.Button);
    }

    public void setValue(String valueString) {
        if (page.getPageStatus() != Page.PagePostBackData)
            setAttributeValue(value, valueString);
    }
}
