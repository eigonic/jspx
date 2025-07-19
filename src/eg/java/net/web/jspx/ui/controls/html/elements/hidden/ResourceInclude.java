/**
 * 
 */
package eg.java.net.web.jspx.ui.controls.html.elements.hidden;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eg.java.net.web.jspx.engine.parser.TagFactory;
import eg.java.net.web.jspx.engine.util.ResourceUtility;
import eg.java.net.web.jspx.engine.util.StringUtility;
import eg.java.net.web.jspx.engine.util.ui.RenderPrinter;
import eg.java.net.web.jspx.ui.controls.attrbs.JspxAttribute;
import eg.java.net.web.jspx.ui.pages.Page;

/**
 * @author Amr.ElAdawy
 *a control that is capable of loading and injecting resources into a page.
 *these resources can be an HTML page Text or anything else.
 */
public class ResourceInclude extends HiddenGenericWebControl
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 779388913245942377L;
	private static final Logger logger = LoggerFactory.getLogger(ResourceInclude.class);

	/**
	 * @param tagName
	 */
	public ResourceInclude()
	{
		super(TagFactory.ResourceInclude);
	}

	/**
	 * @param tagName
	 * @param page
	 */
	public ResourceInclude(Page page)
	{
		super(TagFactory.ResourceInclude, page);
	}

	public void render(RenderPrinter outputStream) throws Exception
	{
		try
		{
			if (isRendered() && isAccessible() && !StringUtility.isNullOrEmpty(getFile()))
				outputStream.write(ResourceUtility.readResource(page.getContext().getResourceAsStream(getFile())));
		}
		catch (Exception e)
		{
			logger.error("failed to import Resource [" + getFile() + "]- are you sure the file exists and its name is correct?", e);
		}
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
