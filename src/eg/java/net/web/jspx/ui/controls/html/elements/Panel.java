/**
 * 
 */
package eg.java.net.web.jspx.ui.controls.html.elements;

import eg.java.net.web.jspx.engine.parser.TagFactory;
import eg.java.net.web.jspx.ui.controls.html.GenericWebControl;
import eg.java.net.web.jspx.ui.pages.Page;

/**
 * @author amr.eladawy
 * 
 */
public class Panel extends GenericWebControl
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 540987427696584710L;

	public Panel()
	{
		super(TagFactory.Panel);
	}

	public Panel(Page page)
	{
		super(TagFactory.Panel, page);
	}

}
