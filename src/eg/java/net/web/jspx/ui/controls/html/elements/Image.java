/**
 *
 */
package eg.java.net.web.jspx.ui.controls.html.elements;

import eg.java.net.web.jspx.engine.parser.TagFactory;
import eg.java.net.web.jspx.engine.util.ui.RenderPrinter;
import eg.java.net.web.jspx.ui.controls.attrbs.JspxAttribute;
import eg.java.net.web.jspx.ui.controls.html.GenericWebControl;
import eg.java.net.web.jspx.ui.pages.Page;

/**
 * @author amr.eladawy
 *
 */
public class Image extends GenericWebControl {

    /**
     *
     */
    private static final long serialVersionUID = 2229565389041964801L;
    protected static String imageSrcKey = "src";
    @JspxAttribute
    private final String altKey = "alt";
    @JspxAttribute
    private final String titleKey = "title";

    public Image() {
        super(TagFactory.Image);
        isContentControl = false;
    }

    public Image(Page page) {
        super(TagFactory.Image, page);
        isContentControl = false;
    }

    public String getSrc() {
        return getAttributeValue(imageSrcKey);
    }

    public void setSrc(String imageSrc) {
        setAttributeValue(imageSrcKey, imageSrc);
    }

    public String getAlt() {
        return getAttributeValue(altKey);
    }

    public void setAlt(String alt) {
        setAttributeValue(altKey, alt);
    }

    public String getTitle() {
        return getAttributeValue(titleKey);
    }

    public void setTitle(String title) {
        setAttributeValue(titleKey, title);
    }

    /**
     * overridden to change the src attribute by adding the absolute url.
     */
    protected void renderAttributes(RenderPrinter outputStream) throws Exception {
        setSrc(composeURL(getSrc()));
        super.renderAttributes(outputStream);
    }
}
