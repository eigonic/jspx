/**
 *
 */
package eg.java.net.web.jspx.ui.controls.html;

import eg.java.net.web.jspx.engine.parser.PageModelComposer;
import eg.java.net.web.jspx.ui.controls.WebControl;
import eg.java.net.web.jspx.ui.controls.attrbs.Attribute;
import eg.java.net.web.jspx.ui.pages.Page;
import org.kxml.Xml;
import org.kxml.io.ParseException;
import org.kxml.parser.ParseEvent;
import org.kxml.parser.XmlParser;

import java.util.HashMap;

/**
 * @author Sherin.Ghonaim
 *
 */
public class StringLiteralContainer extends GenericWebControl {

    /**
     *
     */
    private static final long serialVersionUID = 8099982191640653553L;

    /**
     * @param tagName
     */
    public StringLiteralContainer(String tagName) {
        super(tagName);

    }

    public StringLiteralContainer(String tagName, Page page) {
        super(tagName, page);

    }

    public String getText() {
        return getContentControl().getText();
    }

    public void setText(String text) {
        getContentControl().setText(text);
        if (page != null && page.PageStatus != Page.PageViewState && page.PageStatus != Page.PageCompose && page.PageStatus != Page.PageRendering)
            viewstate.put(content, text);
    }

    protected StringLiteral getContentControl() {
        StringLiteral literal = null;
        if (controls.size() > 0)
            literal = ((StringLiteral) controls.get(0));
        else {
            literal = new StringLiteral("");
            literal.setPage(page);
            addControl(literal);
        }
        return literal;
    }

    public WebControl deSerialize(XmlParser parser, Page page, WebControl parent) throws Exception {
        ParseEvent peek = parser.peek();
        String tagPlace = parser.getLineNumber() + ":" + parser.getColumnNumber();
        parseStartTag(parser);
        StringBuilder text = new StringBuilder();
        while (!parser.peek(Xml.END_TAG, null, tagName)) {
            peek = parser.peek();
            if (peek.getType() == Xml.TEXT)
                text.append(parser.readText());
            else if (peek.getType() == Xml.WHITESPACE)
                text.append(parser.readText());
            else if (peek.getType() == Xml.START_TAG || peek.getType() == Xml.END_TAG)
                throw new ParseException("The control [" + tagName + "] palced at " + tagPlace + ", Should not have another control ["
                        + peek.getName() + "] inside it.", null, parser.getLineNumber(), parser.getColumnNumber());
            else if (peek.getType() == Xml.END_DOCUMENT || (peek.getType() == Xml.END_TAG && !peek.getName().equalsIgnoreCase(tagName)))
                throw new ParseException("The tag [" + tagName + "] palced at " + tagPlace + " has no matching closing tag ["
                        + (peek.getName() == null ? "self closing tag /> " : peek.getName()) + "]", null, parser.getLineNumber(),
                        parser.getColumnNumber());
        }
        setText(text.toString());
        PageModelComposer.ensureTag(parser, Xml.END_TAG, tagName);
        setPage(page);
        getContentControl().setPage(page);
        // return wireToDeclaredControl();
        return this;
    }

    /**
     * overrides the parent to obtain the content and then put it into the
     * string literal.
     */
    public void loadViewState(HashMap<String, String> viewState) {
        super.loadViewState(viewState);
        Attribute contentAtt = attributes.get(content);
        if (contentAtt != null) {
            getContentControl().setText(contentAtt.getValue(page));
            attributes.remove(content);
        }
    }

}
