/**
 *
 */
package eg.java.net.web.jspx.ui.controls.html.w3;

import eg.java.net.web.jspx.engine.ResourceHandler;
import eg.java.net.web.jspx.engine.parser.TagFactory;
import eg.java.net.web.jspx.engine.util.ResourceUtility;
import eg.java.net.web.jspx.engine.util.ui.RenderPrinter;
import eg.java.net.web.jspx.ui.controls.attrbs.Attribute;
import eg.java.net.web.jspx.ui.controls.html.GenericWebControl;
import eg.java.net.web.jspx.ui.pages.Page;

/**
 * @author amr.eladawy
 *
 */

public class Head extends GenericWebControl {

    /**
     *
     */
    private static final long serialVersionUID = 7311246955334350988L;

    public Head() {
        super(TagFactory.Head);

    }

    public Head(Page page) {
        super(TagFactory.Head, page);
    }

    private void registerCss() {
        // calendarCss.setHref(ResourceHandler.ResourcePrefix + ResourceUtility.CalendarCss);
    }

    @Override
    protected void postRenderStartTag(RenderPrinter outputStream) throws Exception {
        new Script(ResourceHandler.ResourcePrefix + ResourceUtility.JQueryScript, page).render(outputStream);
        new Script(ResourceHandler.ResourcePrefix + ResourceUtility.UtilScript, page).render(outputStream);
        // new Script(ResourceHandler.ResourcePrefix + ResourceUtility.CalendarJS);
        new Script(ResourceHandler.ResourcePrefix + ResourceUtility.PostBackScript, page).render(outputStream);
        new Script(ResourceHandler.ResourcePrefix + ResourceUtility.ValidationScript, page).render(outputStream);
        new Script(ResourceHandler.ResourcePrefix + ResourceUtility.AjaxScript, page).render(outputStream);
        new Script(ResourceHandler.ResourcePrefix + ResourceUtility.MaskedScript, page).render(outputStream);
        new Script(ResourceHandler.ResourcePrefix + ResourceUtility.LiveQueryScript, page).render(outputStream);
        new Script(ResourceHandler.ResourcePrefix + ResourceUtility.RatyScript, page).render(outputStream);
        new Script(ResourceHandler.ResourcePrefix + ResourceUtility.GrowlScript, page).render(outputStream);
        new Script(ResourceHandler.ResourcePrefix + ResourceUtility.JQUIScript, page).render(outputStream);
        new Script(ResourceHandler.ResourcePrefix + ResourceUtility.JQUITimeScript, page).render(outputStream);
        new Script(ResourceHandler.ResourcePrefix + ResourceUtility.BootstrapScript, page).render(outputStream);

        // [Sep 12, 2013 5:07:15 PM] [amr.eladawy] [ add CSS Style]
        createCss(ResourceHandler.ResourcePrefix + ResourceUtility.JspxCss).render(outputStream);
        createCss(ResourceHandler.ResourcePrefix + ResourceUtility.GrowlCss).render(outputStream);
        if (page != null && page.getLocale() != null && page.getLocale().getLanguage() != null && page.getLocale().getLanguage().startsWith("ar")) {
            createCss(ResourceHandler.ResourcePrefix + ResourceUtility.BootstrapRTLCss).render(outputStream);
            createCss(ResourceHandler.ResourcePrefix + ResourceUtility.BootstrapResponsiveRTLCss).render(outputStream);
        } else {
            createCss(ResourceHandler.ResourcePrefix + ResourceUtility.BootstrapCss).render(outputStream);
            createCss(ResourceHandler.ResourcePrefix + ResourceUtility.BootstrapResponsiveCss).render(outputStream);
        }
        createCss(ResourceHandler.ResourcePrefix + ResourceUtility.JQUICss).render(outputStream);
    }

    protected Link createCss(String href) {
        Link link = new Link(page);
        link.setHref(href);
        link.addAttribute(new Attribute("type", "text/css"));
        link.addAttribute(new Attribute("rel", "stylesheet"));
        link.setPage(page);
        return link;
    }
}
