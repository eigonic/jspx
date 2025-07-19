package eg.java.net.web.jspx.ui.controls.html.elements.dataitem;

import org.kxml.parser.XmlParser;

import eg.java.net.web.jspx.engine.error.JspxException;
import eg.java.net.web.jspx.engine.parser.TagFactory;
import eg.java.net.web.jspx.engine.util.PageCarriage;
import eg.java.net.web.jspx.engine.util.StringUtility;
import eg.java.net.web.jspx.engine.util.ui.RenderPrinter;
import eg.java.net.web.jspx.ui.controls.WebControl;
import eg.java.net.web.jspx.ui.controls.attrbs.JspxAttribute;
import eg.java.net.web.jspx.ui.controls.html.GenericWebControl;
import eg.java.net.web.jspx.ui.controls.html.elements.markers.InternalValueHolder;
import eg.java.net.web.jspx.ui.controls.html.elements.markers.ValueHolder;
import eg.java.net.web.jspx.ui.pages.Page;

/**
 * Control for supporting binding between controls within an EditTemplate and data columns.
 * <br />
 * This control will wrap only one Value Holder Control. It checks after parsing children
 * controls that they are only one value holder control. If else, then an exception is 
 * thrown.
 * <br />
 * The exposed attributes for this control defines the relation between the value holder
 * control inside it and the column to be updated.
 * @author amr.eladawy
 *
 */
public class EditField extends GenericWebControl
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7353109001588608565L;

	public EditField()
	{
		super(TagFactory.EditField);
	}

	/**
	 * @param tagName
	 * @param page
	 */
	public EditField(Page page)
	{
		super(TagFactory.EditField, page);
	}

	@Override
	public WebControl deSerialize(XmlParser parser, Page page, WebControl parent) throws Exception
	{
		EditField newField = (EditField) super.deSerialize(parser, page, parent);
		// [Aug 25, 2009 3:59:32 PM] [amr.eladawy] [check that there is only one value holder at least in the children]

		if (newField.controls.size() > 1)
			throw new JspxException("EditField :" + this + "\r\n has more than one control.");
		if (newField.controls.size() == 1 && !(newField.controls.get(0) instanceof ValueHolder))
			throw new JspxException("EditField :" + this + "\r\n has a none value holder control :" + newField.controls.get(0));
		// [Aug 27, 2009 8:40:04 AM] [amr.eladawy] [here change the id of the value holder control to add the table id]
		newField.controls.get(0).setId(
				new StringBuilder((String) page.carriage.get(PageCarriage.CURRENT_INTERNAL_VALUE_HOLDER_ID)).append(InternalValueHolder.nameSplitter)
						.append(newField.controls.get(0).getId()).toString());
		return newField;
	}

	@Override
	public void renderChildren(RenderPrinter outputStream) throws Exception
	{
		// [Aug 25, 2009 4:07:23 PM] [amr.eladawy] [the following logic to be executed]
		/*
		 * 1. if this is edit then get the value of the current row 
		 * 2. if the data filed is not editable, then print the obtained.
		 * 3. set the value of the child control to the obtained value.
		 */
		Table parentTable = ((Table) page.carriage.get(PageCarriage.CURRENT_INTERNAL_VALUE_HOLDER));
		Object row = parentTable.getRowToEdit();
		Object value = DataColumn.getColumnValue(row, getFieldName());

		if (getVisible())
		{
			if (parentTable.getStatus().equals(Table.EditState))
			{
				if (value != null)
					((ValueHolder) controls.get(0)).setValue(value.toString());
				else if (!StringUtility.isNullOrEmpty(getDefaultValue()))
					((ValueHolder) controls.get(0)).setValue(getDefaultValue());
				if (getReadOnly())
					outputStream.write(value.toString());
				else
					controls.get(0).render(outputStream);
			}
			else
				controls.get(0).render(outputStream);
		}
		// TODO now render the submit controls (Save, Update)
	}

	@JspxAttribute
	protected String fieldname = "fieldname";

	public String getFieldName()
	{
		return getAttributeValue(fieldname);
	}

	public void setFieldName(String fieldnameValue)
	{
		setAttributeValue(fieldname, fieldnameValue);
	}

	@JspxAttribute
	protected String readonly = "readonly";

	public boolean getReadOnly()
	{
		return getAttributeBooleanValue(readonly);
	}

	public void setReadOnly(boolean readonlyValue)
	{
		setAttributeBooleanValue(readonly, readonlyValue);
	}

	@JspxAttribute
	protected String defaultvalue = "defaultvalue";

	public String getDefaultValue()
	{
		return getAttributeValue(defaultvalue);
	}

	public void setDefaultValue(String defaultvalueValue)
	{
		setAttributeValue(defaultvalue, defaultvalueValue);
	}

	@JspxAttribute
	protected String visible = "visible";

	public boolean getVisible()
	{
		return getAttributeBooleanValue(visible);
	}

	public void setVisible(boolean visibleValue)
	{
		setAttributeBooleanValue(visible, visibleValue);
	}
}
