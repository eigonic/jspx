/**
 * 
 */
package eg.java.net.web.jspx.ui.controls.attrbs;

import java.io.Serializable;

import org.kxml.XML_Attribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eg.java.net.web.jspx.engine.util.StringUtility;
import eg.java.net.web.jspx.engine.util.bean.JspxBeanUtility;
import eg.java.net.web.jspx.engine.util.bean.RepeaterUtility;
import eg.java.net.web.jspx.engine.util.ui.Render;
import eg.java.net.web.jspx.engine.util.ui.RenderPrinter;
import eg.java.net.web.jspx.ui.controls.WebControl;
import eg.java.net.web.jspx.ui.pages.Page;

/**
 * Attribute for a web Control.
 * 
 * @author amr.eladawy
 * 
 */
public class Attribute extends XML_Attribute implements Serializable
{
	private static final long serialVersionUID = 6448827857487938587L;

	public Attribute(String namespace, String name, String value)
	{
		super(namespace, name, value);
	}

	private static final Logger logger = LoggerFactory.getLogger(Attribute.class);

	public Attribute(String key, String value)
	{
		super(key, value);
	}

	private WebControl containerControl;

	public Attribute(String key, String value, WebControl control)
	{
		super(key, value);
		this.containerControl = control;
	}

	public Attribute()
	{
		super("", "");
	}

	public void setValue(String value)
	{
		this.value = value;
		if (containerControl != null)
			containerControl.addAttribute(this);
	}

	public void render(RenderPrinter outputStream, Page page)
	{
		renderCustomKey(key, outputStream, page);
	}

	/**
	 * renders this attribute with different key, but with same value
	 * 
	 * @param outputStream
	 * @param page
	 *            TODO
	 * @param key
	 */
	public void renderCustomKey(String myKey, RenderPrinter outputStream, Page page)
	{
		try
		{
			if (StringUtility.isNullOrEmpty(myKey))
				return;
			outputStream.write(Render.whiteSpace);
			// [23 Apr 2015 20:30:43] [aeladawy] [this is to replace the double quotes in the attribute value ]
			String finalVal = StringUtility.isNullOrEmpty(value) ? "" : evaluateVlaue(page);
			finalVal = finalVal.replace("\"", "&quot;");
			outputStream.write(new StringBuilder(myKey).append("=").append("\"").append(finalVal).append("\"").toString());
		}
		catch (Exception e)
		{
			logger.warn("render() - exception ignored", e);
		}
	}

	protected String evaluateVlaue(Page page)
	{
		String newValue = StringUtility.evaluateComplexVlaue(value, page, null);
		if (newValue == null)
			newValue = value;
		return newValue;
	}

	protected String evaluateVlaue(Page page, String dateformat)
	{
		String newValue = StringUtility.evaluateComplexVlaue(value, page, dateformat);
		if (newValue == null)
			newValue = value;
		return newValue;
	}

	public String toString()
	{
		return new StringBuilder("\"").append(StringUtility.isNullOrEmpty(value) ? "" : value).append("\"").toString();
	}

	/**
	 * returns new instance loaded woth the given values form the control
	 * 
	 * @param attribute
	 */
	public Attribute clone()
	{
		return new Attribute(key, value);
	}

	public String getKey()
	{
		return key;
	}

	public void setKey(String key)
	{
		this.key = key;
	}

	public String getValue()
	{
		return value;
	}

	public String getValue(Page page)
	{
		return evaluateVlaue(page);
	}

	public String getValue(Page page, String dateformat)
	{
		return evaluateVlaue(page, dateformat);
	}

	public Object getValueObject(Page page)
	{
		Object newValue = null;
		if (StringUtility.isEL(value) && page != null && page.request != null && page.session != null)
		{
			if (newValue == null)
				newValue = JspxBeanUtility.evaluateObject(value, page);
			if (newValue == null)
				newValue = RepeaterUtility.evaluateObject(value, page);
		}
		return newValue;
	}
}
