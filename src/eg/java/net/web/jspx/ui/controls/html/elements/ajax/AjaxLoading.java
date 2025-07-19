/**
 *
 */
package eg.java.net.web.jspx.ui.controls.html.elements.ajax;

import eg.java.net.web.jspx.engine.ResourceHandler;
import eg.java.net.web.jspx.engine.parser.TagFactory;
import eg.java.net.web.jspx.engine.util.ResourceUtility;
import eg.java.net.web.jspx.engine.util.ui.RenderPrinter;
import eg.java.net.web.jspx.ui.controls.attrbs.JspxAttribute;
import eg.java.net.web.jspx.ui.controls.html.GenericWebControl;
import eg.java.net.web.jspx.ui.pages.Page;

/**
 * loading sign to be displayed when there is an ajax call.
 *
 * @author amr.eladawy
 *
 */
public class AjaxLoading extends GenericWebControl {
    /**
     *
     */
    private static final long serialVersionUID = -4721124993850434527L;
    @JspxAttribute
    protected static String showbackground = "showbackground";

    public AjaxLoading() {
        super(TagFactory.AjaxLoading);
    }

    public AjaxLoading(Page page) {
        super(TagFactory.AjaxLoading, page);
    }

    public String getTageName() {
        setId(parent.getId() + getClass().getSimpleName());
        StringBuilder styleBuilder = new StringBuilder("display:none;z-index:100;");
        if (getShowBackground()) {
            styleBuilder.append("background-image: url(").append(page.request.getContextPath()).append(ResourceHandler.ResourcePrefix)
                    .append(ResourceUtility.AjaxBG).append(");");
        }
        setStyle(styleBuilder.toString());
        return TagFactory.Span;
    }

    @Override
    protected void postRenderStartTag(RenderPrinter outputStream) throws Exception {
        isContentControl = true;
    }

    @Override
    protected void loadInternalAttributes() {
        super.loadInternalAttributes();
        internalAttribtes.put(showbackground, 0);

    }

    public boolean getShowBackground() {
        return getAttributeBooleanValue(showbackground);
    }

    public void setShowBackground(boolean showbackgroundValue) {
        setAttributeBooleanValue(showbackground, showbackgroundValue);
    }
}
