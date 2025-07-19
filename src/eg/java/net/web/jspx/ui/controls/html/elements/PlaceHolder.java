/**
 *
 */
package eg.java.net.web.jspx.ui.controls.html.elements;

import eg.java.net.web.jspx.engine.parser.TagFactory;
import eg.java.net.web.jspx.engine.util.ui.RenderPrinter;
import eg.java.net.web.jspx.ui.controls.WebControl;
import eg.java.net.web.jspx.ui.controls.html.GenericWebControl;
import eg.java.net.web.jspx.ui.pages.MasterPage;
import eg.java.net.web.jspx.ui.pages.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * place older o nthe master page indicating the presence of some content page
 * datat over here.
 *
 * @author amr.eladawy
 *
 */
public class PlaceHolder extends GenericWebControl {
    /**
     *
     */
    private static final long serialVersionUID = -756707309914242486L;
    private static final Logger logger = LoggerFactory.getLogger(PlaceHolder.class);

    /**
     * @param tagName
     */
    public PlaceHolder() {
        super(TagFactory.PlaceHolder);
        isContentControl = false;
    }

    /**
     * @param tagName
     * @param page
     */
    public PlaceHolder(Page page) {
        super(TagFactory.PlaceHolder, page);
        isContentControl = false;
    }

    /**
     * overridden to get the content page and render the content holder
     * corresponding for this place holder.
     */
    public void render(RenderPrinter outputStream) throws Exception {
        if (page instanceof MasterPage) {
            List<WebControl> controls = ((MasterPage) page).getContentPage().getControls();
            WebControl control = null;
            for (int i = 0; i < controls.size(); i++) {
                control = controls.get(i);
                if (control.getId().equals(getId())) {
                    control.render(outputStream);
                    return;
                }
            }
            logger.warn("The place Holder [" + getId() + "] in the master page [" + page.getFilePath()
                    + "], has no matching content holder in the content page [" + ((MasterPage) page).getContentPage().getFilePath() + "]");
        }
    }
}
