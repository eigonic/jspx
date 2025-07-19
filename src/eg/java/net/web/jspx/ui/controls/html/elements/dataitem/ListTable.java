package eg.java.net.web.jspx.ui.controls.html.elements.dataitem;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eg.java.net.web.jspx.engine.data.DataException;
import eg.java.net.web.jspx.engine.data.MAO;
import eg.java.net.web.jspx.engine.parser.TagFactory;
import eg.java.net.web.jspx.engine.util.ELUtility;
import eg.java.net.web.jspx.engine.util.JspxComparator;
import eg.java.net.web.jspx.engine.util.PropertyAccessor;
import eg.java.net.web.jspx.engine.util.StringUtility;
import eg.java.net.web.jspx.engine.util.ui.Render;
import eg.java.net.web.jspx.engine.util.ui.RenderPrinter;
import eg.java.net.web.jspx.ui.controls.attrbs.JspxAttribute;
import eg.java.net.web.jspx.ui.controls.html.elements.markers.InternalValueHolder;
import eg.java.net.web.jspx.ui.pages.Page;

/**
 * @author ahmed.abdelaal
 * 
 */
public class ListTable extends Table
{

	private static final long serialVersionUID = 623935508196733849L;

	/**
	 * flag to mark that itemsList has been set by setList();
	 */
	private static boolean isDirty = false;

	private static Logger logger = LoggerFactory.getLogger(ListTable.class);

	/**
	 * @param tagName
	 */
	public ListTable()
	{
		super(TagFactory.ListTable);
		loadInternalAttributes();
	}

	/**
	 * @param tagName
	 * @param page
	 */
	public ListTable(Page page)
	{
		super(TagFactory.ListTable, page);
		loadInternalAttributes();
	}

	protected List<Object> itemsList = null;

	@JspxAttribute
	protected static String items = "items";
	protected static String bindToClassKey = "bindtoclass";

	protected void loadInternalAttributes()
	{
		super.loadInternalAttributes();
		internalAttribtes.put(items.toLowerCase(), 0);
		internalAttribtes.put(bindToClassKey.toLowerCase(), 0);
	}

	@JspxAttribute
	protected static String saveInSession = "saveInSession";

	public boolean getSaveInSession()
	{
		return StringUtility.isNullOrEmpty(getAttributeValue(saveInSession)) || getAttributeValue(saveInSession).equalsIgnoreCase("true");
	}

	public void setSaveInSession(boolean SaveInSession)
	{
		setAttributeBooleanValue(saveInSession, SaveInSession);
	}

	/**
	 * invokes the binding operation.
	 */
	public void dataBind()
	{
		try
		{
			bound = true;
			// [Sep 23, 2013 11:00:20 PM] [amr.eladawy] [reset the page index in case the event is external]
			if (!internalPostback)
				resetPageIndex();
			int index = getPageIndex();
			int size = getPageSize();
			// [Jun 29, 2009 1:29:44 PM] [Amr.ElAdawy] [if the items are empty, get the items form page]
			if (!isDirty || itemsList == null || itemsList.size() == 0)
				initializeList();

			//added by bakry Mar 6, 2015 to filter the list based on data-params
			for (DataParam param : parameters)
				itemsList = param.filterList(itemsList, getcaseSensitive());

			int toIndex = (itemsList.size() < (index - 1) * size + size) ? itemsList.size() : (index - 1) * size + size;

			// [21 May 2015 13:31:24] [aeladawy] [perform sorting if needed]
			DataColumn sortedColumn = getSortingColumn();
			if (sortedColumn != null)
			{

				// [Jun 17, 2009 12:35:01 PM] [amr.eladawy] [Check of the sorting column has a filed name, if not log error for
				// that ]
				if (StringUtility.isNullOrEmpty(sortedColumn.getFieldName()))
					logger.error("The DataColumn [" + sortedColumn + "] requires FieldName attribute, sorting is ignored.");
				else
				{
					String sortKey = sortedColumn.getFieldName().trim();
					JspxComparator jspxComparator = new JspxComparator(sortKey, sortedColumn.getSortDirection());
					Collections.sort(itemsList, jspxComparator);
				}
			}

			rows = itemsList.subList((index - 1) * size, toIndex);
			totalCount = itemsList.size();
			// [11 Oct 2015 09:57:01] [aeladawy] [fix the issue if the count is x and the last record is x-1]
			nextPage = toIndex <= (totalCount - 1);
			totalPages = totalCount / size;
			if (totalCount % size > 0)
				totalPages++;

			saveListInSession(itemsList);
		}
		catch (Exception e)
		{
			setRendered(false);
			logger.error("Error while data bind, exp: ", e);
		}
	}

	/**
	 * initializes the itemsList, and gets the list to bind to.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Object> initializeList()
	{
		String itemsName = null;
		if (attributes.get(items) != null)
			itemsName = attributes.get(items).getValue();

		if (getSaveInSession())
			itemsList = (List<Object>) page.session.getAttribute(getListSessionKey());
		if (itemsList == null)
		{
			if (!StringUtility.isNullOrEmpty(itemsName))
			{
				itemsList = evalItemsList(itemsName);
				try
				{
					PropertyAccessor.setProperty(this.page, itemsName, itemsList);
				}
				catch (Exception e)
				{
					logger.warn("Unable to set Items value");
				}
			}

		}
		if (itemsList == null)
			itemsList = new ArrayList<Object>();

		isDirty = false;
		return itemsList;
	}

	/**
	 * evaluates the value of the Items List based on the variable name
	 * 
	 * @param itemsName	
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected List<Object> evalItemsList(String itemsName)
	{
		if (StringUtility.isEL(itemsName))
			return (List<Object>) ELUtility.evaluateEL(itemsName, page);
		else
			return (List<Object>) ELUtility.evaluateEL("${this." + itemsName + "}", page);
		// return (List<Object>) PropertyAccessor.getProperty(this.page, itemsName);
	}

	public String getListSessionKey()
	{
		return page.conversationId + ":" + this.getId() + ":Items";
	}

	public List<?> getList()
	{
		return initializeList();
	}

	public void setListGeneric(List<?> list)
	{
		itemsList = new ArrayList<Object>();
		for (Object object : list)
			itemsList.add(object);
		isDirty = true;
		saveListInSession(itemsList);
	}

	public void setList(List list)
	{
		itemsList = list;
		isDirty = true;
		saveListInSession(list);
	}

	public void saveListInSession(List<Object> list)
	{
		setOrganized(false);
		if (getSaveInSession() && page != null)
			page.session.setAttribute(getListSessionKey(), list);
	}

	public void clearList()
	{
		page.session.removeAttribute(getListSessionKey());
		if (itemsList != null)
			itemsList.clear();
		isDirty = true;
	}

	public void invaidateList()
	{
		isDirty = true;
	}

	/**
	 * @param outputStream
	 * @throws Exception
	 */
	protected void renderNewItemPanel(RenderPrinter outputStream) throws Exception
	{
		outputStream.write("<div class=\"modal fade\" id=\"" + getId() + NEW_ITEM_PANEL_ID
				+ "\" tabindex=\"-1\" role=\"dialog\" aria-labelledby=\"myModalLabel\" aria-hidden=\"true\"><div class=\"modal-dialog\">"
				+ "<div class=\"modal-content\"><div class=\"modal-header\">  "
				+ "<button type=\"button\" class=\"close\" data-dismiss=\"modal\" aria-hidden=\"true\">&times;</button><h4 class=\"modal-title\">Add new Item</h4></div>"
				+ "<div class=\"modal-body\"><div class=\"row\">");

		StringBuilder requiredID = new StringBuilder("");

		String colLabel = "";
		// render rest of the rows.
		String colName = null;
		Object colVal = null;
		for (DataColumn column : columns)
		{
			// [Mar 21, 2012 5:05:51 PM] [amr.eladawy] [skip rendering if this is a column command or an empty column name]
			if (column instanceof DataColumnCommand || StringUtility.isNullOrEmpty(column.getFieldName()))
				continue;
			colName = column.getFieldName();
			colLabel = column.getText();
			// ///

			outputStream.write("<div class=\"span4\"><div class=\"control-group info\">");
			String wcId = new StringBuilder(getId()).append(nameSplitter).append(nameSplitterInsert).append(colName).toString();
			Render.createLabel(colLabel, wcId, page).render(outputStream);

			if (!StringUtility.isNullOrEmpty(column.getHint()))
				outputStream.write(" &nbsp; &nbsp;<small><cite title=\"" + column.getHint() + "\">" + column.getHint() + "</cite></small>");

			outputStream.write(" <div class=\"controls\">");
			{
				if (column.getReadOnly())
				{
					Render.createLabel(column.getDefaultValue(), wcId, page).render(outputStream);
				}
				else
				{
					if (column.getType().equalsIgnoreCase(DataColumn.Date))
						Render.createCalendar(wcId, "", column.getDateFormat(), page).render(outputStream);
					else if (column.getType().equalsIgnoreCase(DataColumn.Lookup))
					{
						if (!StringUtility.isNullOrEmpty(column.getFK()))
							Render.createSelect(wcId, String.valueOf(colVal), lookups.get(column.getFK()), column.getCustomAttribute(), page)
									.render(outputStream);
					}
					else
						Render.renderInput(outputStream, wcId, "", column.getCustomAttribute(), page);
				}
			}
			if (column.getRequired())
			{
				outputStream.write("*");
				requiredID.append("'" + wcId + "',");
			}
			outputStream.write("</div></div></div>");
			// //
		}

		if (requiredID.length() > 0)
		{
			requiredID.deleteCharAt(requiredID.length() - 1).append("]").insert(0, "[");
		}
		String saveAction = Render.composeAction(getId(), Table.SAVE_EVENT, "", null, true, null, false, false, getMySubmitter(), this);

		outputStream
				.write("</div></div><div class=\"modal-footer\"><button type=\"button\" class=\"btn btn-default\" data-dismiss=\"modal\">Close</button>  "
						+ "<button type=\"button\" class=\"btn btn-primary\" onclick=\"if (validInputData(" + requiredID.toString() + ")){"
						+ " $jspx('#" + getId() + NEW_ITEM_PANEL_ID + " ').modal('hide');" + saveAction
						+ ";}\">Save changes</button></div></div></div></div>");
	}

	private Hashtable<String, Object> getObjectTable(boolean isEdit)
	{
		Hashtable<String, Object> table = new Hashtable<String, Object>();
		for (DataColumn column : columns)
		{
			if (column.getType().equalsIgnoreCase("lookup"))
			{
				String name = column.getParent().getName() + nameSplitter + (isEdit ? nameSplitterUpdate : nameSplitterInsert)
						+ column.getFieldName();
				String val = postedInternalValues.get(name).toString();
				// Object object = lookups.get(column.getFK()).lookup(val);
				table.put(name, val);
			}

		}
		return table;
	}

	/**
	 * insert new item
	 * 
	 * @param eventArgs
	 */
	public void doAdd()
	{
		// DO Insert here
		try
		{
			validatePostedData(false);
			List<Object> list = initializeList();
			if (StringUtility.isNullOrEmpty(getOnInsert()))
			{
				MAO.insertIntoList(list, postedInternalValues, getObjectTable(false), getBindToClass());
			}
			else
			{
				Object object = MAO.getObjectInstance(list, postedInternalValues, getObjectTable(false), getBindToClass());
				Method m = page.getClass().getDeclaredMethod(getOnInsert(), Object.class);
				try
				{
					m.invoke(page, object);
				}
				catch (InvocationTargetException e)
				{
					// [Sep 14, 2009 12:46:59 PM] [amr.eladawy] [if there was an exception while invoking the user code.]
					setStatus(EditState);
					throw new RuntimeException(e.getCause());
				}
			}
		}
		catch (Exception e)
		{

			logger.debug("Error while insert, msg = ", e);
			// [Aug 27, 2013 3:14:10 AM] [Amr.ElAdawy] [send error to the error listener]
			if (StringUtility.isNullOrEmpty(getOnError()))
			{
				message = e.getMessage();
				// [Jul 22, 2013 8:44:08 AM] [amr.eladawy] [if e is null pointer exception ]
				if (message == null)
					message = e.toString();
			}
			else
			{
				try
				{
					PropertyAccessor.invokePageMethod(page, page.getClass(), getOnError(), this, e.getMessage());
				}
				catch (InvocationTargetException e1)
				{
					logger.error("doAdd()- ", e1);
				}
			}
			dataBind();
		}
	}

	/**
	 * save the updated item.
	 * 
	 * @param eventArgs
	 */
	public void update(String eventArgs)
	{
		setStatus(ViewState);
		if (!isActionAllowed(EditState))
			logger.error("User is trying to Edit Record while Editing is not allowed.");
		else
		{
			// DO save here.
			try
			{
				validatePostedData(true);
				List<Object> list = initializeList();
				if (StringUtility.isNullOrEmpty(getOnEdit()))
				{
					MAO.editList(list, postedInternalValues, getObjectTable(true), eventArgs, getBindToClass());
				}
				else
				{
					Object object = MAO.getObjectInstance(list, postedInternalValues, getObjectTable(true), list.get(Integer.parseInt(eventArgs)));
					Method m = page.getClass().getDeclaredMethod(getOnEdit(), Object.class, String.class);
					try
					{
						m.invoke(page, object, eventArgs);
					}
					catch (InvocationTargetException e)
					{
						// [Sep 14, 2009 12:46:59 PM] [amr.eladawy] [if there was an exception while invoking the user code.]
						setStatus(EditState);
						throw new RuntimeException(e.getCause());
					}
				}
			}
			catch (DataException e)
			{
				message = e.getMessage();
				setStatus(EditState);
			}
			catch (Exception e)
			{
				logger.debug("Error while save, msg = " + e);
				setStatus(EditState);
				if (e instanceof RuntimeException)
					throw (RuntimeException) e;
			}
		}
		dataBind();
	}

	public void edit(String eventArgs)
	{
		super.edit(eventArgs);
	}

	/**
	 * validates the posted data versus required
	 * 
	 * @throws DataException
	 */
	public void validatePostedData(boolean isEdit) throws DataException
	{
		DataColumn column = null;
		for (String key : postedInternalValues.keySet())
		{
			String delimeter = InternalValueHolder.nameSplitter
					+ (isEdit ? InternalValueHolder.nameSplitterUpdate : InternalValueHolder.nameSplitterInsert);
			int sep = key.lastIndexOf(delimeter);
			String name = key.substring(sep + delimeter.length());
			column = getColumn(name);
			if (StringUtility.isNullOrEmpty(postedInternalValues.get(key)) && column != null && column.getRequired())
			{
				MessageFormat formatter = new MessageFormat("");
				formatter.applyPattern(getRequiredtext());
				throw new DataException(formatter.format(new String[]
				{ column.getText() }));
			}
		}
	}

	public void delete(String eventArgs)
	{
		setStatus(ViewState);

		if (!isActionAllowed(DeleteState))
			logger.error("User is trying to Delete Record while deleting is not allowed.");
		else
		{
			List<?> list = initializeList();
			try
			{
				if (StringUtility.isNullOrEmpty(getOnDelete()))
				{

					list.remove(Integer.parseInt(eventArgs));
				}
				else
				{
					this.page.getClass().getDeclaredMethod(getOnDelete(), Object.class, String.class).invoke(page,
							list.get(Integer.parseInt(eventArgs)), eventArgs);
				}

			}
			catch (Exception e)
			{
				logger.debug("Error while delete, msg = " + e);
			}
		}
		dataBind();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eg.java.net.web.jspx.ui.controls.html.elements.dataitem.Table#cancelItem(java.lang.String)
	 */
	@Override
	public void cancelItem(String eventArgs)
	{
		super.cancelItem(eventArgs);
	}

	protected DataColumn getColumn(String colName)
	{
		DataColumn col = null;
		for (int i = 0; i < columns.size(); i++)
		{
			col = columns.get(i);
			if (col.getFieldName().equalsIgnoreCase(colName))
				return col;
		}
		return null;
	}

	/**
	 * 
	 * @param colName
	 * @return
	 */
	protected String getColumnDataType(String colName)
	{
		DataColumn col = getColumn(colName);
		if (col != null)
			return col.getType();
		return DataColumn.Varchar;
	}

	/**
	 * 
	 * @param colName
	 * @return
	 */
	protected boolean getColumnRequired(String colName)
	{
		DataColumn col = getColumn(colName);
		if (col != null)
			return col.getRequired();
		return false;
	}

	public Object getRow(int rowIndex)
	{
		if (itemsList.size() < 1)
			dataBind();
		return itemsList.get(rowIndex);
	}

	public void setItems(String itemsVal)
	{
		setAttributeValue(items, itemsVal);
	}

	public String getItems()
	{
		return getAttributeValue(items);
	}

	public void setBindToClass(String bindToClass)
	{
		setAttributeValue(bindToClassKey, bindToClass);
	}

	public String getBindToClass()
	{
		return getAttributeValue(bindToClassKey);
	}

	/**
	 * get the selected row either as - an object filled with the column values if the bindtoclass attribute is set - a hashtable
	 * of the selected row otherwise
	 * 
	 * @param index
	 * @return Object
	 */
	public Object getSelectedRow(String index)
	{
		int rowIndex = Integer.parseInt(index);
		// if (getPageIndex() > 1)
		// {
		// rowIndex = +(getPageIndex() * getPageSize());
		// logger.debug("final row index = " + rowIndex);
		// }
		return rows.get(rowIndex);
	}

	@Override
	protected Object getRowToRender(int index)
	{
		return rows.get(index);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object> getSelectedRows()
	{
		dataBind();
		if (isSelectedAll())
		{
			if (isAllSelected())
				return (List<Object>) itemsList; // return all the list
			else
			{
				return (List<Object>) rows; // return the list in the current
				// page
			}
		}
		else
		{ // return selected indecies
			List<Object> list = new ArrayList<Object>();
			List<Integer> indeices = getSelectedIndices();
			for (Integer index : indeices)
				list.add(getSelectedRow(String.valueOf(index)));
			return list;
		}
	}

	@Override
	protected Object getRowToEdit()
	{
		return itemsList.get(selectedIndex);
	}

	@Override
	public String getEventArgs()
	{
		return String.valueOf(selectedIndex);
	}

	@Override
	protected List<?> createEmptyRows()
	{
		List<Object> eRows = new ArrayList<Object>();
		for (int i = 0; i < getPageSize(); i++)
			eRows.add(new Object());

		return eRows;
	}
}
