/**
 * 
 */
package eg.java.net.web.jspx.ui.controls.html.elements.dataitem;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import eg.java.net.web.jspx.engine.data.DAO;
import eg.java.net.web.jspx.engine.parser.TagFactory;
import eg.java.net.web.jspx.engine.util.StringUtility;
import eg.java.net.web.jspx.ui.controls.attrbs.JspxAttribute;
import eg.java.net.web.jspx.ui.controls.html.elements.hidden.HiddenGenericWebControl;
import eg.java.net.web.jspx.ui.pages.Page;

/**
 * FK is an internal control that works as Lookup providers. there is a need to populate a dorp down from a lookup DB table. Data
 * Foreign Key is
 * 
 * @author Amr.ElAdawy
 * 
 */
public class DataLookup extends HiddenGenericWebControl
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -731537765225717903L;

	/**
	 * @param tagName
	 */
	public DataLookup()
	{
		super(TagFactory.DataLookup);
	}

	/**
	 * @param tagName
	 * @param page
	 */
	public DataLookup(Page page)
	{
		super(TagFactory.DataLookup, page);
	}

	protected Hashtable<String, String> elements;
	protected List<String> keys = new ArrayList<String>();

	@JspxAttribute
	private String key = "key";

	public void setKey(String fieldName)
	{
		setAttributeValue(key, fieldName);
	}

	public String getKey()
	{
		return getAttributeValue(key);
	}

	@JspxAttribute
	private String value = "value";

	public void setValue(String valueFieldName)
	{
		setAttributeValue(value, valueFieldName);
	}

	public String getValue()
	{
		return getAttributeValue(value);
	}

	@JspxAttribute
	private String tableName = "table";

	public void setTableName(String dbTable)
	{
		setAttributeValue(tableName, dbTable);
	}

	public String getTableName()
	{
		return getAttributeValue(tableName);
	}

	@JspxAttribute
	protected static String items = "items";

	public void setItems(String itemsVal)
	{
		setAttributeValue(items, itemsVal);
	}

	public Object getItems()
	{
		return getAttributeValueObject(items);
	}

	/**
	 * Retrieves the data from the Lookup DB.
	 * 
	 * @return
	 */
	public Hashtable<String, String> dataBind()
	{
		if (elements == null)
		{
			Object itmesObject = getItems();
			if (itmesObject != null && itmesObject instanceof Map<?, ?>)
			{
				elements = (Hashtable<String, String>) itmesObject;
				for (Object o : elements.keySet())
					keys.add(String.valueOf(o));
			}
			else
			{
				loadDataSource();
				if (StringUtility.isNullOrEmpty(getSql()))
					elements = DAO.loadLookup(getDataSource(),
							new StringBuilder("SELECT ").append(getKey()).append(" , ").append(getValue()).append(" FROM  ").append(getTableName())
									.append(" ORDER BY ").append(getKey()).append(" ASC").toString(), keys);
				else
					elements = DAO.loadLookup(getDataSource(), getSql(), keys);
			}
		}
		return elements;
	}

	/**
	 * returns the SQL that can be used to reverse lookup
	 * [14 Apr 2015 16:20:12]
	 * @author aeladawy 
	 * @return
	 */
	public String getReverseSql(String value)
	{
		return new StringBuilder("SELECT ").append(getKey()).append("  FROM  ").append(getTableName()).append(" WHERE ").append(getValue())
				.append(" = '").append(value).append("'").toString();
	}

	protected String dataSource = "datasource";

	public void setDataSource(String dataSourceVal)
	{
		setAttributeValue(dataSource, dataSourceVal);
	}

	public String getDataSource()
	{
		return getAttributeValue(dataSource);
	}

	/**
	 * looks up the given value in this lookup table.
	 * 
	 * @param Value
	 * @return
	 */
	public Object lookup(String key)
	{
		String value = null;
		if (elements == null && !getLazy())
			dataBind();
		if (elements != null)
			value = elements.get(key);
		if (value == null)
		{
			String where = new StringBuilder(" WHERE ").append(getKey()).append("='").append(key).append("'   ").toString();
			String sqlString = getSql().toLowerCase();
			// if there is no SQL, construct one
			String finalString = "";
			if (StringUtility.isNullOrEmpty(sqlString))
			{
				finalString = new StringBuilder("SELECT ").append(getValue()).append(" FROM  ").append(getTableName()).append(where).toString();
			}
			else
			{
				// use the SQL
				// [Jun 7, 2009 12:02:54 PM] [amr.eladawy] if the developer provided a sql then wrap it with the select * from ()
				// to be easily used.
				finalString = new StringBuilder("select * from (").append(sqlString).append(") lutemp").append(where).toString();

				// int orderbyIndex = sqlString.indexOf("order by");
				// if (orderbyIndex > -1)
				// finalString = new StringBuilder(sqlString.substring(0,
				// orderbyIndex)).append(where).append(sqlString.substring(orderbyIndex))
				// .toString();
				// else
				// finalString = sqlString + " " + where;
			}
			loadDataSource();
			value = DAO.lookup(getDataSource(), finalString, key, getValue()).toString();
		}
		return value;
	}

	protected void loadDataSource()
	{
		if (parent instanceof DataTable && StringUtility.isNullOrEmpty(getDataSource()))
			setDataSource(((DataTable) parent).getDataSource());
	}

	@JspxAttribute
	private String sqlKey = "sql";

	public void setSql(String sqlVal)
	{
		setAttributeValue(sqlKey, sqlVal);
	}

	public String getSql()
	{
		return getAttributeValue(sqlKey);
	}

	@JspxAttribute
	private String lazyKey = "lazy";

	/**
	 * if this data lookup is lazy it will not cache the lookup table values in the memory, used when the lookup table is sooooo
	 * big.
	 * 
	 * @return
	 */
	public void setLazy(boolean lazy)
	{
		setAttributeBooleanValue(lazyKey, lazy);
	}

	/**
	 * if this data lookup is lazy it will not cache the lookup table values in the memory, used when the lookup table is sooooo
	 * big.
	 * 
	 * @return
	 */
	public boolean getLazy()
	{
		return getAttributeBooleanValue(lazyKey);
	}

	public List<String> getKeys()
	{
		return keys;
	}

	public void setKeys(List<String> keys)
	{
		this.keys = keys;
	}

	@JspxAttribute
	protected String caseSenstiveKey = "casesenstive";

	public boolean getcaseSenstive()
	{
		return getAttributeBooleanValue(caseSenstiveKey);
	}

	public void setcaseSenstive(boolean caseSenstive)
	{
		setAttributeBooleanValue(caseSenstiveKey, caseSenstive);
	}

}
