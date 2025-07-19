package eg.java.net.web.jspx.ui.controls.html.elements.select;

import java.util.HashMap;

import eg.java.net.web.jspx.engine.parser.TagFactory;
import eg.java.net.web.jspx.engine.util.ui.RenderPrinter;
import eg.java.net.web.jspx.ui.controls.attrbs.Attribute;
import eg.java.net.web.jspx.ui.controls.attrbs.JspxAttribute;
import eg.java.net.web.jspx.ui.controls.html.GenericWebControl;
import eg.java.net.web.jspx.ui.controls.html.StringLiteral;
import eg.java.net.web.jspx.ui.pages.Page;

public class Option extends GenericWebControl
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4046712051420660923L;

	public Option()
	{
		super(TagFactory.Option);
	}

	public Option(Page page)
	{
		super(TagFactory.Option, page);
	}

	public void setText(String text)
	{
		getContentControl().setText(text);
		if (page != null && page.PageStatus != Page.PageViewState && page.PageStatus != Page.PageCompose)
			viewstate.put(content, text);
	}

	public String getText()
	{
		return getContentControl().getText();
	}

	protected StringLiteral getContentControl()
	{
		StringLiteral literal = null;
		if (controls.size() > 0)
			literal = ((StringLiteral) controls.get(0));
		else
		{
			literal = new StringLiteral("");
			addControl(literal);
		}
		return literal;
	}

	/*
		public WebControl deSerialize(XmlParser parser, Page page, WebControl parent) throws Exception
		{
			ParseEvent peek = parser.peek();
			parseStartTag(parser);
			// [Jun 1, 2011 4:57:24 PM] [amr.eladawy] [fix loop forever in the option]
			StringBuilder text = new StringBuilder();
			String tagPlace = parser.getLineNumber() + ":" + parser.getColumnNumber();
			while (!parser.peek(Xml.END_TAG, null, tagName))
			{
				peek = parser.peek();
				if (peek.getType() == Xml.TEXT)
					text.append(parser.readText());
				else if (peek.getType() == Xml.WHITESPACE)
					text.append(parser.readText());
				else if (peek.getType() == Xml.END_DOCUMENT || (peek.getType() == Xml.END_TAG && !peek.getName().equalsIgnoreCase(tagName)))
					throw new ParseException("The tag [" + tagName + "] palced at " + tagPlace + " has no matching closing tag ["
							+ (peek.getName() == null ? "self closing tag /> " : peek.getName()) + "]", null, parser.getLineNumber(),
							parser.getColumnNumber());
			}
			setText(text.toString());
			PageModelComposer.ensureTag(parser, Xml.END_TAG, tagName);
			setPage(page);
			getContentControl().setPage(page);
			// return wireToDeclaredControl();
			return this;
		}
	*/
	/**
	 * overrides the parent to obtain the content and then put it into the
	 * string literal.
	 */
	public void loadViewState(HashMap<String, String> viewState)
	{
		super.loadViewState(viewState);
		Attribute contentAtt = attributes.get(content);
		if (contentAtt != null)
		{
			getContentControl().setText(contentAtt.getValue(page));
			attributes.remove(content);
		}
	}

	private String selected = "selected";

	public void setSelected(boolean isSelected)
	{
		setAttributeValue(selected, isSelected ? "selected" : "");
	}

	public boolean getSelected()
	{
		return getAttributeValue(selected).equalsIgnoreCase("selected");
	}

	/**
	 * overridden to remove the selected attribute in case this option is not
	 * selected.
	 */
	protected void renderAttributes(RenderPrinter outputStream) throws Exception
	{
		if (!getSelected())
			attributes.remove(selected);
		super.renderAttributes(outputStream);
	}

	private String value = "value";

	public String getValue()
	{
		return getAttributeValue(value);
	}

	public void setValue(String valueString)
	{
		setAttributeValue(value, valueString);
	}

	@JspxAttribute
	protected String valueProperty = "valueProperty";

	public String getValueProperty()
	{
		return getAttributeValue(valueProperty);
	}

	public void setValueProperty(String valueProperty)
	{
		setAttributeValue(this.valueProperty, valueProperty);
	}

	@JspxAttribute
	protected String textProperty = "textProperty";

	public String getTextProperty()
	{
		return getAttributeValue(textProperty);
	}

	public void setTextProperty(String textProperty)
	{
		setAttributeValue(this.textProperty, textProperty);
	}

}
