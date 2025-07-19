/**
 *
 */
package eg.java.net.web.jspx.ui.controls.html.elements.dataitem;

import eg.java.net.web.jspx.engine.error.JspxException;
import eg.java.net.web.jspx.engine.parser.TagFactory;
import eg.java.net.web.jspx.engine.util.ui.RenderPrinter;
import eg.java.net.web.jspx.ui.controls.WebControl;
import eg.java.net.web.jspx.ui.controls.html.GenericWebControl;
import eg.java.net.web.jspx.ui.pages.Page;
import org.kxml.parser.XmlParser;

/**
 * DataRow is declarative element that is used to customize the look of and the behavior of the whole row in a Table.
 * There should be only one DataRow per Table, more than this will throw {@link JspxException}
 * The properties of this instance will be applied on all rows in the table. 
 * @author Amr.ElAdawy
 *
 */
public class DataRow extends GenericWebControl {
    private static final long serialVersionUID = -1843862763323299041L;

    /**
     * @param tagName
     */
    public DataRow() {
        super(TagFactory.DataRow);
    }

    /**
     * @param page
     */
    public DataRow(Page page) {
        super(TagFactory.DataRow, page);
    }

    @Override
    public void render(RenderPrinter outputStream) throws Exception {
        // [Aug 5, 2013 10:15:22 AM] [Amr.ElAdawy] [nothing to be rendered here. it is all about the attributes.]
    }

    @Override
    public WebControl deSerialize(XmlParser parser, Page page, WebControl parent) throws Exception {// [Aug 5, 2013 10:20:27 AM] [Amr.ElAdawy] [Overridden to prevent more than one control]
        if (((Table) parent).getDataRow() != null)
            throw new JspxException("Only one DataRow is allowed on a Table. Please remove duplicate DataRows, palced at " + parser.getLineNumber()
                    + ":" + parser.getColumnNumber());
        DataRow element = (DataRow) super.deSerialize(parser, page, parent);
        ((Table) parent).setDataRow(element);
        return element;
    }

    /**
     * declared as public unlike protected parent method.
     */
    public void renderAttributes(RenderPrinter outputStream) throws Exception {
        super.renderAttributes(outputStream);
    }

    @Override
    public void renderStyle(RenderPrinter outputStream) throws Exception {
        super.renderStyle(outputStream);
    }
}
