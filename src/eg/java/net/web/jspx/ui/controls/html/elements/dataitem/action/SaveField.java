/**
 * 
 */
package eg.java.net.web.jspx.ui.controls.html.elements.dataitem.action;

import org.kxml.parser.XmlParser;

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

/**
 * Controls that is used a a marker around an invoker like (input, LinkCommand) 
 * to represent the save action performed on a Edit Template
 * @author amr.eladawy
 *
 */
public class SaveField extends HiddenGenericWebControl
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 536703451524880351L;

	public SaveField()
	{
		super(TagFactory.SaveField);
	}

	public SaveField(Page page)
	{
		super(TagFactory.SaveField, page);
	}

	@Override
	public WebControl deSerialize(XmlParser parser, Page page, WebControl parent) throws Exception
	{
		SaveField newField = (SaveField) super.deSerialize(parser, page, parent);
		// [Aug 25, 2009 3:59:32 PM] [amr.eladawy] [check that there is only one Control
		// at least in the children and it should be either input or LinkCommand]

		if (newField.controls.size() > 1)
			throw new JspxException("SaveField :" + this + "\r\n has more than one control.");
		if (newField.controls.size() == 1
				&& (!(newField.controls.get(0) instanceof Button) || !(newField.controls.get(0) instanceof ImageButton)
						|| !(newField.controls.get(0) instanceof Submit) || !(newField.controls.get(0) instanceof LinkCommand)))
			throw new JspxException("SaveField :" + this + "\r\n has a none invoker control :" + newField.controls.get(0));

		return newField;
	}

	@Override
	public void renderChildren(RenderPrinter outputStream) throws Exception
	{
		// [Sep 5, 2009 2:09:32 PM] [amr.eladawy] [here I need to override the action of the invoker control
		// in order to invoke the internal action  Save]
		Table parentTable = ((Table) page.carriage.get(PageCarriage.CURRENT_INTERNAL_VALUE_HOLDER));

		if (controls.size() == 0)
		{
			// [Sep 5, 2009 5:41:15 PM] [amr.eladawy] [there is no template, rendering default.]
			Render.renderCommand(outputStream, Table.SAVE_EVENT, "", "Save", null, true, true, null, null, false, page, parentTable.getId(),
					parentTable);

		}
		else
		{
			ActionTempate.overrideAction(controls.get(0), Table.SAVE_EVENT, parentTable, mySubmitter, getMyAjaxSubmitter() );
			controls.get(0).render(outputStream);
		}
	}
}
