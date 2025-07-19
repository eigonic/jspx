package eg.java.net.web.jspx.ui.controls.html.elements.dataitem;

import java.util.Hashtable;

import eg.java.net.web.jspx.engine.ResourceHandler;
import eg.java.net.web.jspx.engine.data.DataField;
import eg.java.net.web.jspx.engine.parser.TagFactory;
import eg.java.net.web.jspx.engine.util.ResourceUtility;
import eg.java.net.web.jspx.engine.util.StringUtility;
import eg.java.net.web.jspx.engine.util.ui.Render;
import eg.java.net.web.jspx.engine.util.ui.RenderPrinter;
import eg.java.net.web.jspx.ui.controls.attrbs.Attribute;
import eg.java.net.web.jspx.ui.controls.attrbs.JspxAttribute;
import eg.java.net.web.jspx.ui.controls.html.elements.Label;
import eg.java.net.web.jspx.ui.controls.html.elements.inputs.CheckBox;
import eg.java.net.web.jspx.ui.controls.html.elements.markers.IAjaxSubmitter;
import eg.java.net.web.jspx.ui.controls.html.elements.markers.InternalValueHolder;
import eg.java.net.web.jspx.ui.pages.Page;

public class DataColumnCommand extends DataColumn
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2774335508697819046L;

	public static String Delete = "delete";

	public static String Edit = "edit";

	public static String Select = "select";

	public static String Check = "check";

	public static String SelectAll = "selectall";

	@JspxAttribute
	protected String postNormalKey = "postnormal";
	/**
	 * this to define whether to select all values or just the visible.
	 */
	@JspxAttribute
	private static final String CheckType = "checktype";
	/**
	 * this is to indicate whether to check or select when clicking on select all
	 * 
	 * (select , check) the default is select
	 */
	@JspxAttribute
	private static final String SelectType = "selecttype";

	public DataColumnCommand()
	{
		tagName = TagFactory.DataColumnCommand;
	}

	public DataColumnCommand(Page page)
	{
		super(page);
		tagName = TagFactory.DataColumnCommand;
	}

	@JspxAttribute
	private String confirmKey = "confirm";

	public void setConfirm(String confirm)
	{
		setAttributeValue(confirmKey, confirm);
	}

	public String getConfirm()
	{
		return getAttributeValue(confirmKey);
	}

	@JspxAttribute
	private String action = "action";

	public void setAction(String action)
	{
		setAttributeValue(action, action);
	}

	public String getAction()
	{
		return getAttributeValue(action);
	}

	public void setCheckType(String type)
	{
		setAttributeValue(CheckType, type);
	}

	public String getCheckType()
	{
		return getAttributeValue(CheckType);
	}

	public void setSelectType(String type)
	{
		setAttributeValue(SelectType, type);
	}

	public String getSelectType()
	{
		return getAttributeValue(SelectType);
	}

	public void setPostNormal(boolean postNormal)
	{
		setAttributeBooleanValue(postNormalKey, postNormal);
	}

	public boolean getPostNormal()
	{
		return getAttributeBooleanValue(postNormalKey);
	}

	/**
	 * overriden to render this control as a command.
	 */
	@SuppressWarnings("unchecked")
	public void render(RenderPrinter outputStream) throws Exception
	{
		try
		{
			// // TO-DO to be removed
			// if (!toBeRendered())
			// return;
			Table table = ((Table) parent);
			TableEventListener listener = table.getEventListener();
			if (listener != null && !listener.renderDataColumCommand(table.getRow(), this))
				return;

			String str = null;
			String type = getType();
			// if parent is a List table --> make the eventArgs to the rowindex
			if (parent instanceof ListTable)
			{
				int index = ((ListTable) parent).getPageIndex();
				int pageSize = ((ListTable) parent).getPageSize();
				eventArgs = String.valueOf(rowIndex + (pageSize * (index - 1)));
			}
			else
			{
				// if this a datatable, the eventArgs the composed Value of the PK
				// this is not valid for the select command , as the index should be used.
				eventArgs = String.valueOf(rowIndex);
				if (type.equalsIgnoreCase(Delete) || type.equalsIgnoreCase(Edit))
					eventArgs = ((DataTable) parent).composeEventArgs((Hashtable<String, DataField>) row);

			}
			if (type.equalsIgnoreCase(Delete))
			{
				Render.renderImgCommand(outputStream, Table.DELETE_EVENT, eventArgs, ResourceHandler.ResourcePrefix + ResourceUtility.Delete,
						StringUtility.isNullOrEmpty(getText()) ? "Delete" : getText(), getConfirm(), true, true, null, getOnclick(), false, page,
						table.getId(), table);
			}
			else if (type.equalsIgnoreCase(Edit))
			{
				boolean isEdit = table.getStatus().equals(Table.EditState) && rowIndex == table.selectedIndex;
				// [Mar 21, 2012 6:55:30 PM] [amr.eladawy] [temporary set the ajax submitter of the parent table to the table
				// itself]
				IAjaxSubmitter old = table.getMyAjaxSubmitter();
				table.setMyAjaxSubmitter(table);
				if (isEdit)
				{
					// [Mar 21, 2012 7:00:22 PM] [amr.eladawy] [render the cancel and the save]
					Render.renderImgCommand(outputStream, Table.CANCEL_EVENT, eventArgs, ResourceHandler.ResourcePrefix + ResourceUtility.CancelDB,
							StringUtility.isNullOrEmpty(table.getCancelLabel()) ? "Cancel" : table.getCancelLabel(), getConfirm(), true, true, null,
							getOnclick(), false, page, table.getId(), table);
					outputStream.write(Render.NonBreakSpace);
					Render.renderImgCommand(outputStream, Table.UPDATE_EVENT, eventArgs, ResourceHandler.ResourcePrefix + ResourceUtility.SaveDB,
							StringUtility.isNullOrEmpty(table.getUpdateLabel()) ? "Update" : table.getUpdateLabel(), getConfirm(), true, true, null,
							getOnclick(), false, page, table.getId(), table);
				}
				else
				{
					Render.renderImgCommand(outputStream, type, eventArgs + InternalValueHolder.nameSplitter + rowIndex,
							ResourceHandler.ResourcePrefix + ResourceUtility.Edit, StringUtility.isNullOrEmpty(getText()) ? "Edit" : getText(),
							getConfirm(), true, true, null, getOnclick(), false, page, table.getId(), table);
				}
				// [Mar 21, 2012 6:55:55 PM] [amr.eladawy] [then restore it back]
				table.setMyAjaxSubmitter(old);
				return;
			}
			else if (type.equalsIgnoreCase(Select))
			{
				Render.renderCommand(outputStream, getAction(), String.valueOf(eventArgs), getText(), getConfirm(),
						!StringUtility.isNullOrEmpty(type), false, getTarget(), getOnclick(), getPostNormal(), page, table.getId(), table);
				return;
			}
			else if (type.equalsIgnoreCase(Check))
			{
				CheckBox box = new CheckBox();
				box.setCssClass(getCssClass());
				box.setId(table.getId() + InternalValueHolder.nameSplitter + Check + rowIndex);
				// [Apr 23, 2013 12:36:47 PM] [Amr.ElAdawy] [If this checkbox was checked before, then maintain the status]
				String value = page.getPostBackData(box.getId());
				box.setValue(value);
				// System.out.println("the value of the check box [" + box.getId() + "] is [" + value + "]");
				box.render(outputStream);
				return;
			}
		}
		catch (Exception e)
		{
			// TODO: handle exception
		}
	}

	/**
	 * overridden to display check box in case of checkbox
	 */
	public void renderHeader(RenderPrinter outputStream, Attribute headClass) throws Exception
	{

		outputStream.write(Render.startTag);
		outputStream.write(TagFactory.TableHead);
		if (headClass != null && !StringUtility.isNullOrEmpty(headClass.getValue()))
		{
			headClass.renderCustomKey("class", outputStream, page);
		}
		else
			outputStream.write(" class=\"jspxCCWidth\" ");
		outputStream.write(Render.whiteSpace);

		outputStream.write(Render.endTag);

		if (getType().equalsIgnoreCase(Check))
		{
			CheckBox box = new CheckBox();
			box.setCssClass(getCssClass());
			box.setId(((Table) parent).getId() + InternalValueHolder.nameSplitter + SelectAll);
			box.setText(getText());
			// [Nov 18, 2009 11:29:44 AM] [Amr.ElAdawy] [if the select type is check then make it check not select]
			if ("check".equalsIgnoreCase(getSelectType()))
				box.setAttributeValue("onclick", "checkAll('" + ((Table) parent).getId() + InternalValueHolder.nameSplitter + "',this)");
			else
				box.setAttributeValue("onclick", "selectAll('" + ((Table) parent).getId() + InternalValueHolder.nameSplitter + "',this)");
			box.render(outputStream);
			Label label = new Label(page);
			label.setText(getText());
			label.setFor(box.getId());

		}
		else
			outputStream.write(Render.NonBreakSpace);

		Render.renderTagEnd(outputStream, TagFactory.TableHead);
		outputStream.write(Render.newLine);
	}

	@JspxAttribute
	protected String target = "target";

	public String getTarget()
	{
		return getAttributeValue(target);
	}

	public void setTarget(String targetValue)
	{
		setAttributeValue(target, targetValue);
	}

	@JspxAttribute
	protected String onclick = "onclick";

	public String getOnclick()
	{
		return getAttributeValue(onclick);
	}

	public void setOnclick(String onclick1)
	{
		setAttributeValue(onclick, onclick1);
	}
}
