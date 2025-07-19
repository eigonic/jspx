/**
 *
 */
package eg.java.net.web.jspx.ui.controls.html.w3;

import eg.java.net.web.jspx.engine.ResourceHandler;
import eg.java.net.web.jspx.engine.parser.TagFactory;
import eg.java.net.web.jspx.engine.util.ResourceUtility;
import eg.java.net.web.jspx.engine.util.ui.Render;
import eg.java.net.web.jspx.engine.util.ui.RenderPrinter;
import eg.java.net.web.jspx.ui.controls.html.GenericWebControl;
import eg.java.net.web.jspx.ui.controls.html.elements.Image;
import eg.java.net.web.jspx.ui.pages.Page;

/**
 * @author amr.eladawy
 *
 */
public class Body extends GenericWebControl {

    /**
     *
     */
    private static final long serialVersionUID = -5749671135622186812L;

    public Body() {
        super(TagFactory.Body);
    }

    public Body(Page page) {
        super(TagFactory.Body, page);
    }

    @Override
    protected void preRenderEndTag(RenderPrinter outputStream) throws Exception {
        Image img = Render.createImage(ResourceHandler.ResourcePrefix + ResourceUtility.AjaxLoaderImg, "working", page);
        img.setCssClass("jspxloadingImage");
        img.addStyle("display:none");
        img.render(outputStream);
        page.renderOnloadScripts(outputStream);
        // [21 Sep 2015 10:10:15] [aeladawy] [render hidden Ajax Panel]

        Render.createHiddenAjaxPane(page).render(outputStream);
    }
}
