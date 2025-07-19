/**
 *
 */
package eg.java.net.web.jspx.ui.controls.html.w3;

import eg.java.net.web.jspx.engine.parser.TagFactory;
import eg.java.net.web.jspx.ui.controls.html.GenericWebControl;
import eg.java.net.web.jspx.ui.pages.Page;

/**
 * HTML Head Meta Data.
 * just overrides the {@link #loadInternalAttributes()}
 * @author amr.eladawy
 *
 */
public class Meta extends GenericWebControl {

    /**
     *
     */
    private static final long serialVersionUID = -571603639179045074L;

    /**
     * @param tagName
     */
    public Meta() {
        super(TagFactory.Meta);
        isContentControl = false;
    }

    /**
     * @param tagName
     * @param page
     */
    public Meta(Page page) {
        super(TagFactory.Meta, page);
        isContentControl = false;
    }

    @Override
    protected void loadInternalAttributes() {
        super.loadInternalAttributes();
        internalAttribtes.remove(content);
    }
}
