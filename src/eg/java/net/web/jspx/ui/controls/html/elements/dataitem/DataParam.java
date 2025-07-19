/**
 * 
 */
package eg.java.net.web.jspx.ui.controls.html.elements.dataitem;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eg.java.net.web.jspx.engine.error.JspxException;
import eg.java.net.web.jspx.engine.parser.TagFactory;
import eg.java.net.web.jspx.engine.util.SqlUtility;
import eg.java.net.web.jspx.engine.util.StringUtility;
import eg.java.net.web.jspx.ui.controls.attrbs.JspxAttribute;
import eg.java.net.web.jspx.ui.controls.html.GenericWebControl;
import eg.java.net.web.jspx.ui.controls.html.elements.hidden.HiddenGenericWebControl;
import eg.java.net.web.jspx.ui.controls.html.elements.markers.ValueHolder;
import eg.java.net.web.jspx.ui.pages.Page;

/**
 * @author amr.eladawy
 * 
 */
public class DataParam extends HiddenGenericWebControl
{
	private static final long serialVersionUID = -187111948296386112L;
	private static final Logger logger = LoggerFactory.getLogger(DataParam.class);

	public DataParam()
	{
		super(TagFactory.DataParam);
	}

	/**
	 * @param tagName
	 * @param page
	 */
	public DataParam(Page page)
	{
		super(TagFactory.DataParam, page);
	}

	@JspxAttribute
	protected static String ControlKey = "control";

	public void setControl(String control)
	{
		setAttributeValue(ControlKey, control);
	}

	public String getControl()
	{
		return getAttributeValue(ControlKey);
	}

	@JspxAttribute
	protected static String typeKey = "type";

	public void setType(String type)
	{
		setAttributeValue(typeKey, type);
	}

	public String getType()
	{
		return getAttributeValue(typeKey);
	}

	@JspxAttribute
	private String exporession = "expression";

	public void setExpression(String value)
	{
		setAttributeValue(exporession, value);
	}

	public String getExpression()
	{
		return getAttributeValue(exporession);
	}

	@JspxAttribute
	protected String sessionKey = "session";

	public String getSessionValue()
	{
		return getAttributeValue(sessionKey);
	}

	public void setSessionValue(String sessionValue)
	{
		setAttributeValue(sessionKey, sessionValue);
	}

	@JspxAttribute
	protected String requestKey = "request";

	public String getRequestValue()
	{
		return getAttributeValue(requestKey);
	}

	public void setRequestValue(String requestValue)
	{
		setAttributeValue(requestKey, requestValue);
	}

	@JspxAttribute
	protected String value = "value";

	public String getValue()
	{
		return getAttributeValue(value);
	}

	public void setValue(String valueValue)
	{
		setAttributeValue(value, valueValue);
	}

	@JspxAttribute
	protected String trim = "trim";

	public boolean getTrim()
	{
		return getAttributeBooleanValue(trim);
	}

	public void setTrim(boolean trimValue)
	{
		setAttributeBooleanValue(trim, trimValue);
	}

	@JspxAttribute
	protected String safe = "safe";

	public boolean getSafe()
	{
		return getAttributeBooleanValue(safe);
	}

	public void setSafe(boolean safeValue)
	{
		setAttributeBooleanValue(safe, safeValue);
	}

	@JspxAttribute
	protected String required = "required";

	public boolean getRequired()
	{
		return getAttributeBooleanValue(required);
	}

	public void setRequired(boolean requiredValue)
	{
		setAttributeBooleanValue(required, requiredValue);
	}

	/**
	 * gets the value of a control to use it in the SQL statement.
	 * 
	 * @param controlId
	 * @return
	 */
	protected String getControlValue(String controlId)
	{
		GenericWebControl control;
		try
		{
			control = (GenericWebControl) page.getControl(controlId);
			if (control == null)
				control = (GenericWebControl) page.getDataModelChild(controlId);
			if (control != null && control instanceof ValueHolder)
				return ((ValueHolder) control).getValue();
		}
		catch (Exception e)
		{
			logger.error(e.getMessage(), e);
		}
		return "";
	}

	/**
	 * this is where the DataParam is injecting the calculated value in the sql.
	 * 
	 * @param sql
	 * @param isCaseSenstive
	 * @return
	 */
	public String formatSql(String sql, boolean isCaseSenstive)
	{
		if (!isRendered() || !isAccessible())
		{
			logger.debug("This Dataparam [{}]will not be used as it is not rendered or not accessible", this);
			return sql;
		}
		String expression = getExpression();
		String orgainlExpression = expression;
		if (StringUtility.isNullOrEmpty(expression))
		{
			logger.error("No Expresion found....");
			throw new JspxException("DataParam [" + jspxString() + "] has no Expression... ");
		}
		String name = getName();
		if (StringUtility.isNullOrEmpty(name))
		{
			// logger.debug("No Name found.... checking for question mark");
			name = "?";
		}

		// logger.info("formatSql(String, boolean) - String case sensitive=" + isCaseSenstive);
		if (!isCaseSenstive)
		{
			expression = expression.toLowerCase();
			name = name.toLowerCase();
		}
		if (!expression.contains(name))
		{
			logger.error("Expression [" + expression + "] does not contain the name [" + name + "]");
			throw new JspxException("DataParam [" + jspxString() + "] has the Expression [" + expression + "] that does not contain the name [" + name
					+ "] Please make sure of the name exists and case matches");
		}

		// [Dec 28, 2014 11:38:36 AM] [Amr.ElAdawy] [remove extra spaces]
		orgainlExpression = orgainlExpression.replaceAll("\\s*=\\s*", " = ");
		sql = sql.replaceAll("\\s*=\\s*", " = ");
		orgainlExpression = orgainlExpression.replaceAll("\\s*<\\s*=\\s*", " <= ");
		sql = sql.replaceAll("\\s*<\\s*=\\s*", " <= ");
		orgainlExpression = orgainlExpression.replaceAll("\\s*>\\s*=\\s*", " >= ");
		sql = sql.replaceAll("\\s*>\\s*=\\s*", " >= ");
		orgainlExpression = orgainlExpression.replaceAll("\\s*<\\s*>\\s*", " <> ");
		sql = sql.replaceAll("\\s*<\\s*>\\s*", " <> ");
		if (!sql.contains(orgainlExpression))
		{
			logger.error("Expression [" + orgainlExpression + "] is not found in the SQL  [" + sql + "]");
		}

		String paramValue = null;
		if (!StringUtility.isNullOrEmpty(getControl()))
			paramValue = getControlValue(getControl());
		if (!StringUtility.isNullOrEmpty(getRequestValue()))
			paramValue = page.request.getParameter(getRequestValue());
		if (!StringUtility.isNullOrEmpty(getSessionValue()))
			paramValue = (String) page.session.getAttribute(getSessionValue());
		if (attributes.get(value) != null && !StringUtility.isNullOrEmpty(attributes.get(value).getValue()))
			paramValue = getValue();
		if (getTrim() && !StringUtility.isNullOrEmpty(paramValue))
			paramValue = paramValue.trim();
		if (StringUtility.isNullOrEmpty(paramValue))
		{
			if (getRequired())
			{
				throw new RuntimeException("Parameter value is required for expression " + expression);
			}
			sql = sql.replace(orgainlExpression, " 1=1 ");
		}
		else
		{
			if (!isCaseSenstive)
			{
				paramValue = paramValue.toLowerCase();
			}
			String sanitizedParamValue = paramValue;
			// [31 Mar 2015 09:25:56] [aeladawy] [dont sanitize if safe contorl]
			if (!getSafe())
				sanitizedParamValue = SqlUtility.encodeForSQL(paramValue);
			logger.debug("ParamValue converted from [" + paramValue + "] to [" + sanitizedParamValue + "]");
			String newExpr = expression.replace(name, sanitizedParamValue).trim();
			if (newExpr.startsWith("#") && newExpr.endsWith("#"))
				newExpr = newExpr.substring(1, newExpr.length() - 1);
			sql = sql.replace(orgainlExpression, newExpr);
		}
		return sql;
	}

	public List<Object> filterList(List<Object> list, boolean isCaseSenstive)
	{
		if (!isRendered() || !isAccessible())
		{
			logger.debug("This Dataparam [{}]will not be used as it is not rendered or not accessible", this);
			return list;
		}
		String expression = getExpression();
		if (StringUtility.isNullOrEmpty(expression))
		{
			logger.error("No Expresion found....");
			throw new JspxException("DataParam [" + jspxString() + "] has no Expression... ");
		}
		String name = getName();
		if (StringUtility.isNullOrEmpty(name))
		{
			// logger.debug("No Name found.... checking for question mark");
			name = "?";
		}
		if (!expression.contains(name))
		{
			logger.error("Expression [" + expression + "] does not contain the name [" + name + "]");
			throw new JspxException("DataParam [" + getId() + "] has the Expression [" + expression + "] that does not contain the name [" + name
					+ "] Please make sure of the name exists and match case");
		}

		// [Dec 28, 2014 11:38:36 AM] [Amr.ElAdawy] [remove extra spaces]
		expression = expression.replaceAll("\\s*=\\s*", " = ");
		expression = expression.replaceAll("\\s*<\\s*=\\s*", " <= ");
		expression = expression.replaceAll("\\s*>\\s*=\\s*", " >= ");
		expression = expression.replaceAll("\\s*<\\s*>\\s*", " <> ");

		String paramValue = null;
		if (!StringUtility.isNullOrEmpty(getControl()))
			paramValue = getControlValue(getControl());
		if (!StringUtility.isNullOrEmpty(getRequestValue()))
			paramValue = page.request.getParameter(getRequestValue());
		if (!StringUtility.isNullOrEmpty(getSessionValue()))
			paramValue = (String) page.session.getAttribute(getSessionValue());
		if (attributes.get(value) != null && !StringUtility.isNullOrEmpty(attributes.get(value).getValue()))
			paramValue = getValue();
		if (getTrim() && !StringUtility.isNullOrEmpty(paramValue))
			paramValue = paramValue.trim();
		if (!StringUtility.isNullOrEmpty(paramValue))
		{
			if (!isCaseSenstive)
			{
				paramValue = paramValue.toLowerCase();
			}
			if ("string".equalsIgnoreCase(getType()))
			{
				paramValue = "\"" + paramValue + "\"";
			}
			expression = expression.replace(name, paramValue);
			expression = "${" + expression + "}";

			List<Object> newList = new ArrayList<Object>();
			String varName = ((Table) this.getParent()).getVar();
			for (Object object : list)
			{
				this.getPage().registerElVariable(varName, object);
				String result = StringUtility.evaluateComplexVlaue(expression, this.getPage(), null);
				if (!StringUtility.isNullOrEmpty(result) && "true".equalsIgnoreCase(result))
				{
					newList.add(object);
				}
			}
			return newList;
		}
		return list;
	}

	public static void main(String[] arfs)
	{
		int x = Integer.valueOf('@');
		System.out.println(x);
		System.out.println(Integer.toHexString('@'));
		String sql = "SELECT * FROM company WHERE  1=1  AND NAME LIKE  '%?%'";
		String ex = "NAME LIKE  '%?%'";
		System.out.println(sql.indexOf(ex));
		System.out.println(sql.replace(ex, "sdd"));
	}
}
