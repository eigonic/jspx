package eg.java.net.web.jspx.ui.controls.html.elements.dataitem;

import eg.java.net.web.jspx.engine.error.JspxException;
import eg.java.net.web.jspx.engine.parser.TagFactory;
import eg.java.net.web.jspx.engine.util.ELUtility;
import eg.java.net.web.jspx.engine.util.StringUtility;
import eg.java.net.web.jspx.engine.util.ui.RenderPrinter;
import eg.java.net.web.jspx.ui.controls.WebControl;
import eg.java.net.web.jspx.ui.controls.html.elements.hidden.HiddenGenericWebControl;
import eg.java.net.web.jspx.ui.pages.Page;
import org.kxml.parser.XmlParser;

/**
 * Control that is containing Edit Template for Item within Table.
 * <br />
 * it has a mix of normal html along with  EditField Controls.
 *
 * @author amr.eladawy
 */
public class EditTemplate extends HiddenGenericWebControl {

    /**
     *
     */
    private static final long serialVersionUID = 5858519193981074165L;

    protected String var;

    protected Object row;

    public EditTemplate() {
        super(TagFactory.EditTemplate);
    }

    public EditTemplate(Page page) {
        super(TagFactory.EditTemplate, page);
    }

    @Override
    /**
     * overridden to make the custom render of the column.
     */
    public void render(RenderPrinter outputStream) throws Exception {
        if (!isRendered())
            return;
        // set the request with the current row.
        page.request.setAttribute(Page.RepeaterNamePrefix + getVar(), row);
        // [Jul 16, 2013 1:08:45 PM] [Amr.ElAdawy] [adding the repeater object to the jexl context]
        ELUtility.addObjectToJexlMap(page, getVar(), row);

        renderChildren(outputStream);
    }

    @Override
    public WebControl deSerialize(XmlParser parser, Page page, WebControl parent) throws Exception {
        EditTemplate control = (EditTemplate) super.deSerialize(parser, page, parent);
        if (!StringUtility.isNullOrEmpty(control.getVar()))
            throw new JspxException("Edit Template [" + this + "] has no attribute [var] ");
        return control;
    }

    public Object getRow() {
        return row;
    }

    public void setRow(Object row) {
        this.row = row;
    }

    public String getVar() {
        return var;
    }

    public void setVar(String var) {
        this.var = var;
    }

}
