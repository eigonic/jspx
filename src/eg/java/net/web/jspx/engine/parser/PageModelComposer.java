/**
 * 
 */
package eg.java.net.web.jspx.engine.parser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
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

import eg.java.net.web.jspx.engine.RequestHandler;
import eg.java.net.web.jspx.engine.error.JspxException;
import eg.java.net.web.jspx.engine.error.PageNotFoundException;
import eg.java.net.web.jspx.engine.util.PropertyAccessor;
import eg.java.net.web.jspx.engine.util.Security;
import eg.java.net.web.jspx.engine.util.StringUtility;
import eg.java.net.web.jspx.engine.util.bean.ResourceBundleUtility;
import eg.java.net.web.jspx.engine.util.ui.Render;
import eg.java.net.web.jspx.ui.controls.WebControl;
import eg.java.net.web.jspx.ui.controls.attrbs.Attribute;
import eg.java.net.web.jspx.ui.controls.html.StringLiteral;
import eg.java.net.web.jspx.ui.controls.html.WhiteSpace;
import eg.java.net.web.jspx.ui.controls.html.elements.CollapsePanel;
import eg.java.net.web.jspx.ui.controls.html.elements.Form;
import eg.java.net.web.jspx.ui.controls.html.elements.LinkCommand;
import eg.java.net.web.jspx.ui.controls.html.elements.ajax.AjaxPanel;
import eg.java.net.web.jspx.ui.controls.html.elements.dataitem.DataColumn;
import eg.java.net.web.jspx.ui.controls.html.elements.dataitem.ItemTemplate;
import eg.java.net.web.jspx.ui.controls.html.elements.inputs.InputFactory;
import eg.java.net.web.jspx.ui.controls.html.elements.markers.InternalValueHolder;
import eg.java.net.web.jspx.ui.controls.html.elements.markers.ValueHolder;
import eg.java.net.web.jspx.ui.controls.validators.Validator;
import eg.java.net.web.jspx.ui.controls.validators.ValidatorFactory;
import eg.java.net.web.jspx.ui.pages.ContentPage;
import eg.java.net.web.jspx.ui.pages.MasterPage;
import eg.java.net.web.jspx.ui.pages.Page;
import eg.java.net.web.jspx.ui.pages.PortletPage;

/**
 * Page Parser.
 * 
 * @author amr.eladawy
 * 
 */
public abstract class PageModelComposer
{
	private static final Logger logger = LoggerFactory.getLogger(PageModelComposer.class);

	private static Object insertLock = new Object();

	private static Object masterInsertLock = new Object();

	private static Hashtable<String, Page> cachedPages = new Hashtable<String, Page>();

	private static Hashtable<String, MasterPage> cachedMasters = new Hashtable<String, MasterPage>();
	private static int valueHolderId = 0;

	/**
	 * returns an object model for the HTML of this page. if the page already cached, it is returned.
	 * 
	 * @param charSet
	 * @param isPortlet
	 *            TODO
	 * @param page
	 * 
	 * @return
	 * @throws Exception
	 */
	public static Page composePage(String jspxFileName, ServletContext context, String locale, String charSet, boolean isPortlet) throws Exception
	{
		Page cachedPage = getPage(jspxFileName, context, locale, false, charSet, isPortlet);
		if (cachedPage instanceof ContentPage)
		{
			String masterPageName = ((ContentPage) cachedPage).getMaster();
			if (StringUtility.isNullOrEmpty(masterPageName))
				throw new Exception("The Content Page must have a master page. Please make sure to set the attribute [master] of this page.");
			MasterPage masterPage = (MasterPage) getPage(masterPageName, context, locale, true, charSet, isPortlet);
			((ContentPage) cachedPage).setMasterPage(masterPage);
		}
		return cachedPage;
	}

	/**
	 * 1- get the localized name of the file. <br/>
	 * 2- get the cached page for the localized name. <br/>
	 * 3- if not cached try to parse it. <br/>
	 * 4- if not parsed try to get the cached page for original name. <br/>
	 * 5- if not cached, parse the page with the original name.
	 * 
	 * @param charSet
	 * @param isPortlet
	 *            TODO
	 */
	public static Page getPage(String jspxFileName, ServletContext context, String locale, boolean isMasterPage, String charSet, boolean isPortlet)
			throws Exception
	{
		String threadName = Thread.currentThread().getName();
		XmlParser parser = null;
		// 1- get the localized name of the file.
		String local_jspxFileName = getLocalizedName(jspxFileName, locale);
		Page cachedPage = null;
		// 2- get the cached page under the localized name.
		if (isMasterPage)
			cachedPage = getCachedMasterPage(local_jspxFileName);
		else
			cachedPage = getCahcedPage(local_jspxFileName);
		if (cachedPage == null)
		{
			// 3- if not cached try to parse it.
			Security.setThreadName("Parsing JSPX page [" + local_jspxFileName + "]");
			try
			{
				parser = getPageParser(local_jspxFileName, context, charSet);
				if (parser == null)
				{
					// 4- if file not exists try to get the cached page for
					// original name.
					if (isMasterPage)
						cachedPage = getCachedMasterPage(jspxFileName);
					else
						cachedPage = getCahcedPage(jspxFileName);

					// 5- if not cached, parse the page with the original name.
					if (cachedPage == null)
					{
						cachedPage = parsePage(getPageParser(jspxFileName, context, charSet), isMasterPage, jspxFileName, isPortlet);
						// 6.1- save the parsed page under the original name.
						if (isMasterPage)
							saveMasterPage((MasterPage) cachedPage, jspxFileName);
						else
							savePage(cachedPage, jspxFileName);
					}
				}
				else
				{
					// 4.2- if exist try to parse it.
					cachedPage = parsePage(parser, isMasterPage, jspxFileName, isPortlet);
					// 6.2- save the parsed page under the local name.
					if (isMasterPage)
						saveMasterPage((MasterPage) cachedPage, local_jspxFileName);
					else
						savePage(cachedPage, local_jspxFileName);
				}
			}
			catch (ParseException ex)
			{
				throw ex;
			}
			catch (JspxException e)
			{
				throw e;
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
				logger.error("getPage(jspxFileName=" + jspxFileName + ", context=" + context + ", locale=" + locale + ", isMasterPage=" + isMasterPage
						+ ", charSet=" + charSet + ", isPortlet=" + isPortlet + ")", e);
				throw new Exception(new StringBuilder("Could not find the jspx page [").append(jspxFileName)
						.append("]  please make sure that the file exists on the specidifed path").toString());
			}
		}
		Security.setThreadName(threadName);
		cachedPage.setCharSet(charSet);
		return cachedPage;
	}

	/**
	 * tries to find page with specific local, if the local is empty or default or localized page is not found, the default page
	 * is returned.
	 * 
	 * @param pageName
	 * @param context
	 * @param locale
	 * @param charSet
	 * @return
	 */
	protected static XmlParser getLocalizedPageParser(String pageName, ServletContext context, String locale, String charSet)
	{
		if (!StringUtility.isNullOrEmpty(locale) && !ResourceBundleUtility.isDefaultLocale(locale))
		{
			XmlParser parser = getPageParser(getLocalizedName(pageName, locale), context, charSet);
			if (parser != null)
				return parser;
		}
		return getPageParser(pageName, context, charSet);
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
	protected static XmlParser getPageParser(String pageName, ServletContext context, String charSet)
	{
		// [22 Oct 2015 11:09:41] [aeladawy] [add any special path defined by the developer.]
		try
		{
			return new XmlParser(new InputStreamReader(context.getResourceAsStream(RequestHandler.JSPX_FILES_PATH + pageName), charSet));
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
	 * @param isPortlet
	 *            TODO
	 * @throws IOException
	 */
	protected static Page parsePage(XmlParser parser, boolean isMasterPage, String jspxFileName, boolean isPortlet) throws Exception
	{
		StringBuilder sb = new StringBuilder();
		if (parser == null)
			throw new PageNotFoundException(jspxFileName);
		ParseEvent peek;
		while (!parser.peek(Xml.START_TAG, null, TagFactory.Page))
		{
			peek = parser.peek();
			if (peek.getType() == Xml.END_DOCUMENT)
			{
				logger.error("The end of the file [" + jspxFileName
						+ "] is reached without finding any jspx syntax... please make sure you had added the tag <page></page> around your html code");

				Page p = new Page();
				p.setHeaderText(sb.toString());

				return p;
			}
			else if (peek.getType() == Xml.START_TAG)
			{
				sb.append(Render.startTag).append(peek.getName());
				List<Attribute> ats = peek.getAttributes();
				if (ats != null)
					for (Attribute a : ats)
						sb.append(" ").append(a.getKey()).append("=\"").append(a.getValue()).append("\" ");
				sb.append(Render.endTag);
			}
			else if (peek.getType() == Xml.END_TAG)
				sb.append(Render.startEndTag).append(peek.getName()).append(Render.endTag);
			else if (peek.getType() == Xml.DOCTYPE)
			{
				sb.append(Render.startTag);
				sb.append("!");
				sb.append(peek.getText());
				sb.append(Render.endTag);
				sb.append(Render.newLine);
			}
			else if (peek.getType() == Xml.SCRIPTLET)
			{

				// [Jan 7, 2011 2:22:45 PM] [amr.eladawy] [remove the scriptlets as they should not be rendered to end user.]
				// sb.append(Render.startTag);
				// sb.append("%");
				// sb.append(peek.getText());
				// sb.append("%");
				// sb.append(Render.endTag);
				// sb.append(Render.newLine);
			}
			else
				sb.append(peek.getText());
			peek = parser.read();
		}

		String tagPlace = parser.getLineNumber() + ":" + parser.getColumnNumber();
		Page page = findCodeBehinde(PageModelComposer.ensureTag(parser, Xml.START_TAG, TagFactory.Page), isMasterPage, jspxFileName, isPortlet);
		page.setHeaderText(sb.toString());
		while (!parser.peek(Xml.END_TAG, null, TagFactory.Page))
		{
			peek = parser.peek();
			tagPlace = parser.getLineNumber() + ":" + parser.getColumnNumber();
			if (peek.getType() == Xml.START_TAG)
			{
				WebControl wc = prepareWebControl(peek, page, tagPlace).deSerialize(parser, page, null);

				page.addControl(wc);
				WebControl v = createValidator(page, wc, null, parser.getLineNumber(), parser.getColumnNumber());
				if (v != null)
					page.addControl(v);
			}

			else if (peek.getType() == Xml.END_DOCUMENT)
				throw new ParseException(
						"Reached End of document without finding closing tag for the tag [" + TagFactory.Page + "] palced at " + tagPlace, null,
						parser.getLineNumber(), parser.getColumnNumber());
			else if (peek.getType() == Xml.END_TAG && !peek.getName().equalsIgnoreCase(TagFactory.Page))
				throw new ParseException(
						"The tag [" + TagFactory.Page + "] palced at " + tagPlace + " has no matching closing tag ["
								+ (peek.getName() == null ? "self closing tag /> " : peek.getName()) + "]",
						null, parser.getLineNumber(), parser.getColumnNumber());
			else if (peek.getType() == Xml.COMMENT)
			{
				page.addControl(new StringLiteral("<!--" + peek.getText() + "-->"));
				peek = parser.read();
			}
			else if (peek.getType() == Xml.TEXT)
			{
				page.addControl(new StringLiteral(peek.getText()));
				peek = parser.read();
			}
			else if (peek.getType() == Xml.WHITESPACE)
			{
				page.addControl(new WhiteSpace(peek.getText()));
				peek = parser.read();
			}
			// [May 30, 2011 8:05:18 AM] [amr.eladawy] [supporting DocType]
			else if (peek.getType() == Xml.DOCTYPE)
			{
				page.addControl(new StringLiteral(
						new StringBuilder("").append(Render.startTag).append("!").append(peek.getText()).append(Render.endTag).toString()));
				peek = parser.read();
			}
			else if (peek.getType() == Xml.SCRIPTLET)
			{
				// Scriplets are not handled yet
			}
			// [May 30, 2011 8:06:54 AM] [amr.eladawy] [parse anything else as string]
			else
			{
				page.addControl(new StringLiteral(peek.getText()));
				peek = parser.read();
			}
		}
		// [Mar 18, 2012 5:46:20 PM] [amr.eladawy] [read the Page End tag]
		peek = parser.read();
		// [Mar 18, 2012 5:46:31 PM] [amr.eladawy] [if there is anything outside the <page></page> then display it as is]
		sb = new StringBuilder();
		while (true)
		{
			peek = parser.peek();
			if (peek.getType() == Xml.END_DOCUMENT)
				break;
			else if (peek.getType() == Xml.START_TAG)
			{
				sb.append(Render.startTag).append(peek.getName());
				List<Attribute> ats = peek.getAttributes();
				if (ats != null)
					for (Attribute a : ats)
						sb.append(a.getKey()).append("=\"").append(a.getValue()).append("\" ");
				sb.append(Render.endTag);
			}
			else if (peek.getType() == Xml.END_TAG)
				sb.append(Render.startEndTag).append(peek.getName()).append(Render.endTag);
			else if (peek.getType() == Xml.DOCTYPE)
			{
				sb.append(Render.startTag);
				sb.append("!");
				sb.append(peek.getText());
				sb.append(Render.endTag);
				sb.append(Render.newLine);
			}
			else if (peek.getType() == Xml.SCRIPTLET)
			{
				// sb.append(Render.startTag);
				// sb.append("%");
				// sb.append(peek.getText());
				// sb.append("%");
				// sb.append(Render.endTag);
				// sb.append(Render.newLine);
			}
			else
				sb.append(peek.getText());
			peek = parser.read();
		}
		page.setFooterText(sb.toString());
		return page;
	}

	/**
	 * If the control has an attribute [validate] then create and attache a validator 
	 * [1 Sep 2015 14:19:03]
	 * @author aeladawy 
	 * @param page
	 * @param wc
	 * @param parent 
	 * @param line 
	 * @param column 
	 * @return 
	 * @throws Exception 
	 */
	public static Validator createValidator(Page page, WebControl wc, WebControl parent, int line, int column) throws Exception
	{
		String validatorType = wc.getAttributeValue(WebControl.validate);
		if (!StringUtility.isNullOrEmpty(validatorType))
			return new ValidatorFactory().createRuntimeValidator(wc, page, parent, line, column);
		return null;
	}

	/**
	 * finds the control that handles the given tag. this control might be a declared control with the given id. if not found, the
	 * method generate new one.
	 * 
	 * @param peek
	 * @param page
	 * @param tagPlace
	 *            TODO
	 * @return
	 * @throws Exception
	 */
	public static WebControl prepareWebControl(ParseEvent peek, Page page, String tagPlace) throws Exception
	{
		String tagName = peek.getName();
		String id = null;
		WebControl declaredControl = null;
		Attribute idAttribute = peek.getAttributeIgnoreCase("id");
		if (idAttribute != null)
			id = idAttribute.getValue();
		// [Feb 21, 2011 9:38:51 PM] [Amr.ElAdawy] [this is should be not null or empty]
		if (!StringUtility.isNullOrEmpty(id))
		{
			// [Mar 18, 2012 4:57:37 PM] [amr.eladawy] [Validating that the ID is unique.]
			page.validateUniqueControlID(id, tagPlace);
			boolean isAnnotated = false, isDeclared = false;
			Field annotaedControl = page.annotatedWebControls.get(id);
			isAnnotated = annotaedControl != null;

			if (isAnnotated)
			{
				annotaedControl.setAccessible(true);
				try
				{
					declaredControl = (WebControl) annotaedControl.get(page);
				}
				catch (ClassCastException e)
				{
					throw new JspxException("Filed [" + annotaedControl.getName() + "] of type [" + annotaedControl.getType().getEnclosingClass()
							+ "] is not a webcontrol");
				}
			}
			else
			{
				isDeclared = PropertyAccessor.hasGetter(page, id);
				declaredControl = page.getControl(id);
			}
			if (declaredControl == null)
			{
				// [Jul 10, 2009 8:13:17 PM] [amr.eladawy] [if the control was null, then create new control ]
				declaredControl = TagFactory.getControl(tagName);
				// [Jul 10, 2009 7:58:50 PM] [amr.eladawy] [check if the control has a getter or not]
				if (isAnnotated || isDeclared)
				{
					// [Jul 10, 2009 7:59:15 PM] [amr.eladawy] [if the control has a getter, then this means that the developer
					// left the control null
					// . so here we will set it the auto generated value.
					// if there is a setter method, then throw error.]
					if (declaredControl instanceof InputFactory)
					{
						String inputType = null;
						Attribute typeAttribute = peek.getAttributeIgnoreCase("type");
						if (typeAttribute != null)
							inputType = typeAttribute.getValue();
						if (StringUtility.isNullOrEmpty(inputType))
							throw new JspxException("Input control [" + id + "] should have an attribute [type]");
						declaredControl = InputFactory.getConcreteInput(inputType);
						if (declaredControl == null)
							throw new JspxException("Input control [" + id + "] has an invalid value for attribute type [" + inputType + "]");

					}
					try
					{
						if (isAnnotated)
							annotaedControl.set(page, declaredControl);
						else
						{
							if (PropertyAccessor.hasSetter(page, id))
								PropertyAccessor.setProperty(page, id, declaredControl);
							else
								logger.warn("The control [" + id
										+ "] is declared with null value and no setter, so it will stay null, please add a setter or initialize the control");
						}
					}
					catch (IllegalArgumentException e)
					{
						throw new JspxException("The Declared Control  [" + id + "] should be the same type as the HTML control [" + id + "] ");
					}
				}
				else
					logger.trace("The Control [" + id + "] is not declared in the page, and will not be updated");
			}
			// [Nov 3, 2011 4:11:21 PM] [amr.eladawy] [this may fix the captcha typo mistake ]
			// if (tagName.equals(TagFactory.Capatcha) || tagName.equals(TagFactory.Captcha))
			// logger.debug("ignore captcha");
			// else

			if (declaredControl != null && !declaredControl.getTagName().equalsIgnoreCase(tagName))
			{
				String error = "The Declared Control [" + id + "] with Tagname [" + declaredControl.getTagName() + "] : does not match the HTML Tag ["
						+ tagName + "] at " + tagPlace + ",  please make sure to use the correct type";
				logger.error(error);
				throw new JspxException(error);
			}
			if (isDeclared || isAnnotated)
			{
				declaredControl.setDeclared(true);
				page.addDeclaredControl(declaredControl);
			}
		}
		else
		{
			// [Jul 10, 2009 8:10:10 PM] [amr.eladawy] [the control was not declared on the page, then create new one of it]
			declaredControl = TagFactory.getControl(tagName);
			// define an id if the developer didn't define one
			// [Jun 23, 2009 1:20:16 PM] [amr.eladawy] [If the Form has no ID, generate one for it]
			// [Jul 10, 2009 5:02:40 PM] [amr.eladawy] [Changed the Generated ID and replaced # to _]
			if (declaredControl instanceof ValueHolder || declaredControl instanceof LinkCommand || declaredControl instanceof AjaxPanel
					|| declaredControl instanceof Form || declaredControl instanceof CollapsePanel || declaredControl instanceof InternalValueHolder
					|| declaredControl instanceof ItemTemplate || declaredControl instanceof DataColumn)
				declaredControl.setId("jspx_generated_" + valueHolderId++);
			else
				declaredControl.getAttributes().remove(WebControl.idKey);
		}
		// [Feb 11, 2013 9:59:49 AM] [Amr.ElAdawy] [Adding control position]
		declaredControl.setPosition(tagPlace);
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

	private static void savePage(Page page, String jspxFileName)
	{
		synchronized (insertLock)
		{
			cachedPages.put(jspxFileName, page);
		}
	}

	private static void saveMasterPage(MasterPage masterPage, String masterFileName)
	{
		synchronized (masterInsertLock)
		{
			cachedMasters.put(masterFileName, masterPage);
		}
	}

	private static Page getCahcedPage(String jspxFileName)
	{
		synchronized (insertLock)
		{
			return cachedPages.get(jspxFileName);
		}
	}

	private static MasterPage getCachedMasterPage(String masterFileName)
	{
		synchronized (masterInsertLock)
		{
			return cachedMasters.get(masterFileName);
		}
	}

	/**
	 * finds the code behind page.
	 * 
	 * @param pageAttributes
	 * @param jspxFileName
	 *            TODO
	 * @param isPortlet
	 *            TODO
	 * @return
	 */
	public static Page findCodeBehinde(List<Attribute> pageAttributes, boolean isMasterPage, String jspxFileName, boolean isPortlet)
	{
		if (pageAttributes == null)
		{
			if (isMasterPage)
				return new MasterPage();
			else if (isPortlet)
				return new PortletPage();
			else
				return new Page();
		}
		Page page = null;
		Attribute att = null;
		String codeBehind = null;
		String master = null;
		for (int i = 0; i < pageAttributes.size(); i++)
		{
			att = pageAttributes.get(i);
			if (att.getKey().equalsIgnoreCase(Page.Controller))
				codeBehind = att.getValue().trim();
			else if (att.getKey().equalsIgnoreCase(Page.MasterKey))
				master = att.getValue().trim();
		}
		if (!StringUtility.isNullOrEmpty(codeBehind))
		{
			try
			{
				if (isPortlet)
					page = (PortletPage) Class.forName(codeBehind).newInstance();
				else if (isMasterPage)
					page = (MasterPage) Class.forName(codeBehind).newInstance();
				else if (StringUtility.isNullOrEmpty(master))
					page = (Page) Class.forName(codeBehind).newInstance();
				else
					page = (ContentPage) Class.forName(codeBehind).newInstance();
			}
			catch (Exception e)
			{
				if (e instanceof ClassNotFoundException)
					logger.error("Controller [" + codeBehind + "] Is not found, please make sure that you typed it correctly", e);
				else if (e instanceof ClassCastException)
					logger.error(
							"Controller [" + codeBehind
									+ "] Is not the same type as the Page, please extend from the correct parent (Page,ContentPage or MasterPage)",
							e);

			}
		}
		if (page == null)
		{
			// no page implementor, loading the default.
			if (isPortlet)
				page = new PortletPage();
			else if (isMasterPage)
				page = new MasterPage();
			else if (StringUtility.isNullOrEmpty(master))
				page = new Page();
			else
				page = new ContentPage();
		}
		page.setPageProperties(pageAttributes);
		page.setFilePath(jspxFileName);
		page.loadAnnotatedWebControls();
		return page;
	}

	/**
	 * removes the page from cache.
	 * 
	 * @param pageName
	 *            the name of the page to be removed from cache
	 */
	public static void removeChachedPage(String pageName)
	{
		cachedPages.remove(pageName);
		cachedMasters.remove(pageName);
	}

	/**
	 * clears all cached pages (normal and master pages)
	 * 
	 * @param pageName
	 */
	public static void clearChachedPages()
	{
		cachedPages.clear();
		cachedMasters.clear();
	}

	public static List<String> getCachedPages()
	{
		List<String> pages = new ArrayList<String>();
		for (String page : cachedPages.keySet())
		{
			pages.add(page);
		}
		return pages;
	}

	public static List<String> getCachedMasterPages()
	{
		List<String> pages = new ArrayList<String>();
		for (String page : cachedMasters.keySet())
		{
			pages.add(page);
		}
		return pages;
	}

}
