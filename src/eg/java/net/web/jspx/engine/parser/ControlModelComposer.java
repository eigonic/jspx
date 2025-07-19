/**
 * 
 */
package eg.java.net.web.jspx.engine.parser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.List;

import javax.servlet.ServletContext;

import org.kxml.Xml;
import org.kxml.io.ParseException;
import org.kxml.parser.ParseEvent;
import org.kxml.parser.Tag;
import org.kxml.parser.XmlParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eg.java.net.web.jspx.engine.error.PageNotFoundException;
import eg.java.net.web.jspx.engine.util.Security;
import eg.java.net.web.jspx.engine.util.StringUtility;
import eg.java.net.web.jspx.engine.util.bean.ResourceBundleUtility;
import eg.java.net.web.jspx.ui.controls.WebControl;
import eg.java.net.web.jspx.ui.controls.attrbs.Attribute;
import eg.java.net.web.jspx.ui.controls.html.custom.UserControl;
import eg.java.net.web.jspx.ui.controls.html.elements.CollapsePanel;
import eg.java.net.web.jspx.ui.controls.html.elements.LinkCommand;
import eg.java.net.web.jspx.ui.controls.html.elements.ajax.AjaxPanel;
import eg.java.net.web.jspx.ui.controls.html.elements.dataitem.DataColumn;
import eg.java.net.web.jspx.ui.controls.html.elements.dataitem.ItemTemplate;
import eg.java.net.web.jspx.ui.controls.html.elements.markers.InternalEventsListener;
import eg.java.net.web.jspx.ui.controls.html.elements.markers.ValueHolder;
import eg.java.net.web.jspx.ui.pages.Page;

/**
 * User Control Parser. <br/>
 * parses the JSPX control file.
 * 
 * @author amr.eladawy
 * 
 */
public abstract class ControlModelComposer
{
	private static final Logger logger = LoggerFactory.getLogger(ControlModelComposer.class);

	private static Object insertLock = new Object();

	private static Hashtable<String, WebControl> cachedControls = new Hashtable<String, WebControl>();

	/**
	 * returns an object model for the HTML of this Control. if the Control already cached, it is returned.
	 * 
	 * @param charSet
	 * 
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public static WebControl composeControl(String jspxFileName, ServletContext context, String locale, String charSet) throws Exception
	{
		WebControl cached = getControl(jspxFileName, context, locale, charSet);
		return cached;
	}

	/**
	 * 1- get the localized name of the file. <br/>
	 * 2- get the cached page for the localized name. <br/>
	 * 3- if not cached try to parse it. <br/>
	 * 4- if not parsed try to get the cached page for original name. <br/>
	 * 5- if not cached, parse the page with the original name.
	 * 
	 * @param charSet
	 */
	public static WebControl getControl(String jspxFileName, ServletContext context, String locale, String charSet) throws Exception
	{
		String threadName = Thread.currentThread().getName();
		XmlParser parser = null;
		// 1- get the localized name of the file.
		String local_jspxFileName = getLocalizedName(jspxFileName, locale);
		WebControl cachedControl = null;
		// 2- get the cached page under the localized name.
		cachedControl = getCahcedControl(local_jspxFileName);
		if (cachedControl == null)
		{
			// 3- if not cached try to parse it.
			Security.setThreadName("Parsing JSPX Control [" + local_jspxFileName + "]");
			try
			{
				parser = getControlParser(local_jspxFileName, context, charSet);
				if (parser == null)
				{
					// 4- if file not exists try to get the cached page for
					// original name.
					cachedControl = getCahcedControl(jspxFileName);

					// 5- if not cached, parse the page with the original name.
					if (cachedControl == null)
					{
						cachedControl = parseControl(getControlParser(jspxFileName, context, charSet), jspxFileName);
						// 6.1- save the parsed page under the original name.
						saveControl(cachedControl, jspxFileName);
					}
				}
				else
				{
					// 4.2- if exist try to parse it.
					cachedControl = parseControl(parser, jspxFileName);
					// 6.2- save the parsed page under the local name.
					saveControl(cachedControl, local_jspxFileName);
				}
			}
			catch (ParseException ex)
			{
				throw ex;
			}
			catch (PageNotFoundException pe)
			{
				throw pe;
			}
			catch (IllegalAccessException exc)
			{
				throw exc;
			}
			catch (Exception e)
			{
				logger.error(
						"getControl(jspxFileName=" + jspxFileName + ", context=" + context + ", locale=" + locale + ", charSet=" + charSet + ")", e);
				throw new Exception(new StringBuilder("Could not find the jspx page [").append(jspxFileName)
						.append("]  please make sure that the file exists on the specidifed path").toString());
			}
		}
		Security.setThreadName(threadName);
		return cachedControl;
	}

	/**
	 * tries to find page with specific local, if the local is empty or default or localized page is not found, the default page
	 * is returned.
	 * 
	 * @param controlName
	 * @param context
	 * @param locale
	 * @param charSet
	 * @return
	 */
	protected static XmlParser getLocalizedPageParser(String controlName, ServletContext context, String locale, String charSet)
	{
		if (!StringUtility.isNullOrEmpty(locale) && !ResourceBundleUtility.isDefaultLocale(locale))
		{
			XmlParser parser = getControlParser(getLocalizedName(controlName, locale), context, charSet);
			if (parser != null)
				return parser;
		}
		return getControlParser(controlName, context, charSet);
	}

	/**
	 * gets the file name after adding the locale.
	 * 
	 * @param pageName
	 * @param locale
	 * @return
	 */
	protected static String getLocalizedName(String pageName, String locale)
	{
		if (!StringUtility.isNullOrEmpty(locale) && !ResourceBundleUtility.isDefaultLocale(locale))
		{
			int index = pageName.lastIndexOf('.');
			return pageName.substring(0, index) + "_" + locale + pageName.substring(index);
		}
		return pageName;
	}

	/**
	 * get the input Stream for certain page.
	 * 
	 * @param pageName
	 * @param context
	 * @param charSet
	 * @return
	 */
	protected static XmlParser getControlParser(String pageName, ServletContext context, String charSet)
	{
		try
		{
			return new XmlParser(new InputStreamReader(context.getResourceAsStream(pageName), charSet));
		}
		catch (Exception e)
		{
			// e.printStackTrace();
		}
		return null;
	}

	/**
	 * parses the model from the html page;
	 * 
	 * @param parser
	 * @param jspxFileName
	 *            TODO
	 * @throws IOException
	 */
	protected static WebControl parseControl(XmlParser parser, String jspxFileName) throws Exception
	{
		if (parser == null)
			throw new PageNotFoundException(jspxFileName);
		String header = null;
		ParseEvent peek = parser.peek();
		while (!parser.peek(Xml.START_TAG, null, TagFactory.Control))
			parser.read();

		if (peek.getType() == Xml.DOCTYPE)
		{
			peek = parser.read();
			header = peek.getText();
		}
		WebControl control = findCodeBehinde(ControlModelComposer.ensureTag(parser, Xml.START_TAG, TagFactory.Control), jspxFileName);
		while (!parser.peek(Xml.END_TAG, null, TagFactory.Control))
		{
			peek = parser.peek();
			if (peek.getType() == Xml.START_TAG)
				control.addControl(prepareWebControl(peek, control).deSerialize(parser, /* page */null, null));
		}
		return control;
	}

	/**
	 * finds the control that handles the given tag. this control might be a declared control with the given id. if not found, the
	 * method generate new one.
	 * 
	 * @param peek
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public static WebControl prepareWebControl(ParseEvent peek, WebControl control) throws Exception
	{
		String tagName = peek.getName();
		String id = null;
		WebControl declaredControl = null;
		Attribute idAttribute = peek.getAttributeIgnoreCase("id");
		if (idAttribute != null)
		{
			id = idAttribute.getValue();
			if (id != null)
				declaredControl = null;// page.getControl(id);
		}
		if (declaredControl == null)
			declaredControl = TagFactory.getControl(tagName);
		else
		{
			declaredControl.setDeclared(true);
			// page.addDeclaredControl(declaredControl);
		}
		if (id == null)
		{
			// define an id if the developer didn't define one
			if (declaredControl instanceof ValueHolder || declaredControl instanceof LinkCommand || declaredControl instanceof AjaxPanel
					|| declaredControl instanceof InternalEventsListener || declaredControl instanceof CollapsePanel
					|| declaredControl instanceof ItemTemplate || declaredControl instanceof DataColumn)
				declaredControl.setId("jspx_generated#" /* +page.valueHolderId++ */);
			else
				declaredControl.getAttributes().remove(WebControl.idKey);
		}
		return declaredControl;
	}

	/**
	 * Consumes the StartTag or endTag, skips the whitespace & stuff, & returns the a Vector of the attributes that this tag
	 * includes.
	 * 
	 * @param parser
	 *            The XmlParser used to read the byteArray
	 * @param type
	 *            either Xml.START_TAG or Xml.END_TAG
	 * @param tagName
	 *            The name of the XML tag
	 * @return A vector of Attribute that the opening tag includes,an empty vector if no attributes exist, <code>null</code>
	 *         otherwise
	 * @throws IOException
	 */
	public static List<Attribute> ensureTag(XmlParser parser, int type, String tagName) throws IOException
	{
		Tag tag = (Tag) parser.read(type, null, tagName);
		parser.skip();

		if (type == Xml.START_TAG)
			return tag.getAttributes();
		return null;
	}

	private static void saveControl(WebControl control, String jspxFileName)
	{
		synchronized (insertLock)
		{
			cachedControls.put(jspxFileName, control);
		}
	}

	private static WebControl getCahcedControl(String jspxFileName)
	{
		synchronized (insertLock)
		{
			return cachedControls.get(jspxFileName);
		}
	}

	/**
	 * finds the code behind WebControl.
	 * 
	 * @param pageAttributes
	 * @param jspxFileName
	 *            TODO
	 * @return
	 */
	public static WebControl findCodeBehinde(List<Attribute> pageAttributes, String jspxFileName)
	{
		WebControl control;
		Attribute att = null;
		String codeBehind = null;
		for (int i = 0; i < pageAttributes.size(); i++)
		{
			att = pageAttributes.get(i);
			if (att.getKey().equalsIgnoreCase(Page.Controller))
				codeBehind = att.getValue().trim();
		}
		try
		{
			control = (WebControl) Class.forName(codeBehind).newInstance();
		}
		catch (Exception e)
		{
			if (e instanceof ClassNotFoundException)
				logger.error("Control Code behind [" + codeBehind + "] Is not found, please make sure that you typed it correctly", e);
			else if (e instanceof ClassCastException)
				logger.error("Control Code behind [" + codeBehind
						+ "] Is not the same type as the Control, please extend from the correct parent (UserControl)", e);
			// no page implementor, loading the default.
			return new UserControl();
		}
		return control;
	}
}
