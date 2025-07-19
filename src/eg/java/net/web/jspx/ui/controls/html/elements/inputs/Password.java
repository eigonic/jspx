/**
 *
 */
package eg.java.net.web.jspx.ui.controls.html.elements.inputs;

import eg.java.net.web.jspx.engine.util.ui.RenderPrinter;
import eg.java.net.web.jspx.ui.controls.html.elements.Input;

/**
 * @author amr.eladawy
 *
 */
public class Password extends Input {

    /**
     *
     */
    private static final long serialVersionUID = -2045984556580260986L;

    public Password() {
        setType(Password);
    }

    protected void renderAttributes(RenderPrinter outputStream) throws Exception {
        setValue("");
        super.renderAttributes(outputStream);
    }

}
