package eg.java.net.web.jspx.ui.controls.html.w3;

import java.util.List;

import org.kxml.Xml;
import org.kxml.parser.ParseEvent;
import org.kxml.parser.XmlParser;

import eg.java.net.web.jspx.engine.parser.PageModelComposer;
import eg.java.net.web.jspx.engine.parser.TagFactory;
import eg.java.net.web.jspx.engine.util.ui.Render;
import eg.java.net.web.jspx.ui.controls.WebControl;
import eg.java.net.web.jspx.ui.controls.attrbs.Attribute;
import eg.java.net.web.jspx.ui.controls.html.StringLiteralContainer;
import eg.java.net.web.jspx.ui.pages.Page;

/**
 * Pre tag should be parsed as string 
 * @author amr.eladawy
 * Mar 29, 2012 7:12:00 PM
 */
public class Pre extends StringLiteralContainer
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8035818620232627777L;

	/**
	 * @param tagName
	 * @param page
	 */
	public Pre(Page page)
	{
		super(TagFactory.Pre, page);
	}

	/**
	 * 
	 */
	public Pre()
	{
		super(TagFactory.Pre);
	}

	/* (non-Javadoc)
	 * @see eg.java.net.web.jspx.ui.controls.html.StringLiteralContainer#deSerialize(org.kxml.parser.XmlParser, eg.java.net.web.jspx.ui.pages.Page, eg.java.net.web.jspx.ui.controls.WebControl)
	 */
	@Override
	public WebControl deSerialize(XmlParser parser, Page page, WebControl parent) throws Exception
	{
		ParseEvent peek = parser.peek();
		String tagPlace = parser.getLineNumber() + ":" + parser.getColumnNumber();
		parseStartTag(parser);
		StringBuilder text = new StringBuilder();
		while (!parser.peek(Xml.END_TAG, null, tagName))
		{
			peek = parser.peek();
			if (peek.getType() == Xml.END_DOCUMENT)
				break;
			else if (peek.getType() == Xml.START_TAG)
			{
				text.append(Render.startTag).append(peek.getName());
				List<Attribute> ats = peek.getAttributes();
				if (ats != null)
					for (Attribute a : ats)
						text.append(a.getKey()).append("=\"").append(a.getValue()).append("\" ");
				text.append(Render.endTag);
			}
			else if (peek.getType() == Xml.END_TAG)
				text.append(Render.startEndTag).append(peek.getName()).append(Render.endTag);
			else if (peek.getType() == Xml.DOCTYPE)
			{
				text.append(Render.startTag);
				text.append("!");
				text.append(peek.getText());
				text.append(Render.endTag);
				text.append(Render.newLine);
			}
			else if (peek.getType() == Xml.SCRIPTLET)
			{
				text.append(Render.startTag);
				text.append("%");
				text.append(peek.getText());
				text.append("%");
				text.append(Render.endTag);
				text.append(Render.newLine);
			}
			else
				text.append(peek.getText());
			peek = parser.read();
		}
		setText(text.toString());
		PageModelComposer.ensureTag(parser, Xml.END_TAG, tagName);
		setPage(page);
		getContentControl().setPage(page);
		return this;
	}
}
