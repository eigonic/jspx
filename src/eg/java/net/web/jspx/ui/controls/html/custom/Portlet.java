/**
 *
 */
package eg.java.net.web.jspx.ui.controls.html.custom;

import eg.java.net.web.jspx.engine.parser.TagFactory;
import eg.java.net.web.jspx.engine.util.ui.RenderPrinter;
import eg.java.net.web.jspx.ui.controls.WebControl;
import eg.java.net.web.jspx.ui.controls.attrbs.JspxAttribute;
import eg.java.net.web.jspx.ui.controls.html.GenericWebControl;
import eg.java.net.web.jspx.ui.controls.html.elements.markers.IAjaxSubmitter;
import eg.java.net.web.jspx.ui.controls.html.elements.markers.ISubmitter;
import eg.java.net.web.jspx.ui.pages.Page;
import eg.java.net.web.jspx.ui.pages.PortletPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * portlet class 
 * @author amr.eladawy
 *
 */
public class Portlet extends GenericWebControl {
    /**
     *
     */
    private static final long serialVersionUID = -1016562194126004186L;
    private static final Logger logger = LoggerFactory.getLogger(Portlet.class);
    @JspxAttribute
    protected String path = "path";
    private PortletPage portletPage;

    /**
     * @param tagName
     */
    public Portlet() {
        super(TagFactory.Portlet);
    }

    /**
     * @param tagName
     * @param page
     */
    public Portlet(Page page) {
        super(TagFactory.Portlet, page);
    }

    public String getPath() {
        return getAttributeValue(path);
    }

    public void setPath(String pathValue) {
        setAttributeValue(path, pathValue);
    }

    @Override
    public WebControl clone(WebControl parent, Page page, ISubmitter submitter, IAjaxSubmitter ajaxSubmitter) {
        try {
            Portlet portlet = (Portlet) super.clone(parent, page, submitter, ajaxSubmitter);
            portlet.setPortletPage(page.loadPortletPage(getPath()));
            page.addPortletPage(portlet.portletPage);
            return portlet;
        } catch (Exception e) {
            logger.error("cannot load Portlet Page for " + getPath(), e);
        }
        return this;
    }

    public void setPortletPage(PortletPage portletPage) {
        this.portletPage = portletPage;
    }

    @Override
    public void render(RenderPrinter outputStream) throws Exception {
        portletPage.renderContent();
    }
}
