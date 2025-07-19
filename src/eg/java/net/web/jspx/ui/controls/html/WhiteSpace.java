/**
 *
 */
package eg.java.net.web.jspx.ui.controls.html;

import eg.java.net.web.jspx.engine.util.ui.RenderPrinter;
import eg.java.net.web.jspx.ui.controls.WebControl;
import eg.java.net.web.jspx.ui.controls.html.elements.markers.IAjaxSubmitter;
import eg.java.net.web.jspx.ui.controls.html.elements.markers.ISubmitter;
import eg.java.net.web.jspx.ui.pages.Page;
import org.kxml.parser.XmlParser;

import java.util.HashMap;

/**
 * Internal Jspx Control used to hold the white space in the document.
 * While the parser is running into the document, the white space event will be stored in this control.
 *
 * There will be no cloned version of control as there should not be any change applied by user code. 
 * @author Amr.ElAdawy
 *
 */
public class WhiteSpace extends WebControl {
    /**
     *
     */
    private static final long serialVersionUID = 8674293555956857914L;
    String text;

    public WhiteSpace(String text) {
        this.text = text;
    }

    /* (non-Javadoc)
     * @see eg.java.net.web.jspx.ui.controls.WebControl#render(eg.java.net.web.jspx.engine.util.ui.RenderPrinter)
     */
    @Override
    public void render(RenderPrinter outputStream) throws Exception {
        outputStream.write(text);
    }

    /* (non-Javadoc)
     * @see eg.java.net.web.jspx.ui.controls.WebControl#deSerialize(org.kxml.parser.XmlParser, eg.java.net.web.jspx.ui.pages.Page, eg.java.net.web.jspx.ui.controls.WebControl)
     */
    @Override
    public WebControl deSerialize(XmlParser parser, Page page, WebControl parent) throws Exception {
        return this;
    }

    /* (non-Javadoc)
     * @see eg.java.net.web.jspx.ui.controls.WebControl#clone(eg.java.net.web.jspx.ui.controls.WebControl, eg.java.net.web.jspx.ui.pages.Page, eg.java.net.web.jspx.ui.controls.html.elements.markers.ISubmitter, eg.java.net.web.jspx.ui.controls.html.elements.markers.IAjaxSubmitter)
     */
    @Override
    public WebControl clone(WebControl parent, Page page, ISubmitter submitter, IAjaxSubmitter ajaxSubmitter) {
        return this;
    }

    @Override
    public void saveViewState(HashMap<String, HashMap<String, String>> viewState) {
    }

    @Override
    protected void renderStyle(RenderPrinter outputStream) throws Exception {
    }

    @Override
    protected void renderAttributes(RenderPrinter outputStream) throws Exception {
    }
}
