/**
 *
 */
package eg.java.net.web.jspx.ui.controls.html.elements.inputs;

import eg.java.net.web.jspx.engine.util.ui.RenderPrinter;
import eg.java.net.web.jspx.ui.controls.html.elements.Input;
import eg.java.net.web.jspx.ui.pages.Page;

/**
 * @author amr.eladawy
 *
 */
public class Button extends Input {
    /**
     *
     */
    private static final long serialVersionUID = -122727258751810888L;

    public Button() {
        super();
        setType(Input.Button);
    }

    public void setValue(String valueString) {
        if (page.getPageStatus() != Page.PagePostBackData)
            setAttributeValue(value, valueString);
    }

    /* (non-Javadoc)
     * @see eg.java.net.web.jspx.ui.controls.html.elements.Input#render(eg.java.net.web.jspx.engine.util.ui.RenderPrinter)
     */
    @Override
    public void render(RenderPrinter outputStream) throws Exception {
        setCssClass(getCssClass() + " btn");
        super.render(outputStream);
    }
}
