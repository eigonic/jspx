/**
 *
 */
package eg.java.net.web.jspx.ui.controls.html.w3;

import eg.java.net.web.jspx.engine.parser.TagFactory;
import eg.java.net.web.jspx.engine.util.StringUtility;
import eg.java.net.web.jspx.engine.util.ui.RenderPrinter;
import eg.java.net.web.jspx.ui.controls.WebControl;
import eg.java.net.web.jspx.ui.controls.attrbs.Attribute;
import eg.java.net.web.jspx.ui.controls.html.GenericWebControl;
import eg.java.net.web.jspx.ui.controls.html.StringLiteral;
import eg.java.net.web.jspx.ui.pages.Page;
import org.kxml.parser.XmlParser;

/**
 * @author amr.eladawy
 *
 */
public class Script extends GenericWebControl {

    /**
     *
     */
    private static final long serialVersionUID = -4693922140541749564L;

    protected static String scriptSrcKey = "src";

    private final StringLiteral scriptCode = new StringLiteral("");

    /**
     * @param tagName
     */
    public Script() {
        super(TagFactory.Script);
        initiateAttributes();
        addControl(scriptCode);
    }

    /**
     * @param tagName
     */
    public Script(String src, Page page) {
        this(page);
        setSrc(src);
    }

    /**
     * @param tagName
     */
    public Script(String src) {
        this();
        setSrc(src);
    }

    /**
     * @param tagName
     * @param page
     */
    public Script(Page page) {
        super(TagFactory.Script, page);
        initiateAttributes();
        addControl(scriptCode);
    }

    private void initiateAttributes() {
        addAttribute(new Attribute("type", "text/javascript"));
        addAttribute(new Attribute("language", "javascript"));
    }

    public void setScriptCode(String script) {
        scriptCode.setText(script);
    }

    public WebControl deSerialize(XmlParser parser, Page page, WebControl parent) throws Exception {

        parseStartTag(parser);
        setScriptCode(parser.readText());
        parseEndTage(parser);
        setPage(page);
        return this;
    }

    public String getSrc() {
        return getAttributeValue(scriptSrcKey);
    }

    public void setSrc(String src) {
        setAttributeValue(scriptSrcKey, src);
    }

    /**
     * Overridden to change the src attribute by adding the absolute url.
     */
    protected void renderAttributes(RenderPrinter outputStream) throws Exception {
        loadInternalAttributes();
        String src = getSrc();
        if (StringUtility.isNullOrEmpty(src))
            attributes.remove(scriptSrcKey);
        else
            setSrc(composeURL(src));
        super.renderAttributes(outputStream);
    }
}
