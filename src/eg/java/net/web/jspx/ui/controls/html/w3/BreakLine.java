/**
 * 
 */
package eg.java.net.web.jspx.ui.controls.html.w3;

import eg.java.net.web.jspx.engine.parser.TagFactory;
import eg.java.net.web.jspx.engine.util.ui.Render;
import eg.java.net.web.jspx.engine.util.ui.RenderPrinter;
import eg.java.net.web.jspx.ui.controls.html.GenericWebControl;
import eg.java.net.web.jspx.ui.pages.Page;

/**
 * @author amr.eladawy
 *
 */
public class BreakLine extends GenericWebControl
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7196079348966676573L;

	/**
	 * @param tagName
	 */
	public BreakLine()
	{
		super(TagFactory.BreakLine);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param tagName
	 * @param page
	 */
	public BreakLine(Page page)
	{
		super(TagFactory.BreakLine, page);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void render(RenderPrinter outputStream) throws Exception
	{
		outputStream.write(Render.startTag);
		outputStream.write(TagFactory.BreakLine);
		outputStream.write(Render.closeTag);
	}

}
