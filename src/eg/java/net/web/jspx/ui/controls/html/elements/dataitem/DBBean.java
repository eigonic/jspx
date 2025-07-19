package eg.java.net.web.jspx.ui.controls.html.elements.dataitem;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eg.java.net.web.jspx.engine.data.DAO;
import eg.java.net.web.jspx.engine.data.DataField;
import eg.java.net.web.jspx.engine.error.JspxException;
import eg.java.net.web.jspx.engine.parser.TagFactory;
import eg.java.net.web.jspx.engine.util.ELUtility;
import eg.java.net.web.jspx.engine.util.PropertyAccessor;
import eg.java.net.web.jspx.engine.util.StringUtility;
import eg.java.net.web.jspx.engine.util.ui.RenderPrinter;
import eg.java.net.web.jspx.ui.controls.WebControl;
import eg.java.net.web.jspx.ui.controls.attrbs.JspxAttribute;
import eg.java.net.web.jspx.ui.controls.html.elements.hidden.HiddenGenericWebControl;
import eg.java.net.web.jspx.ui.controls.html.elements.markers.IAjaxSubmitter;
import eg.java.net.web.jspx.ui.controls.html.elements.markers.ISubmitter;
import eg.java.net.web.jspx.ui.controls.html.elements.markers.InternalValueHolder;
import eg.java.net.web.jspx.ui.pages.Page;

/**
 * @author akhidir
 * 
 */
public class DBBean extends HiddenGenericWebControl implements InternalValueHolder
{

	private static final Logger logger = LoggerFactory.getLogger(DBBean.class);

	protected List<DataParam> parameters = new ArrayList<DataParam>();
	protected Hashtable<String, DataField> bean;
	protected boolean bound = false;

	@JspxAttribute
	protected static String dataSource = "datasource";

	public void setDataSource(String dataSourceVal)
	{
		setAttributeValue(dataSource, dataSourceVal);
	}

	public String getDataSource()
	{
		return getAttributeValue(dataSource);
	}

	@JspxAttribute
	private static String sqlKey = "sql";

	public void setSql(String sqlString)
	{
		setAttributeValue(sqlKey, sqlString);
	}

	public String getSql()
	{
		return getAttributeValue(sqlKey);
	}

	@JspxAttribute
	protected static String dateformat = "dateformat";

	public void setDateformat(String dateformatVal)
	{
		setAttributeValue(dateformat, dateformatVal);
	}

	public String getDateformat()
	{
		return getAttributeValue(dateformat);
	}

	@JspxAttribute
	protected String caseSensitiveKey = "casesensitive";

	public boolean getCaseSensitive()
	{
		return getAttributeBooleanValue(caseSensitiveKey);
	}

	public void setCaseSenstive(boolean caseSenstive)
	{
		setAttributeBooleanValue(caseSensitiveKey, caseSenstive);
	}

	@JspxAttribute
	protected static String autoBindKey = "autobind";

	public void setAutoBind(boolean auto)
	{
		setAttributeBooleanValue(autoBindKey, auto);
	}

	public boolean getAutoBind()
	{
		return getAttributeBooleanValue(autoBindKey);
	}

	@Override
	public void render(RenderPrinter outputStream) throws Exception
	{
		if (getAutoBind())
			dataBind();
		super.render(outputStream);
	}

	/**
	 * @param tagName
	 */
	public DBBean()
	{
		super(TagFactory.DbBean);
	}

	/**
	 * @param tagName
	 * @param page
	 */
	public DBBean(Page page)
	{
		super(TagFactory.DataTable, page);
	}

	@JspxAttribute
	private static String bindToClass = "bindtoclass";

	public void setBindToClass(String bindToClassVal)
	{
		setAttributeValue(bindToClass, bindToClassVal);
	}

	public String getBindToClass()
	{
		return getAttributeValue(bindToClass);
	}

	public WebControl clone(WebControl parent, Page page, ISubmitter submitter, IAjaxSubmitter ajaxSubmitter)
	{
		DBBean bean = (DBBean) super.clone(parent, page, submitter, ajaxSubmitter);
		for (WebControl control : bean.controls)
		{
			if (control instanceof DataParam)
				bean.parameters.add((DataParam) control);
		}
		String attName = Page.DBBeanNamePrefix + bean.getName();
		page.request.setAttribute(attName, bean);
		return bean;
	}

	/**
	 * override this method to return the sql string.
	 * 
	 * @return
	 */
	public String getFinalSql()
	{
		String sql = getSql();
		for (DataParam param : parameters)
			sql = param.formatSql(sql, getCaseSensitive());
		return sql;
	}

	/**
	 * invokes the binding operation.
	 */
	@SuppressWarnings("unchecked")
	public void dataBind()
	{
		try
		{
			String sql = getFinalSql();
			long t = System.currentTimeMillis();
			if (logger.isDebugEnabled())
			{
				logger.debug("dataBind() - {}", this.getId() + " - DBBeanBinding: " + sql);
			}
			List<?> rows = DAO.search(getDataSource(), sql, 0, 1, false, null);
			if (logger.isDebugEnabled())
			{
				logger.debug("dataBind() - {}",
						this.getId() + " - Data retrieved in [" + (System.currentTimeMillis() - t) + "] ms, count[" + rows.size() + "], sql:" + sql);
			}
			if (rows.size() > 0)
			{
				bean = (Hashtable<String, DataField>) rows.get(0);
			}
			else
			{
				bean = new Hashtable<String, DataField>();
			}
			bound = true;

			ELUtility.addObjectToJexlMap(getPage(), getName(), bean);
		}
		catch (Exception e)
		{
			bound = false;
			if (e instanceof JspxException)
				throw (JspxException) e;
			setRendered(false);
			logger.error("Couldn't load db-bean from database: ", e);
		}
	}

	/**
	 * overridden to add the child control to the corresponding collection for example if dataparam --> params. datacolumn colmuns.
	 */
	public void addControl(WebControl control)
	{
		super.addControl(control);
		if (control instanceof DataParam)
			parameters.add((DataParam) control);
	}

	public String getBeanAttributeValue(String propertyName)
	{
		return getBeanAttributeValue(propertyName, null);
	}

	public String getBeanAttributeValue(String propertyName, String dateformat)
	{
		if (!bound)
			dataBind();

		if (dateformat == null)
			dateformat = getDateformat();
		DataField field = bean.get(propertyName.toLowerCase());
		if (field != null)
		{
			Object value = field.getValue();
			if (value != null && !value.toString().equalsIgnoreCase("null"))
			{
				if (value instanceof java.sql.Date || value instanceof java.sql.Timestamp)
				{
					return new SimpleDateFormat(dateformat).format((Date) value);
				}
				else
				{
					return value.toString();
				}
			}
			else
			{
				return "NA";
			}
		}
		return null;
	}

	public Object getObject()
	{
		try
		{
			if (!bound)
				dataBind();
			return convertToObject(bean, getBindToClass());
		}
		catch (Exception e)
		{
			logger.error("getObject()", e);
			return null;
		}
	}

	public Object getData()
	{
		return bean;
	}

	public Object convertToObject(Hashtable<String, DataField> row, String classname) throws InstantiationException, IllegalAccessException,
			ClassNotFoundException
	{
		if (StringUtility.isNullOrEmpty(classname))
			return row;
		try
		{
			Object dataClass = Class.forName(classname).newInstance();
			// do reflection.
			Method[] methods = dataClass.getClass().getDeclaredMethods();
			for (Method method : methods)
			{
				String name = method.getName();
				String key = "";
				if (name.startsWith("set"))
				{
					key = name.substring(3);
					DataField dataField = row.get(getFieldName(key));
					if (dataField == null)
						continue;
					Object value = dataField.getValue();
					try
					{
						PropertyAccessor.invokeSetter(dataClass, method, value, getDateformat());
					}
					catch (Throwable e)
					{
						logger.error("convertToObject(Hashtable<String,DataField>, String)", e);
					}
				}
			}
			return dataClass;
		}
		catch (Exception e)
		{
			logger.error("convertToObject(Hashtable<String,DataField>, String)", e);
		}
		return row;
	}

	public void setInternalInputValues(Hashtable<String, String> values)
	{

	}

	/**
	 * @author Bakry
	 * @author aeladawy
	 *  less complex and smaller
	 * @param classVar
	 * @return
	 */
	public static String getFieldName(String classVar)
	{
		StringBuilder field = new StringBuilder(Character.toString(Character.toLowerCase(classVar.charAt(0))));
		for (int index = 1; index < classVar.length(); index++)
		{
			char c = classVar.charAt(index);
			if (Character.isUpperCase(c))
				field.append("_");
			field.append(Character.toLowerCase(c));
		}
		return field.toString();
	}
}
