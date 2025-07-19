/**
 *
 */
package eg.java.net.web.jspx.ui.controls.html.elements;

import eg.java.net.web.jspx.engine.parser.TagFactory;
import eg.java.net.web.jspx.engine.util.ui.RenderPrinter;
import eg.java.net.web.jspx.ui.controls.html.GenericWebControl;
import eg.java.net.web.jspx.ui.pages.Page;

/**
 * @author amr.eladawy
 *
 */
public class ContentHolder extends GenericWebControl {

    private static final long serialVersionUID = 2717934129766617293L;

    /**
     * @param tagName
     */
    public ContentHolder() {
        super(TagFactory.ContentHolder);
    }

    /**
     * @param tagName
     * @param page
     */
    public ContentHolder(Page page) {
        super(TagFactory.ContentHolder, page);
    }

    /**
     * overridden to change the tag name before rendering.
     */
    @Override
    public void render(RenderPrinter outputStream) throws Exception {
        tagName = TagFactory.Span;
        super.render(outputStream);
    }

}
