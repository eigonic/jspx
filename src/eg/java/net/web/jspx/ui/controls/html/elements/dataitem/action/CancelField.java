/**
 *
 */
package eg.java.net.web.jspx.ui.controls.html.elements.dataitem.action;

import eg.java.net.web.jspx.engine.error.JspxException;
import eg.java.net.web.jspx.engine.parser.TagFactory;
import eg.java.net.web.jspx.engine.util.PageCarriage;
import eg.java.net.web.jspx.engine.util.ui.Render;
import eg.java.net.web.jspx.engine.util.ui.RenderPrinter;
import eg.java.net.web.jspx.ui.controls.WebControl;
import eg.java.net.web.jspx.ui.controls.html.elements.LinkCommand;
import eg.java.net.web.jspx.ui.controls.html.elements.dataitem.Table;
import eg.java.net.web.jspx.ui.controls.html.elements.hidden.HiddenGenericWebControl;
import eg.java.net.web.jspx.ui.controls.html.elements.inputs.Button;
import eg.java.net.web.jspx.ui.controls.html.elements.inputs.ImageButton;
import eg.java.net.web.jspx.ui.controls.html.elements.inputs.Submit;
import eg.java.net.web.jspx.ui.pages.Page;
import org.kxml.parser.XmlParser;

/**
 * Controls that is used a a marker around an invoker like (input, LinkCommand) 
 * to represent the save action performed on a Edit Template
 * @author amr.eladawy
 *
 */
public class CancelField extends HiddenGenericWebControl {

    /**
     *
     */
    private static final long serialVersionUID = -5305639777096661939L;

    public CancelField() {
        super(TagFactory.SaveField);
    }

    public CancelField(Page page) {
        super(TagFactory.SaveField, page);
    }

    @Override
    public WebControl deSerialize(XmlParser parser, Page page, WebControl parent) throws Exception {
        CancelField newField = (CancelField) super.deSerialize(parser, page, parent);
        // [Aug 25, 2009 3:59:32 PM] [amr.eladawy] [check that there is only one Control
        // at least in the children and it should be either input or LinkCommand]

        if (newField.controls.size() > 1)
            throw new JspxException("CancelField :" + this + "\r\n has more than one control.");
        if (newField.controls.size() == 1
                && (!(newField.controls.get(0) instanceof Button) || !(newField.controls.get(0) instanceof ImageButton)
                || !(newField.controls.get(0) instanceof Submit) || !(newField.controls.get(0) instanceof LinkCommand)))
            throw new JspxException("CancelField :" + this + "\r\n has a none invoker control :" + newField.controls.get(0));

        return newField;
    }

    @Override
    public void renderChildren(RenderPrinter outputStream) throws Exception {
        // [Sep 5, 2009 2:09:32 PM] [amr.eladawy] [here I need to override the action of the invoker control
        // in order to invoke the internal action  Save]
        Table parentTable = ((Table) page.carriage.get(PageCarriage.CURRENT_INTERNAL_VALUE_HOLDER));

        if (controls.isEmpty()) {
            // [Sep 5, 2009 5:41:15 PM] [amr.eladawy] [there is no template, rendering default.]
            Render.renderCommand(outputStream, Table.CANCEL_EVENT, "", "Cancel", null, true, true, null, null, false, page, parentTable.getId(),
                    parentTable);

        } else {
            ActionTempate.overrideAction(controls.get(0), Table.CANCEL_EVENT, parentTable, mySubmitter, getMyAjaxSubmitter());
            controls.get(0).render(outputStream);
        }
    }
}
