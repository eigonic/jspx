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
import eg.java.net.web.jspx.ui.pages.Page;

/**
 * @author amr.eladawy
 * 
 */
public class LinkCommand extends GenericWebControl
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1041374738316056753L;

	@JspxAttribute
	protected static String confirmationKey = "confirmation";

	@JspxAttribute
	protected static String onServerClickKey = "onServerClick";

	@JspxAttribute
	protected static String onClickKey = "onClick";

	@JspxAttribute
	protected static String textKey = "text";

	@JspxAttribute
	protected static String GroupKey = "group";

	@JspxAttribute
	protected static String submitOnceKey = "submitonce";
	@JspxAttribute
	protected static String postNormalKey = "postnormal";

	@JspxAttribute
	protected static String commandArgs = "commandargs";

	public LinkCommand()
	{
		super(TagFactory.LinkCommand);
	}

	public LinkCommand(Page page)
	{
		super(TagFactory.LinkCommand, page);
	}

	protected void loadInternalAttributes()
	{
		super.loadInternalAttributes();
		internalAttribtes.put(onServerClickKey.toLowerCase(), 0);
		internalAttribtes.put(onClickKey.toLowerCase(), 0);
		internalAttribtes.put(GroupKey.toLowerCase(), 0);
		internalAttribtes.put(submitOnceKey.toLowerCase(), 0);
		internalAttribtes.put(postNormalKey.toLowerCase(), 0);
		internalAttribtes.put(commandArgs.toLowerCase(), 0);
		internalAttribtes.put(confirmationKey.toLowerCase(), 0);
		internalAttribtes.put(textKey.toLowerCase(), 0);
	}

	public String getTageName()
	{
		return TagFactory.HyperLink;
	}

	protected void renderAttributes(RenderPrinter outputStream) throws Exception
	{
		StringBuilder onclickJavaScript = new StringBuilder("");
		String onClick = getOnClick();
		if (!StringUtility.isNullOrEmpty(onClick))
			onclickJavaScript.append(onClick).append(";");

		new Attribute("onClick", onclickJavaScript.toString()).render(outputStream, page);

		String onServerClick = getOnServerClick();
		if (!StringUtility.isNullOrEmpty(onServerClick))
		{
			StringBuilder javaScript = new StringBuilder("javascript:");
			String group = getGroup();
			String confirmation = getConfirmation();
			if (!StringUtility.isNullOrEmpty(confirmation))
				javaScript.append("if (confirm('").append(confirmation).append("'))");
			if (StringUtility.isNullOrEmpty(group))
				javaScript.append("postBack(");
			else
				javaScript.append("validatePostBack(");
			javaScript.append("'").append(getId()).append("','");
			javaScript.append(onServerClick).append("','");
			javaScript.append(mySubmitter == null ? "null" : ((WebControl) mySubmitter).getId()).append("','");
			javaScript.append(getMyAjaxSubmitter()  == null || getPostNormal() ? "null" : ((WebControl) getMyAjaxSubmitter() ).getId()).append("'");
			if (!StringUtility.isNullOrEmpty(group))
				javaScript.append(",").append("'").append(group).append("'");
			if (getSubmitOnce())
				javaScript.append(",true");
			else
				javaScript.append(",false");
			if (!StringUtility.isNullOrEmpty(getCommandArgs()))
				javaScript.append(",'").append(getCommandArgs()).append("'");

			javaScript.append(")");
			// rendering the composed on click;
			new Attribute("href", javaScript.toString()).render(outputStream, page);
		}

		super.renderAttributes(outputStream);

	}

	/**
	 * sets the text displayed when hovering over the link command
	 * 
	 * @param text
	 */
	public void setText(String text)
	{
		setAttributeValue(textKey, text);
	}

	/**
	 * gets the text displayed when hovering over the link command
	 * 
	 * @param text
	 */
	public String getText()
	{
		return getAttributeValue(textKey);
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

	public String getOnServerClick()
	{
		return getAttributeValue(onServerClickKey);
	}

	public void setOnServerClick(String onClientClick)
	{
		setAttributeValue(onServerClickKey, onClientClick);
	}

	public String getOnClick()
	{
		return getAttributeValue(onClickKey);
	}

	public void setOnClick(String onClick)
	{
		setAttributeValue(onClickKey, onClick);
	}

	public String getGroup()
	{
		return getAttributeValue(GroupKey);
	}

	public void setGroup(String group)
	{
		setAttributeValue(GroupKey, group);
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

	public String getCommandArgs()
	{
		return getAttributeValue(commandArgs);
	}

	public void setCommandArgs(String commandArgsValue)
	{
		setAttributeValue(commandArgs, commandArgsValue);
	}

}
