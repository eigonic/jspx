/**
 * 
 */
package eg.java.net.web.jspx.ui.controls.html.elements;

import java.util.ArrayList;

import eg.java.net.web.jspx.engine.parser.TagFactory;
import eg.java.net.web.jspx.engine.util.StringUtility;
import eg.java.net.web.jspx.engine.util.ui.RenderPrinter;
import eg.java.net.web.jspx.ui.controls.WebControl;
import eg.java.net.web.jspx.ui.controls.attrbs.JspxAttribute;
import eg.java.net.web.jspx.ui.controls.html.GenericWebControl;
import eg.java.net.web.jspx.ui.controls.html.StringLiteral;
import eg.java.net.web.jspx.ui.pages.Page;

/**
 * @author amr.eladawy
 * 
 */
public class HyperLink extends GenericWebControl
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5559717380123925663L;
	private static String navUrl = "href";

	public HyperLink()
	{
		super(TagFactory.HyperLink);
	}

	public HyperLink(Page page)
	{
		super(TagFactory.HyperLink, page);
	}

	public String getNavigationURL()
	{
		return getAttributeValue(navUrl);
	}

	public void setNavigationURL(String url)
	{
		setAttributeValue(navUrl, url);
	}

	/**
	 * overidden to change the src attribute by adding the absolute url.
	 */
	protected void renderAttributes(RenderPrinter outputStream) throws Exception
	{
		loadInternalAttributes();
		// [Nov 12, 2010 2:43:49 AM] [Amr.ElAdawy] [save the original url ]
		String urlNative = null;
		if (attributes.get(navUrl) != null)
			urlNative = attributes.get(navUrl).getValue();
		// [Nov 12, 2010 2:44:36 AM] [Amr.ElAdawy] [evaluate the url to final value]
		String url = getNavigationURL();
		if (!url.toLowerCase().startsWith("javascript:") && !url.toLowerCase().startsWith("#"))
			setNavigationURL(composeURL(url));
		super.renderAttributes(outputStream);
		// [Nov 12, 2010 2:45:06 AM] [Amr.ElAdawy] [save back th original url]
		if (StringUtility.containsEL(urlNative))
			attributes.get(navUrl).setValue(urlNative);
	}

	@JspxAttribute
	protected String target = "target";

	public String getTarget()
	{
		return getAttributeValue(target);
	}

	public void setTarget(String targetValue)
	{
		setAttributeValue(target, targetValue);
	}

	public void setText(String text)
	{
		// [Jul 23, 2012 2:19:02 PM] [Amr.ElAdawy] [isContentControl is aproperty of the web control itself not a function of 
		// of the children]
		//isContentControl = true;
		// if the content controls are not String literal, replace them with StringLiteral.
		StringLiteral sl = getContentControl();
		if (sl == null)
			controls = new ArrayList<WebControl>();
		getContentControl().setText(text);
		if (page != null && page.PageStatus != Page.PageViewState && page.PageStatus != Page.PageCompose && page.PageStatus != Page.PageRendering)
			viewstate.put(content, text);
	}

	public String getText()
	{
		StringLiteral sl = getContentControl();
		if (sl == null)
			return "";
		return sl.getText();
	}

	protected StringLiteral getContentControl()
	{
		StringLiteral literal = null;
		if (controls.size() > 0)
		{
			if (controls.get(0) instanceof StringLiteral)
				literal = ((StringLiteral) controls.get(0));
		}
		else
		{
			literal = new StringLiteral("");
			literal.setPage(page);
			addControl(literal);
		}
		return literal;
	}

	public String getValue()
	{
		return getText();
	}

	public void setValue(String valueString)
	{
		setText(valueString);
	}

	private String valueBinding;

	public String getValueBinding()
	{
		if (StringUtility.isNullOrEmpty(valueBinding))
		{
			String value = getText();
			if (StringUtility.containsEL(value))
				valueBinding = value;
		}
		return valueBinding;
	}

	public void setValueBinding(String valueBinding)
	{
		this.valueBinding = valueBinding;
	}
}
