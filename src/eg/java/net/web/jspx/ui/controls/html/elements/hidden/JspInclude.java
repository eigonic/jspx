package eg.java.net.web.jspx.ui.controls.html.elements.hidden;

import eg.java.net.web.jspx.engine.parser.TagFactory;
import eg.java.net.web.jspx.engine.util.ui.RenderPrinter;
import eg.java.net.web.jspx.ui.controls.attrbs.JspxAttribute;
import eg.java.net.web.jspx.ui.pages.Page;

public class JspInclude extends HiddenGenericWebControl
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3415443908595001239L;

	public JspInclude()
	{
		super(TagFactory.JspInclude);
	}

	public JspInclude(Page page)
	{
		super(TagFactory.JspInclude, page);
	}

	public void render(RenderPrinter outputStream) throws Exception
	{
		if (isRendered() && isAccessible())
			page.request.getRequestDispatcher(getFile()).include(page.request, page.response);
	}

	@JspxAttribute
	private static String fileKey = "file";

	public void setFile(String jspFile)
	{
		setAttributeValue(fileKey, jspFile);
	}

	public String getFile()
	{
		return getAttributeValue(fileKey);
	}
}
