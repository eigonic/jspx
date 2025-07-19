/**
 *
 */
package eg.java.net.web.jspx.ui.controls.html.elements.inputs;

import eg.java.net.web.jspx.engine.util.ui.RenderPrinter;
import eg.java.net.web.jspx.ui.controls.attrbs.JspxAttribute;
import eg.java.net.web.jspx.ui.controls.html.elements.Input;

/**
 * @author amr.eladawy
 *
 */
public class ImageButton extends Input {

    /**
     *
     */
    private static final long serialVersionUID = -8111791953100690860L;
    @JspxAttribute
    protected static String imageSrcKey = "src";

    public ImageButton() {
        setType(Input.Image);
    }

    public String getSrc() {
        return getAttributeValue(imageSrcKey);
    }

    public void setSrc(String imageSrc) {
        setAttributeValue(imageSrcKey, imageSrc);
    }

    protected void loadInternalAttributes() {
        // the src attribute is added to internal attributes to render it cusomize way.
        internalAttribtes.put(imageSrcKey.toLowerCase(), 0);
        super.loadInternalAttributes();
    }

    protected void renderAttributes(RenderPrinter outputStream) throws Exception {
        setSrc(composeURL(getSrc()));
        attributes.get(imageSrcKey).render(outputStream, page);
        super.renderAttributes(outputStream);
    }
}
