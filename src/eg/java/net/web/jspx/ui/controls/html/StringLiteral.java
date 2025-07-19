/**
 * 
 */
package eg.java.net.web.jspx.ui.controls.html;

import java.util.HashMap;

import org.kxml.parser.XmlParser;

import eg.java.net.web.jspx.engine.util.StringUtility;
import eg.java.net.web.jspx.engine.util.ui.RenderPrinter;
import eg.java.net.web.jspx.ui.controls.WebControl;
import eg.java.net.web.jspx.ui.controls.html.elements.markers.IAjaxSubmitter;
import eg.java.net.web.jspx.ui.controls.html.elements.markers.ISubmitter;
import eg.java.net.web.jspx.ui.pages.Page;

/**
 * @author amr.eladawy
 * 
 */
public class StringLiteral extends WebControl
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 516008357632311830L;
	private String content;

	public StringLiteral(String content)
	{
		super();
		this.content = content;
		isContentControl = false;
	}

	/*
	 * (non-Javadoc)
	 * @see java.net.web.jspx.ui.controls.WebControl#render(java.io.OutputStream)
	 */
	@Override
	public void render(RenderPrinter outputStream) throws Exception
	{
		if (!StringUtility.isNullOrEmpty(content))
		{
			outputStream.write(getContent());
		}
	}

	protected String getContent()
	{
		String newValue = StringUtility.evaluateComplexVlaue(content.replace("\t", "").replace("\n", "").trim(), page, null);
		if (newValue == null)
			newValue = content;
		return newValue;
	}

	@Override
	protected void renderAttributes(RenderPrinter outputStream) throws Exception
	{
	}

	@Override
	protected void renderStyle(RenderPrinter outputStream) throws Exception
	{
	}

	public WebControl deSerialize(XmlParser parser, Page page, WebControl parent) throws Exception
	{
		return this;
	}

	public String getText()
	{
		return content;
	}

	public void setText(String content)
	{
		this.content = content;
	}

	@Override
	public WebControl clone(WebControl parent, Page page, ISubmitter submitter, IAjaxSubmitter ajaxSubmitter)
	{
		StringLiteral newLiteral = new StringLiteral(content);
		super.mergeBasicProperties(newLiteral);
		newLiteral.parent = parent;
		newLiteral.page = page;
		return newLiteral;
	}

	@Override
	public void saveViewState(HashMap<String, HashMap<String, String>> viewState)
	{
		viewState.put(getId(), this.viewstate);
	}

	public String toString()
	{
		return content;
	}

	@Override
	public String jspxString()
	{
		return content;
	}

}
