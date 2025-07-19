/**
 * 
 */
package eg.java.net.web.jspx.ui.controls.html.elements.dataitem;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Hashtable;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eg.java.net.web.jspx.engine.ResourceHandler;
import eg.java.net.web.jspx.engine.data.DataField;
import eg.java.net.web.jspx.engine.error.JspxException;
import eg.java.net.web.jspx.engine.parser.TagFactory;
import eg.java.net.web.jspx.engine.util.PropertyAccessor;
import eg.java.net.web.jspx.engine.util.ResourceUtility;
import eg.java.net.web.jspx.engine.util.StringUtility;
import eg.java.net.web.jspx.engine.util.ui.Render;
import eg.java.net.web.jspx.engine.util.ui.RenderPrinter;
import eg.java.net.web.jspx.ui.controls.WebControl;
import eg.java.net.web.jspx.ui.controls.attrbs.Attribute;
import eg.java.net.web.jspx.ui.controls.attrbs.JspxAttribute;
import eg.java.net.web.jspx.ui.controls.html.GenericWebControl;
import eg.java.net.web.jspx.ui.controls.html.elements.Image;
import eg.java.net.web.jspx.ui.controls.html.elements.hidden.HiddenGenericWebControl;
import eg.java.net.web.jspx.ui.controls.html.elements.markers.IAjaxSubmitter;
import eg.java.net.web.jspx.ui.controls.html.elements.markers.InternalValueHolder;
import eg.java.net.web.jspx.ui.pages.Page;

/**
 * @author amr.eladawy
 * 
 */
public class DataColumn extends HiddenGenericWebControl
{
	private static final long serialVersionUID = 8487960773274953136L;

	private static final Logger logger = LoggerFactory.getLogger(DataColumn.class);

	public static String Lookup = "lookup";

	public static String LookupText = "lookupText";

	public static String LookupValue = "lookupValue";

	public static String Varchar = "string";

	public static String Number = "number";

	public static String Date = "date";
	protected static String SORT_ASC = "asc";
	protected static String SORT_DESC = "desc";

	protected boolean hasItemTemplate;

	public String getLookupText()
	{
		return getAttributeValue(LookupText);
	}

	public void setLookupText(String lookupKey)
	{
		setAttributeValue(LookupText, lookupKey);
	}

	public String getLookupValue()
	{
		return getAttributeValue(LookupValue);
	}

	public void setLookupValue(String lookupValue)
	{
		setAttributeValue(LookupValue, lookupValue);
	}

	@JspxAttribute
	protected String groupBy = "groupBy";

	public boolean getGroupBy()
	{
		return getAttributeBooleanValue(groupBy);
	}

	public void setGroupBy(boolean groupBy)
	{
		setAttributeBooleanValue(this.groupBy, groupBy);
	}

	@JspxAttribute
	protected String total = "total";

	public boolean getTotal()
	{
		return getAttributeBooleanValue(total);
	}

	public void setTotal(boolean total)
	{
		setAttributeBooleanValue(this.total, total);
	}

	@JspxAttribute
	protected String longtext = "longtext";

	public boolean getLongText()
	{
		return getAttributeBooleanValue(longtext);
	}

	public void setLongText(boolean longtext)
	{
		setAttributeBooleanValue(this.longtext, longtext);
	}

	@JspxAttribute
	protected String itemLookup = "itemLookup";

	public String getItemLookup()
	{
		return getAttributeValue(itemLookup);
	}

	public void setItemLookup(String itemLookup)
	{
		setAttributeValue(this.itemLookup, itemLookup);
	}

	@JspxAttribute
	protected String hint = "hint";

	public String getHint()
	{
		return getAttributeValue(hint);
	}

	public void setHint(String hint)
	{
		setAttributeValue(this.hint, hint);
	}

	public DataColumn()
	{
		super(TagFactory.DataColumn);
	}

	public DataColumn(Page page)
	{
		super(TagFactory.DataColumn, page);
	}

	private boolean toBeRendered()
	{
		Attribute rend = getAttribute(rendered);
		// return true if the rendered is not set, default = true
		if (StringUtility.isNullOrEmpty(rend.getValue()))
			return true;

		// TODO we need to use an expression language liberary to handle the
		// expression evaluation
		// currently I just added support to the following expression
		// #{fieldname,value&fieldname,value&fieldname,value}
		// #{fieldname,value||fieldname,value||fieldname,value}
		// I dont support both ||, & in the same expression
		try
		{
			if (rend.getValue().startsWith("#{"))
			{
				String exp = rend.getValue().substring(2, rend.getValue().length() - 1);
				String op = "#{}";
				boolean result = true;
				if (exp.indexOf("||") > 0)
				{
					op = "||";
					result = false;
				}
				else if (exp.indexOf("&") > 0)
					op = "&";
				StringTokenizer tz = new StringTokenizer(exp, op);
				while (tz.hasMoreElements())
				{
					String tok = tz.nextToken();
					if (op.equals("||"))
						result = result || evaluateExp(tok);
					if (op.equals("&"))
						result = result & evaluateExp(tok);
					if (op.equals("#{}"))
						result = evaluateExp(tok);
				}
				return result;
			}
			else
			{
				return rend.getValue().equalsIgnoreCase("true");
			}
		}
		catch (Exception e)
		{
		}
		return true;
	}

	private boolean evaluateExp(String exp)
	{
		String parts[] = exp.split(",");

		Object column = getColumnValue(parts[0]);
		String value = "";
		if (column != null)
		{
			value = column.toString();
		}
		if (value.equalsIgnoreCase(parts[1]))
			return true;
		else
			return false;
	}

	@Override
	public void render(RenderPrinter outputStream) throws Exception
	{
		// [Mar 21, 2012 5:41:43 PM] [amr.eladawy] [render in both EDIT and VIEW]
		try
		{
			if (!isRendered() || !isAccessible())
			{
				outputStream.write(Render.NonBreakSpace);
				return;
			}
			ItemTemplate itemTemplate;
			Table table = (Table) parent;
			// [Apr 7, 2009 12:49:04 PM] [amr.eladawy] if it has item Template render it
			// [Apr 3, 2011 4:37:58 PM] [amr.eladawy] [loop till you find the item template]
			if (controls != null && controls.size() > 0)
			{
				for (WebControl control : controls)
				{
					if (control instanceof ItemTemplate)
					{
						// set the request with the current row.
						page.request.setAttribute(Page.RepeaterNamePrefix + table.getVar(), row);
						itemTemplate = (ItemTemplate) control.clone(this, page, getMySubmitter(), getMyAjaxSubmitter());
						// itemTemplate.setRow(row);
						// itemTemplate.setVar(table.getVar());
						itemTemplate.setRowIndex(rowIndex);
						itemTemplate.render(outputStream);

						return;
					}
				}
			}
			boolean isEdit = table.getStatus().equals(Table.EditState) && rowIndex == table.selectedIndex;
			String thisId = new StringBuilder(table.getId()).append(Table.nameSplitter)
					.append(isEdit ? Table.nameSplitterUpdate : Table.nameSplitterInsert).append(getFieldName()).toString();
			// [Mar 21, 2012 6:47:58 PM] [amr.eladawy] [this is editable only if this is the selected field and that table is in
			// edit mode and this is not a pk]
			boolean isDataTable = table instanceof DataTable;
			if (isDataTable)
				isEdit &= !((DataTable) table).isPK(getFieldName());
			// Check the row instead of checking the parent to avoid the
			// coupling between
			// The Datacolumn and the table Children
			// Current condition check if the row is object or
			// metadata(Hashtable)

			Object fieldvalue = getColumnValue();
			if (StringUtility.isNullOrEmpty(String.valueOf(fieldvalue)) || String.valueOf(fieldvalue).equalsIgnoreCase("null"))
			{
				if (!isEdit)
				{
					outputStream.write(Render.NonBreakSpace);
					return;
				}
			}
			if (!(row instanceof GroupByRow<?, ?>) && getGroupBy())
				fieldvalue = "";
			String finalVal = "";
			if (getType().equalsIgnoreCase(DataColumn.Lookup))
			{
				if (row instanceof Hashtable<?, ?>)
				{
					DataLookup lookup = table.getLookups().get(getFK());
					if (lookup == null)
						throw new JspxException("Data Lookup [" + getFK() + "] is not found, please make sure of the name");
					finalVal = lookup.lookup(fieldvalue.toString()).toString();
				}
				else
				{
					// String fieldname = ((Table)getParent()).listLookups.get(getListLookup()).getValue();
					fieldvalue = PropertyAccessor.getProperty(row, getFieldName());
					DataLookup lookup = table.getLookups().get(getFK());
					if (lookup == null)
						throw new JspxException("Data Lookup [" + getFK() + "] is not found, please make sure of the name");
					finalVal = lookup.lookup(fieldvalue.toString()).toString();
					// finalVal = fieldvalue.toString();
				}
				// [Jan 15, 2013 11:09:36 PM] [Amr.ElAdawy] [In case of Edit, render the actual value of the column not the lookup
				// value]
				if (isEdit)
				{
					if (getReadOnly())
					{
						outputStream.write(finalVal);
						Render.createHidden(thisId, fieldvalue.toString(), page).render(outputStream);
					}
					else
						Render.renderSelect(outputStream, thisId, fieldvalue.toString(), table.lookups.get(getFK()), getCustomAttribute(), page);
				}
				else
					outputStream.write(finalVal);
			}
			else if (getType().equalsIgnoreCase(DataColumn.Date))
			{
				finalVal = fieldvalue.toString();
				// [Jun 18, 2009 6:37:54 PM] [amr.eladawy] [if the Data Column of Type Date, then the specified DateFormat will be
				// used.]
				try
				{

					if (StringUtility.isNullOrEmpty(getDateFormat()))
					{
						finalVal = table.getSimpleDateFormat().format(fieldvalue);
					}
					else
						finalVal = new SimpleDateFormat(getDateFormat()).format(fieldvalue);
				}
				catch (IllegalArgumentException e)
				{
					logger.trace("unable to render Date with value [" + fieldvalue + "]");
				}

				if (isEdit)
				{
					if (getReadOnly())
					{
						outputStream.write(finalVal);
						Render.createHidden(thisId, fieldvalue.toString(), page).render(outputStream);
					}
					else
						Render.renderCalendar(outputStream, thisId, finalVal, getDateFormat(), page);
				}
				else
					outputStream.write(finalVal);
			}
			else
			{
				finalVal = fieldvalue.toString();
				if (getType().equalsIgnoreCase(DataColumn.Number) && !StringUtility.isNullOrEmpty(getDecimalFormat()))
				{
					try
					{
						finalVal = getSimpleDecimalFormat().format(Double.parseDouble(fieldvalue.toString()));
					}
					catch (Exception e)
					{
					}
				}

				String longText = finalVal;
				boolean trimed = false;
				if (getLongText() && finalVal != null && finalVal.length() > 55)
				{
					trimed = true;
					finalVal = finalVal.substring(0, 52);
				}
				if (isEdit)
				{
					if (getReadOnly())
					{
						outputStream.write(finalVal);
						Render.createHidden(thisId, fieldvalue.toString(), page).render(outputStream);
					}
					else
						Render.renderInput(outputStream, thisId, finalVal, getCustomAttribute(), page);
				}
				else
				{
					outputStream.write(finalVal);
					if (trimed)
					{
						String modalId = getId() + InternalValueHolder.nameSplitter + "LongTextModal";
						String scriptPop = "$jspx('#" + modalId + " .modal-body > pre').html('"
								+ longText.replace("\"", "&quot;").replace("'", "\\'").replace("<", "&lt;").replace(">", "&gt;") + "');$jspx('#"
								+ modalId + "').modal('show');";
						String showModalButton = " <button type=\"button\" onclick=\"" + scriptPop
								+ "\" class=\"btn btn-info jspx_long_text_trimed\">...</button>";
						outputStream.write(showModalButton);
					}
				}
			}
			if (isEdit && getRequired())
				outputStream.write("*");
		}
		catch (JspxException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			logger.error("render(outputStream=" + outputStream + ")", e);
		}
	}

	public Object getColumnValue()
	{
		return getColumnValue(row, getFieldName());
	}

	@SuppressWarnings("unchecked")
	public Object getColumnValue(String field)
	{

		Object fieldvalue = null;

		if (row instanceof Hashtable<?, ?>)
			fieldvalue = ((Hashtable<String, DataField>) row).get(field.toLowerCase()).getValue();
		else
			fieldvalue = PropertyAccessor.getProperty(row, field);
		return fieldvalue;
	}

	/**
	 * used to render the header of the Column
	 * 
	 * @param outputStream
	 * @throws Exception
	 */
	public void renderHeader(RenderPrinter outputStream, Attribute headClass) throws Exception
	{
		String args = "", iconClass = "";
		if (getSortable())
		{
			args = getFieldName() + InternalValueHolder.nameSplitter + SORT_DESC;

			// [Jan 18, 2013 1:49:10 AM] [Amr.ElAdawy] [render based on twitter bootstrap]

			if (!StringUtility.isNullOrEmpty(getSortDirection()))
			{
				if (getSortDirection().equalsIgnoreCase(SORT_DESC))
				{
					args = getFieldName() + InternalValueHolder.nameSplitter + SORT_ASC;
					iconClass = "icon-arrow-down";
				}
				else
					iconClass = "icon-arrow-up";
			}
		}

		String loadImageId = String.valueOf(System.nanoTime()) + "_ajax_loader_img";
		// else
		// iconClass = "icon-retweet";

		// [Jul 5, 2013 8:45:20 AM] [Amr.ElAdawy] [the whole header cell will be clickable]
		// Render.renderColumnStart(outputStream, headClass, TagFactory.TableHead, page);
		outputStream.write(Render.startTag);
		outputStream.write(TagFactory.TableHead);
		if (headClass != null && !StringUtility.isNullOrEmpty(headClass.getValue()))
		{
			headClass.renderCustomKey("class", outputStream, page);
		}
		outputStream.write(Render.whiteSpace);

		if (getSortable())
		{
			Attribute cursorStyle = new Attribute("style", "cursor:pointer");
			cursorStyle.render(outputStream, page);
			Attribute onClickAttrib = new Attribute("onclick", "$app('#" + loadImageId + "').show();"
					+ Render.composeAction(parent.getId(), "sort", args, "", true, "", false, true, parent.getMySubmitter(), (IAjaxSubmitter) parent));

			onClickAttrib.render(outputStream, page);
		}
		if (!StringUtility.isNullOrEmpty(getHint()))
		{
			Attribute toolTip = new Attribute("data-toggle", "tooltip");
			toolTip.render(outputStream, page);
			toolTip = new Attribute("title", getHint());
			toolTip.render(outputStream, page);
		}
		outputStream.write(Render.endTag);

		String text = getText();
		if (!StringUtility.isNullOrEmpty(text))
			outputStream.write(text);

		outputStream.write(Render.NonBreakSpace);

		// Render.renderBootCommand(outputStream, "sort", args, "", "", true, true, null, null, false, iconClass, page,
		// parent.getId(), parent);
		if (!StringUtility.isNullOrEmpty(getSortDirection()))
		{
			GenericWebControl gwc = new GenericWebControl("i", page);

			gwc.setCssClass(iconClass);
			gwc.render(outputStream);
			// [Jan 18, 2013 1:57:49 AM] [Amr.ElAdawy] [if there is sorting, then render desorting]
			Render.renderBootCommand(outputStream, "desort", args, "", "", true, true, null, "$app('#" + loadImageId + "').show();", false,
					"icon-random", false, page, parent.getId(), parent);
		}

		// [31 Mar 2015 10:56:36] [aeladawy] [render the image of the loading for sorting ]
		Image loadImg = Render.createImage(ResourceHandler.ResourcePrefix + ResourceUtility.AjaxLoaderImg, "working", page);
		loadImg.getStyle().put("display", new Attribute("display", "none"));
		loadImg.setCssClass("jspx_loading_img");
		loadImg.setId(loadImageId);
		loadImg.render(outputStream);

		// [3 May 2015 14:45:57] [aeladawy] [render the modal for long text]
		String modalHtml = "<div id=\"" + getId() + InternalValueHolder.nameSplitter + "LongTextModal"
				+ "\" class=\"modal hide fade\" tabindex=\"-1\" role=\"dialog\" aria-labelledby=\"myModalLabel\" aria-hidden=\"true\">\r\n"
				+ "  <div class=\"modal-header\">\r\n"
				+ "    <button type=\"button\" class=\"close\" data-dismiss=\"modal\" aria-hidden=\"true\">&times;</button>\r\n"
				+ "    <h3 id=\"myModalLabel\">" + getText() + "</h3>\r\n" + "  </div>\r\n" + "  <div class=\"modal-body\"><pre>\r\n" + "    \r\n"
				+ "  </pre></div>\r\n" + "</div>";
		outputStream.write(modalHtml);

		Render.renderTagEnd(outputStream, TagFactory.TableHead);
		outputStream.write(Render.newLine);
	}

	private String ColName = "fieldname";

	public void setFieldName(String name)
	{
		setAttributeValue(ColName, name);
	}

	public String getFieldName()
	{
		// [Jun 10, 2009 6:21:26 PM] [amr.eladawy] we trim the field name to help with typo mistakes
		return getAttributeValue(ColName).trim();
	}

	private String textKey = "text";

	public void setText(String headerVal)
	{
		setAttributeValue(textKey, headerVal);
	}

	public String getText()
	{
		return getAttributeValue(textKey);
	}

	private String type = "type";

	public void setType(String typeName)
	{
		setAttributeValue(type, typeName.toLowerCase());
	}

	public String getType()
	{
		return getAttributeValue(type);
	}

	private String fk = "lookup";

	public void setFK(String dataLookup)
	{
		setAttributeValue(fk, dataLookup);
	}

	public String getFK()
	{
		return getAttributeValue(fk);
	}

	private String required = "required";

	public void setRequired(boolean bool)
	{
		setAttributeBooleanValue(required, bool);
	}

	public boolean getRequired()
	{
		return getAttributeBooleanValue(required);
	}

	protected Object row;

	public void setRow(Object row)
	{
		this.row = row;
	}

	protected int rowIndex;

	public void setRowIndex(int rowIndex)
	{
		this.rowIndex = rowIndex;
	}

	protected String eventArgs;

	public void setEventArgs(String eventArgs)
	{
		this.eventArgs = eventArgs;
	}

	@JspxAttribute
	private String readOnly = "readonly";

	public void setReadOnly(boolean readonly)
	{
		setAttributeBooleanValue(readOnly, readonly);
	}

	public boolean getReadOnly()
	{
		return getAttributeBooleanValue(readOnly);
	}

	@JspxAttribute
	private String dateFormat = "dateformat";

	public void setDateFormat(String dateFormat)
	{
		setAttributeValue(dateFormat, dateFormat);
	}

	public String getDateFormat()
	{
		return getAttributeValue(dateFormat);
	}

	public SimpleDateFormat getSimpleDateFormat()
	{
		if (StringUtility.isNullOrEmpty(getDateFormat()))
			return (parent == null ? new SimpleDateFormat("dd/MM/yyyy HH:mm:ss") : ((Table) parent).getSimpleDateFormat());
		else
			return new SimpleDateFormat(getDateFormat());
	}

	@JspxAttribute
	private String decimalFormat = "decimalformat";

	public void setDecimalFormat(String decimalFormat)
	{
		setAttributeValue(decimalFormat, decimalFormat);
	}

	public String getDecimalFormat()
	{
		return getAttributeValue(decimalFormat);
	}

	public DecimalFormat getSimpleDecimalFormat()
	{
		if (StringUtility.isNullOrEmpty(getDecimalFormat()))
			return new DecimalFormat("#.##");
		else
			return new DecimalFormat(getDecimalFormat());
	}

	@JspxAttribute
	protected String customAttribute = "customAttribute";

	public String getCustomAttribute()
	{
		return getAttributeValue(customAttribute);
	}

	public void setCustomAttribute(String att)
	{
		setAttributeValue(customAttribute, att);
	}

	@JspxAttribute
	protected String sortable = "sortable";

	public boolean getSortable()
	{
		return getAttributeBooleanValue(sortable);
	}

	public void setSortable(boolean sortableValue)
	{
		setAttributeBooleanValue(sortable, sortableValue);
	}

	/**
	 * the sorting direction of this column.
	 */
	@JspxAttribute
	private String sortDirection = "sortdir";

	public String getSortDirection()
	{
		return getAttributeValue(sortDirection);
	}

	public void setSortDirection(String sortDirectionVal)
	{
		setAttributeValue(sortDirection, sortDirectionVal);
	}

	/**
	 * extracts the value of a certain column out of the given row
	 * 
	 * @param rowObject
	 * @param fieldName
	 *            the name of the column to get its value.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Object getColumnValue(Object rowObject, String fieldName)
	{
		Object fieldvalue = null;

		if (rowObject instanceof Hashtable<?, ?>)
		{
			DataField dataField = ((Hashtable<String, DataField>) rowObject).get(fieldName.toLowerCase());
			if (dataField == null)
				logger.warn("The Field Name [" + fieldName + "] is not found...");
			else
				fieldvalue = dataField.getValue();
		}
		else
			fieldvalue = PropertyAccessor.getProperty(rowObject, fieldName);
		return fieldvalue;
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

	public boolean hasItemTemplate()
	{
		return hasItemTemplate;
	}

	public void setHasItemTemplate(boolean hastemplate)
	{
		hasItemTemplate = hastemplate;
	}

	@JspxAttribute
	protected String export = "export";

	public boolean getExport()
	{
		String val = getAttribute(export.toLowerCase()).getValue(page);
		val = StringUtility.isNullOrEmpty(val) ? TRUE : val;

		return TRUE.equalsIgnoreCase(val);
	}

	public void setExport(boolean export)
	{
		setAttributeBooleanValue(this.export, export);
	}

	@Override
	public void renderStyle(RenderPrinter outputStream) throws Exception
	{
		super.renderStyle(outputStream);
	}
}
