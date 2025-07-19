package eg.java.net.web.jspx.ui.controls.html.w3;

import eg.java.net.web.jspx.engine.parser.TagFactory;
import eg.java.net.web.jspx.engine.util.StringUtility;
import eg.java.net.web.jspx.engine.util.ui.RenderPrinter;
import eg.java.net.web.jspx.ui.controls.html.GenericWebControl;
import eg.java.net.web.jspx.ui.pages.Page;

public class IFrame extends GenericWebControl {
    private static final long serialVersionUID = 6579071011208452195L;
    private final String src = "src";

    public IFrame() {
        super(TagFactory.IFrame);
        isContentControl = true;
    }

    public IFrame(Page page) {
        super(TagFactory.IFrame, page);
        isContentControl = true;
    }

    public String getSrc() {
        return getAttributeValue(src);
    }

    public void setSrc(String src) {
        setAttributeValue(this.src, src);
    }

    /**
     * overridden to change the src attribute by adding the absolute url.
     */
    protected void renderAttributes(RenderPrinter outputStream) throws Exception {
        String srcVal = getSrc();
        if (StringUtility.isNullOrEmpty(srcVal))
            attributes.remove(src);
        else
            setSrc(composeURL(srcVal));
        super.renderAttributes(outputStream);
    }

}
