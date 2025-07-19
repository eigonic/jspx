package eg.java.net.web.jspx.ui.controls.html.elements.select;

import eg.java.net.web.jspx.engine.parser.TagFactory;
import eg.java.net.web.jspx.ui.controls.html.GenericWebControl;
import eg.java.net.web.jspx.ui.pages.Page;

public class OptionGroup extends GenericWebControl
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4834285103331411370L;

	public OptionGroup()
	{
		super(TagFactory.OptionGroup);
	}

	public OptionGroup(Page page)
	{
		super(TagFactory.OptionGroup, page);
	}
}
