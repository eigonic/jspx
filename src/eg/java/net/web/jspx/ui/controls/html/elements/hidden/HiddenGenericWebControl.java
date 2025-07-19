/**
 *
 */
package eg.java.net.web.jspx.ui.controls.html.elements.hidden;

import eg.java.net.web.jspx.engine.util.ui.RenderPrinter;
import eg.java.net.web.jspx.ui.controls.html.GenericWebControl;
import eg.java.net.web.jspx.ui.pages.Page;

import java.io.OutputStream;

/**
 * parent for all controls that do not render anything.
 *
 * @author Amr.ElAdawy
 *
 */
public class HiddenGenericWebControl extends GenericWebControl {

    private static final long serialVersionUID = -6991818891059964094L;

    /**
     * @param tagName
     */
    public HiddenGenericWebControl(String tagName) {
        super(tagName);
    }

    /**
     * @param tagName
     * @param page
     */
    public HiddenGenericWebControl(String tagName, Page page) {
        super(tagName, page);
    }

    public void render(RenderPrinter outputStream) throws Exception {
    }

    /**
     * empty as there is no attributes to be rendered.
     */
    protected void renderAttributes(OutputStream outputStream) throws Exception {
    }
}
