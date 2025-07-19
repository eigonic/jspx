/**
 * 
 */
package eg.java.net.web.jspx.ui.controls.html.elements;

import eg.java.net.web.jspx.engine.parser.TagFactory;
import eg.java.net.web.jspx.engine.util.StringUtility;
import eg.java.net.web.jspx.engine.util.ui.RenderPrinter;
import eg.java.net.web.jspx.ui.controls.WebControl;
import eg.java.net.web.jspx.ui.controls.attrbs.Attribute;
import eg.java.net.web.jspx.ui.controls.attrbs.JspxAttribute;
import eg.java.net.web.jspx.ui.controls.html.GenericWebControl;
import eg.java.net.web.jspx.ui.controls.html.elements.markers.ValueHolder;
import eg.java.net.web.jspx.ui.pages.Page;

/**
 * @author amr.eladawy
 * 
 */
public abstract class Input extends GenericWebControl implements ValueHolder
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5252913415154942534L;

	public static String TextBox = "text";

	public static String HiddenField = "hidden";

	public static String CheckBox = "checkbox";

	public static String Radio = "radio";

	public static String Submit = "submit";

	public static String File = "file";

	public static String Button = "button";

	public static String Image = "image";

	public static String Reset = "reset";

	public static String Password = "password";

	@JspxAttribute
	public static String value = "value";
	@JspxAttribute
	public static String runtimevalue = "runtimevalue";

	@JspxAttribute
	protected static String type = "type";

	@JspxAttribute
	protected static String confirmationKey = "confirm";

	@JspxAttribute
	public static String onClientClickKey = "onClick";

	@JspxAttribute
	protected static String onServerClickKey = "onServerClick";
	@JspxAttribute
	protected static String maskKey = "mask";

	@JspxAttribute
	protected static String GroupKey = "group";

	@JspxAttribute
	protected static String submitOnceKey = "submitonce";
	@JspxAttribute
	protected static String postNormalKey = "postnormal";

	@JspxAttribute
	protected static String commandArgs = "commandargs";

	public Input(Page page)
	{
		this();
		setPage(page);
	}

	public Input()
	{
		super(TagFactory.Input);
		isContentControl = false;
		loadInternalAttributes();
	}

	protected void loadInternalAttributes()
	{
		super.loadInternalAttributes();
		internalAttribtes.put(onServerClickKey.toLowerCase(), 0);
		internalAttribtes.put(confirmationKey.toLowerCase(), 0);
		internalAttribtes.put(GroupKey.toLowerCase(), 0);
		internalAttribtes.put(styleKey.toLowerCase(), 0);
		internalAttribtes.put(rendered.toLowerCase(), 0);
		internalAttribtes.put(submitOnceKey.toLowerCase(), 0);
		internalAttribtes.put(onClientClickKey.toLowerCase(), 0);
		internalAttribtes.put(postNormalKey.toLowerCase(), 0);
		internalAttribtes.put(commandArgs.toLowerCase(), 0);
		internalAttribtes.put(maskKey.toLowerCase(), 0);
	}

	protected void renderAttributes(RenderPrinter outputStream) throws Exception
	{
		// rendering the composed on click;
		String onClickScript = createServerClientScript(getOnClientClick(), getOnServerClick(), getConfirmation());
		if (!StringUtility.isNullOrEmpty(onClickScript))
			new Attribute(onClientClickKey, onClickScript).render(outputStream, page);
		super.renderAttributes(outputStream);
	}

	/**
	 * overriden to add the mask script
	 */
	@Override
	public void render(RenderPrinter outputStream) throws Exception
	{
		String document = "";
		if (page != null && page.isAjaxPostBack && page.isMultiPartForm)
			document = ",window.parent.document";
		super.render(outputStream);
		if (!StringUtility.isNullOrEmpty(getMask()))
		{
			String script = new StringBuffer("$('#").append(getId()).append("'").append(document).append(").mask('").append(getMask())
					.append("',{placeholder:\"\"});").toString();
			page.addOnloadScript(script);
		}
	}

	/**
	 * composes Script String for Server and Client jscript.
	 * 
	 * @param client
	 * @param server
	 * @param confirm
	 * @return
	 */
	public String createServerClientScript(String client, String server, String confirm)
	{
		StringBuilder javaScript = new StringBuilder("");
		if (!StringUtility.isNullOrEmpty(client))
			javaScript.append(client).append(";");
		if (!StringUtility.isNullOrEmpty(confirm))
			javaScript.append("if (confirm('".concat(confirm).concat("')){"));

		String group = getGroup();
		if (!StringUtility.isNullOrEmpty(server))
		{
			if (StringUtility.isNullOrEmpty(group))
				javaScript.append("postBack(this.id,'");
			else
				javaScript.append("return (validatePostBack(this.id,'");
			javaScript.append(server).append("','");
			javaScript.append(mySubmitter == null ? "null" : ((WebControl) mySubmitter).getId()).append("','");
			javaScript.append(getMyAjaxSubmitter() == null || getPostNormal() ? "null" : ((WebControl) getMyAjaxSubmitter()).getId()).append("'");
			if (!StringUtility.isNullOrEmpty(group))
				javaScript.append(",").append("'").append(group).append("'");
			if (getSubmitOnce())
				javaScript.append(",true");
			else
				javaScript.append(",false");
			if (!StringUtility.isNullOrEmpty(getCommandArgs()))
				javaScript.append(",'").append(getCommandArgs()).append("'");
			if (!StringUtility.isNullOrEmpty(group))
				javaScript.append(")");
			javaScript.append(")");

		}

		if (!StringUtility.isNullOrEmpty(confirm))
			javaScript.append("}");

		// rendering the composed on click;
		return javaScript.toString();

	}

	/**
	 * gets the confirmation message to be displayed when clicking on this button.
	 * 
	 * @return
	 */
	public String getConfirmation()
	{
		return getAttributeValue(confirmationKey);
	}

	public void setConfirmation(String confirmation)
	{
		setAttributeValue(confirmationKey, confirmation);
	}

	public String getOnClientClick()
	{
		return getAttributeValue(onClientClickKey);
	}

	public void setOnClientClick(String onClientClick)
	{
		setAttributeValue(onClientClickKey, onClientClick);
	}

	public String getOnServerClick()
	{
		return getAttributeValue(onServerClickKey);
	}

	public void setOnServerClick(String onClick)
	{
		setAttributeValue(onServerClickKey, onClick);
	}

	public String getGroup()
	{
		return getAttributeValue(GroupKey);
	}

	public void setGroup(String group)
	{
		setAttributeValue(GroupKey, group);
	}

	public String getValue()
	{
		return getAttributeValue(value);
	}

	public void setValue(String valueString)
	{
		// [30 Aug 2015 12:33:09] [aeladawy] [if the page is loading postback data, and the original value is EL, then don't set the Value, keep the posted date in the Run time Value]
		if (page != null && page.PageStatus == page.PagePostBackData && StringUtility.isEL(getAttribute(value).getValue()))
			setAttributeValue(runtimevalue, valueString);
		else
			setAttributeValue(value, valueString);
	}

	public String getType()
	{
		return getAttributeValue(type);
	}

	public void setType(String typeString)
	{
		setAttributeValue(type, typeString);
	}

	public boolean getSubmitOnce()
	{
		return getAttributeBooleanValue(submitOnceKey);
	}

	public void setSubmitOnce(boolean once)
	{
		setAttributeBooleanValue(submitOnceKey, once);
	}

	public void setPostNormal(boolean postNormal)
	{
		setAttributeBooleanValue(postNormalKey, postNormal);
	}

	public boolean getPostNormal()
	{
		return getAttributeBooleanValue(postNormalKey);
	}

	public void setType(boolean submitOnce)
	{
		setAttributeValue(submitOnceKey, String.valueOf(submitOnce));
	}

	private String valueBinding;

	public String getValueBinding()
	{
		return calculateValueBinding(valueBinding, getAttribute(value));
	}

	public void setValueBinding(String valueBinding)
	{
		this.valueBinding = valueBinding;
	}

	public String getCommandArgs()
	{
		return getAttributeValue(commandArgs);
	}

	public void setCommandArgs(String commandArgsValue)
	{
		setAttributeValue(commandArgs, commandArgsValue);
	}

	public String getMask()
	{
		return getAttributeValue(maskKey);
	}

	public void setMask(String maskValue)
	{
		setAttributeValue(maskKey, maskValue);
	}

	public static String calculateValueBinding(String valueBinding, Attribute valueAttribute)
	{
		if (StringUtility.isNullOrEmpty(valueBinding))
		{
			if (valueAttribute != null)
			{
				String v = valueAttribute.getValue();
				if (StringUtility.containsEL(v))
					valueBinding = v;
			}
		}
		return valueBinding;
	}

}
