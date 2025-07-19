/**
 * 
 */
package eg.java.net.web.jspx.ui.controls;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.kxml.parser.XmlParser;

import eg.java.net.web.jspx.engine.error.JspxException;
import eg.java.net.web.jspx.engine.parser.PageModelComposer;
import eg.java.net.web.jspx.engine.util.Security;
import eg.java.net.web.jspx.engine.util.StringUtility;
import eg.java.net.web.jspx.engine.util.ui.RenderPrinter;
import eg.java.net.web.jspx.ui.controls.attrbs.Attribute;
import eg.java.net.web.jspx.ui.controls.attrbs.JspxAttribute;
import eg.java.net.web.jspx.ui.controls.html.elements.dataitem.DataPK;
import eg.java.net.web.jspx.ui.controls.html.elements.dataitem.DataParam;
import eg.java.net.web.jspx.ui.controls.html.elements.markers.IAjaxSubmitter;
import eg.java.net.web.jspx.ui.controls.html.elements.markers.ISubmitter;
import eg.java.net.web.jspx.ui.controls.html.elements.markers.ItemDataBound;
import eg.java.net.web.jspx.ui.controls.html.elements.markers.ValueHolder;
import eg.java.net.web.jspx.ui.pages.Page;

/**
 * Base Web UI Control for all controls
 * 
 * @author amr.eladawy
 * 
 */
public abstract class WebControl implements Serializable
{
	private static final long serialVersionUID = 7537990844781139564L;

	protected Page page;

	protected String tagName;

	protected WebControl parent;

	protected Hashtable<String, Attribute> attributes = new Hashtable<String, Attribute>();

	protected Hashtable<String, Attribute> style = new Hashtable<String, Attribute>();

	protected List<WebControl> controls = new ArrayList<WebControl>();

	protected boolean isContentControl = true;
	@JspxAttribute
	public static String idKey = "id";
	@JspxAttribute
	protected static String nameKey = "name";
	@JspxAttribute
	protected static String cssClassKey = "class";
	@JspxAttribute
	protected static String rendered = "rendered";
	@JspxAttribute
	protected static String included = "included";
	@JspxAttribute
	protected static String ajaxsubmitter = "ajaxsubmitter";
	@JspxAttribute
	protected static String originalId = "original_id";

	@JspxAttribute
	public static String validate = "validate";
	@JspxAttribute
	protected boolean visible = true;
	protected boolean parentRendered = true;
	@JspxAttribute
	private String isSafe = "issafe";
	@JspxAttribute
	protected static String allowedroles = "allowedroles";

	@JspxAttribute
	protected static String deniedroles = "deniedroles";

	// [Feb 11, 2013 10:00:21 AM] [Amr.ElAdawy] [the position of the control on the HTML page]
	String position = "";

	public String getTagName()
	{
		return tagName;
	}

	public String getAllowedRoles()
	{
		return getAttributeValue(allowedroles);
	}

	public void setAllowedRoles(String allowedrolesValue)
	{
		setAttributeValue(allowedroles, allowedrolesValue);
	}

	public String getDeniedRoles()
	{
		return getAttributeValue(deniedroles);
	}

	public void setDeniedRoles(String deniedRolesValue)
	{
		setAttributeValue(deniedroles, deniedRolesValue);
	}

	protected HashMap<String, String> viewstate = new HashMap<String, String>();

	protected Hashtable<String, Integer> internalAttribtes = new Hashtable<String, Integer>();

	/**
	 * there are some attributes that should not be rendered to the end user, hence this method add them to the collection of
	 * internalAttributes, any child has its own controls should be adding them.
	 */
	protected void loadInternalAttributes()
	{
		internalAttribtes.put(rendered, 0);
		internalAttribtes.put(allowedroles, 0);
		internalAttribtes.put(deniedroles, 0);
		internalAttribtes.put(isSafe, 0);
		internalAttribtes.put(included, 0);
		internalAttribtes.put(ajaxsubmitter, 0);
		internalAttribtes.put(originalId, 0);
		internalAttribtes.put(validate, 0);
	}

	protected boolean isDataModelChild;

	public static String TRUE = "true";

	public static String FALSE = "false";

	/**
	 * tells if this is a paged declared control.
	 */
	protected boolean isDeclared;

	// Normal Submitter (Form)
	protected ISubmitter mySubmitter = null;

	// ajax Submitter
	protected IAjaxSubmitter myAjaxSubmitter = null;

	public WebControl()
	{
		loadInternalAttributes();
	}

	public WebControl(Page page)
	{
		this();
		this.page = page;
	}

	public WebControl(String tageName)
	{
		this();
		this.tagName = tageName;
	}

	public WebControl(String tagName, Page page)
	{
		this(page);
		this.tagName = tagName;
	}

	/**
	 * renders the content of this control to the stream
	 * 
	 * @param outputStream
	 */
	public abstract void render(RenderPrinter outputStream) throws Exception;

	public abstract WebControl deSerialize(XmlParser parser, Page page, WebControl parent) throws Exception;

	public void addAttribute(Attribute attribute)
	{
		Attribute storedAttribute = attributes.get(attribute.getKey().toLowerCase());
		if (storedAttribute == null)
		{
			storedAttribute = new Attribute(attribute.getKey().toLowerCase(), attribute.getValue());
			attributes.put(attribute.getKey().toLowerCase(), attribute);
		}
		else
			storedAttribute.setValue(attribute.getValue());

	}

	/**
	 * called from the page to tell the control to save its viewstat to the page viewstate.
	 * 
	 * @param viewState
	 */
	public abstract void saveViewState(HashMap<String, HashMap<String, String>> viewState);

	/**
	 * called from the page to give the control the posted viewstate to update its. iterates over the viewstate items and update
	 * the atttributes with the value.
	 * 
	 * @param viewState
	 */
	public void loadViewState(HashMap<String, String> viewState)
	{
		if (viewState == null)
			return;
		this.viewstate = viewState;
		for (Iterator<String> i = viewstate.keySet().iterator(); i.hasNext();)
		{
			String key = i.next();
			attributes.put(key, new Attribute(key, viewstate.get(key)));
		}
	}

	/**
	 * checks whether this attribute is internal or not.
	 * 
	 * @param attributeKey
	 * @return
	 */
	protected boolean isInternalAttribute(String attributeKey)
	{
		return internalAttribtes.get(attributeKey.toLowerCase()) != null;
	}

	protected Attribute getAttribute(String key)
	{
		if (key == null)
			return null;
		if (StringUtility.isNullOrEmpty(key))
			return new Attribute("", "");
		Attribute attribute = attributes.get(key.toLowerCase());
		if (attribute == null)
		{
			attribute = new Attribute(key.toLowerCase(), "");
			attributes.put(key, attribute);
		}
		return attribute;
	}

	public Object getAttributeValueObject(String key)
	{
		return getAttribute(key.toLowerCase()).getValueObject(page);
	}

	public String getAttributeValue(String key)
	{
		return getAttribute(key.toLowerCase()).getValue(page);
	}

	protected String getAttributeValue(String key, String dateformat)
	{
		return getAttribute(key.toLowerCase()).getValue(page, dateformat);
	}

	public void setAttributeValue(String key, String value)
	{
		key = key.toLowerCase();
		getAttribute(key).setValue(value);
		// if this is not viewstate loading phase, put this new value in the
		// viewstate.
		if (page != null && page.PageStatus != Page.PageViewState && page.PageStatus != Page.PageCompose && page.PageStatus != Page.PageRendering)
		{
			// if the value is empty, remove the attribute from the view state.
			if (StringUtility.isNullOrEmpty(value))
				viewstate.remove(key);
			else
				viewstate.put(key, value);
		}

	}

	protected int getAttributeIntValue(String key)
	{
		try
		{
			return Integer.parseInt(getAttribute(key.toLowerCase()).getValue(page));
		}
		catch (Exception e)
		{
		}
		return 0;
	}

	protected void setAttributeIntValue(String key, int value)
	{
		setAttributeValue(key, String.valueOf(value));
	}

	/**
	 * Active True (Means that there should be a value "true")
	 * Empty value means FALSE
	 * 
	 * @param key
	 * @return
	 */
	protected boolean getAttributeBooleanValue(String key)
	{
		return TRUE.equalsIgnoreCase(getAttribute(key.toLowerCase()).getValue(page));
	}

	protected void setAttributeBooleanValue(String key, boolean value)
	{
		setAttributeValue(key, value ? TRUE : FALSE);
	}

	public void addStyle(String pair)
	{
		String[] parts = pair.split(":");
		if (parts.length != 2)
			throw new JspxException("[" + pair + "] is not a vaild style pair. It should be something like \"display:none\"");
		addStyle(new Attribute(parts[0], parts[1]));
	}

	public void addStyle(String key, String value)
	{
		addAttribute(new Attribute(key, value));
	}

	public void addStyle(Attribute styleAttribute)
	{
		Attribute storedAttribute = style.get(styleAttribute.getKey().toLowerCase());
		if (storedAttribute == null)
		{
			storedAttribute = new Attribute(styleAttribute.getKey().toLowerCase(), styleAttribute.getValue());
			style.put(styleAttribute.getKey(), styleAttribute);
		}
		else
			storedAttribute.setValue(styleAttribute.getValue());

	}

	protected Attribute getStyle(String key)
	{
		Attribute attribute = style.get(key.toLowerCase());
		if (attribute == null)
		{
			attribute = new Attribute(key.toLowerCase(), "");
			style.put(key, attribute);
		}
		return attribute;
	}

	/**
	 * loads this instance with the given control. called in the parsing phase. returns this instance.
	 * 
	 * @param control
	 */
	public WebControl merge(WebControl control)
	{
		page = control.getPage();
		parent = control.getParent();
		Hashtable<String, Attribute> passedAttributes = control.getAttributes();
		for (Iterator<String> i = passedAttributes.keySet().iterator(); i.hasNext();)
		{
			String key = i.next();
			attributes.put(key, passedAttributes.get(key));
		}
		Hashtable<String, Attribute> passedStyle = control.getStyle();
		for (Iterator<String> i = passedStyle.keySet().iterator(); i.hasNext();)
		{
			String key = i.next();
			style.put(key, passedStyle.get(key));
		}
		// setRendered(control.getRendered());
		visible = control.isVisible();
		controls = control.getControls();
		return this;
	}

	/**
	 * returns a new instance of this control loaded with the existing variables,.
	 * 
	 * @param page
	 *            TODO
	 * @param submitter
	 *            TODO
	 * @param ajaxSubmitter
	 *            TODO
	 * @param control
	 */
	public abstract WebControl clone(WebControl parent, Page page, ISubmitter submitter, IAjaxSubmitter ajaxSubmitter);

	/**
	 * moves the basic properties of this instance to the given control.
	 * 
	 * @param control
	 */
	protected void mergeBasicProperties(WebControl control)
	{
		control.visible = visible;
		// control.setRendered(isRendered());
		// control.parent = parent;
		control.isContentControl = isContentControl;
	}

	protected abstract void renderStyle(RenderPrinter outputStream) throws Exception;

	protected abstract void renderAttributes(RenderPrinter outputStream) throws Exception;

	/**
	 * adds new control to the controls collection.
	 * 
	 * @param control
	 */
	public void addControl(WebControl control)
	{
		control.setParent(this);
		// control.setIndentLevel(indentLevel + 1);
		control.setPage(this.page);
		controls.add(control);
		// [1 Sep 2015 15:54:29] [aeladawy] [check that there is a runtime validator]
		try
		{
			WebControl v;
			v = PageModelComposer.createValidator(page, control, null, 0, 0);
			if (v != null)
				controls.add(v);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * tells whether there is an attribute with the given key.
	 * 
	 * @param key
	 * @return
	 */
	protected boolean hasAttribute(String key)
	{
		return attributes.get(key) != null;
	}

	public WebControl findControl(String id, boolean recursive)
	{
		for (WebControl control : controls)
			if (control.getId().equals(id))
				return control;
			else if (recursive)
			{
				WebControl c = control.findControl(id, true);
				if (c != null)
					return c;
			}
		return null;
	}

	public String getWidth()
	{
		return getStyle("width").getValue(page);
	}

	public String getHeight()
	{
		return getStyle("height").getValue(page);
	}

	public void setWidth(String width)
	{
		getStyle("width").setValue(width);
	}

	public void setHeight(String height)
	{
		getStyle("height").setValue(height);
	}

	public String toString()
	{
		return new StringBuilder("<").append(tagName).append(" id=").append(getId() == null ? "" : getId()).append(">\n").append(attributes)
				.toString();
	}

	public String jspxString()
	{
		StringBuilder sb = new StringBuilder("<").append(tagName).append(" ");
		for (Attribute attribute : attributes.values())
			if (!attribute.getKey().equalsIgnoreCase("name") || this instanceof DataPK || this instanceof DataParam)
				sb.append(attribute.getKey()).append("=\"").append(attribute.getValue()).append("\" ");
		if (isContentControl)
		{
			sb.append(">");
			for (WebControl control : controls)
				sb.append(control.jspxString());
			sb.append("</").append(tagName).append(">");
		}
		else
			sb.append("/>");
		return sb.toString();
	}

	public String getId()
	{
		return getAttributeValue(idKey);
	}

	public void setId(String id)
	{
		setAttributeValue(nameKey, id);
		setAttributeValue(idKey, id);
	}

	public String getName()
	{
		return getId();
	}

	public void setName(String name)
	{
		setId(name);
	}

	public String getCssClass()
	{
		return getAttributeValue(cssClassKey);
	}

	public void setCssClass(String cssClass)
	{
		setAttributeValue(cssClassKey, cssClass);
	}

	public Page getPage()
	{
		return page;
	}

	public void setPage(Page page)
	{
		this.page = page;
	}

	public WebControl getParent()
	{
		return parent;
	}

	public void setParent(WebControl parent)
	{
		this.parent = parent;
	}

	public Hashtable<String, Attribute> getAttributes()
	{
		return attributes;
	}

	public void setAttributes(Hashtable<String, Attribute> attributes)
	{
		this.attributes = attributes;
	}

	public List<WebControl> getControls()
	{
		return controls;
	}

	public void setControls(List<WebControl> controls)
	{
		this.controls = controls;
		// [Jun 11, 2009 7:28:50 PM] [amr.eladawy] [update the parent of each control in the controls collection to point to this
		// instance]
		for (WebControl webControl : this.controls)
			webControl.setParent(this);
	}

	// public String getRendered()
	// {
	// return getAttribute(rendered).getValue();
	// }
	//
	// public void setRendered(String renderedString)
	// {
	// setAttributeValue(rendered, renderedString);
	// }

	public String getIncluded()
	{
		return getAttribute(included).getValue();
	}

	public void setIncluded(String includedStr)
	{
		setAttributeValue(included, includedStr);
	}

	public boolean isIncluded()
	{
		String val = getAttribute(included.toLowerCase()).getValue(page);
		val = StringUtility.isNullOrEmpty(val) ? TRUE : val;
		if (parent == null)
			return TRUE.equalsIgnoreCase(val);
		else
		{
			return TRUE.equalsIgnoreCase(val) && parent.isIncluded();
		}
	}

	public boolean isRendered()
	{
		String val = getAttribute(rendered.toLowerCase()).getValue(page);
		val = StringUtility.isNullOrEmpty(val) ? TRUE : val;

		return TRUE.equalsIgnoreCase(val);

	}

	/**
	 * traverse into the parents until it find the first hidden control.
	 * 
	 * @Author Amr.ElAdawy Sep 19, 2013 9:21:30 AM
	 * @return
	 */
	public boolean isFinallyRendered()
	{
		String val = getAttribute(rendered.toLowerCase()).getValue(page);
		val = StringUtility.isNullOrEmpty(val) ? TRUE : val;

		if (TRUE.equalsIgnoreCase(val))
		{
			if (getParent() == null)
				return true;
			else
				return getParent().isFinallyRendered();
		}
		return false;

	}

	/**
	 * if the user in the allowed role, the control is display.
	 * 
	 * @return
	 */
	public boolean isAccessible()
	{
		return Security.isAccessible(getAllowedRoles(), getDeniedRoles(), page);
	}

	public void setRendered(boolean renderedBoolean)
	{
		setAttributeBooleanValue(rendered, renderedBoolean);
	}

	public boolean isVisible()
	{
		return visible;
	}

	public void setVisible(boolean visible)
	{
		this.visible = visible;
	}

	public Hashtable<String, Attribute> getStyle()
	{
		return style;
	}

	public boolean isDeclared()
	{
		return isDeclared;
	}

	public void setDeclared(boolean isDeclared)
	{
		this.isDeclared = isDeclared;
	}

	public void setStyle(Hashtable<String, Attribute> style)
	{
		this.style = style;
	}

	protected boolean childOfItemDataBound(boolean recursive)
	{
		if (this.parent instanceof ItemDataBound)
		{
			return true;
		}
		if (recursive)
		{
			if (this.parent != null)
				if (this.parent.childOfItemDataBound(true))
					return true;
		}
		return false;
	}

	public void setDataModelChild(boolean isDataModelChild)
	{
		this.isDataModelChild = isDataModelChild;
		if (this instanceof ValueHolder)
			page.addItemDataBoundChildControl(this.getId(), this);
		for (WebControl control : controls)
		{
			control.setDataModelChild(isDataModelChild);
		}
	}

	public ISubmitter getMySubmitter()
	{
		return mySubmitter;
	}

	public void setMySubmitter(ISubmitter mySubmitter)
	{
		this.mySubmitter = mySubmitter;
	}

	public IAjaxSubmitter getMyAjaxSubmitter()
	{
		// [6 Jul 2015 09:13:33] [aeladawy] [if the user set the attribute of the ajaxsubmitter, then use it]
		String val = getAttributeValue(ajaxsubmitter);
		if (!StringUtility.isNullOrEmpty(val) && page != null)
		{
			WebControl wc = null;
			try
			{
				wc = page.getControl(val);
			}
			catch (IllegalAccessException e)
			{
				e.printStackTrace();
			}
			try
			{
				if (wc == null)
					wc = page.getUndeclaredControl(val);
			}
			catch (IllegalAccessException e)
			{
				e.printStackTrace();
			}
			// [6 Jul 2015 09:21:55] [aeladawy] [if WC is not found or is not an IAjaxSubmitter then return the default.]
			return (IAjaxSubmitter) (wc == null || !(wc instanceof IAjaxSubmitter) ? myAjaxSubmitter : wc);

		}
		return myAjaxSubmitter;
	}

	public void setMyAjaxSubmitter(IAjaxSubmitter myAjaxSubmitter)
	{
		this.myAjaxSubmitter = myAjaxSubmitter;
	}

	/**
	 * This is the ID that is added to the end of each Child Control ID
	 */
	private String idSuffix;

	public String getIdSuffix()
	{
		return idSuffix;
	}

	public void setIdSuffix(String idSuffix)
	{
		this.idSuffix = idSuffix;
	}

	/**
	 * @param safe
	 *            the safe to set
	 */
	public void setIsSafe(boolean safe)
	{
		setAttributeBooleanValue(this.isSafe, safe);
	}

	/**
	 * @return the safe
	 */
	public boolean getIsSafe()
	{
		return getAttributeBooleanValue(isSafe);
	}

	public String getPosition()
	{
		return position;
	}

	public void setPosition(String position)
	{
		this.position = position;
	}

}
