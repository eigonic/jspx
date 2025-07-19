package eg.java.net.web.jspx.ui.controls.html.w3;

import eg.java.net.web.jspx.engine.parser.TagFactory;
import eg.java.net.web.jspx.engine.util.StringUtility;
import eg.java.net.web.jspx.engine.util.ui.RenderPrinter;
import eg.java.net.web.jspx.ui.controls.html.GenericWebControl;
import eg.java.net.web.jspx.ui.pages.Page;

public class Link extends GenericWebControl {

    /**
     *
     */
    private static final long serialVersionUID = 6579071011208452195L;
    private final String Href = "href";

    public Link() {
        super(TagFactory.Link);
        isContentControl = false;
    }

    public Link(Page page) {
        super(TagFactory.Link, page);
        isContentControl = false;
    }

    public String getHref() {
        return getAttributeValue(Href);
    }

    public void setHref(String hrefVal) {
        setAttributeValue(Href, hrefVal);
    }

    /**
     * overridden to change the href attribute by adding the absolute url.
     */
    protected void renderAttributes(RenderPrinter outputStream) throws Exception {
        String href = getHref();
        if (StringUtility.isNullOrEmpty(href))
            attributes.remove(Href);
        else
            setHref(composeURL(href));
        super.renderAttributes(outputStream);
    }

}
