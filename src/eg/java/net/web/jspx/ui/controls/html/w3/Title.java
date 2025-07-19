/**
 * 
 */
package eg.java.net.web.jspx.ui.controls.html.w3;

import eg.java.net.web.jspx.engine.parser.TagFactory;
import eg.java.net.web.jspx.ui.controls.WebControl;
import eg.java.net.web.jspx.ui.controls.html.elements.Label;
import eg.java.net.web.jspx.ui.controls.html.elements.markers.IAjaxSubmitter;
import eg.java.net.web.jspx.ui.controls.html.elements.markers.ISubmitter;
import eg.java.net.web.jspx.ui.pages.Page;

/**
 * Page Title
 * @author amr.eladawy
 *
 */
public class Title extends Label
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5533337006030176096L;

	public Title()
	{
		super();
		tagName = TagFactory.Title;
	}

	public Title(Page page)
	{
		super(page);
		tagName = TagFactory.Title;

	}

	/**
	 * overriden to add set the page Title control to this instance.
	 */
	@Override
	public WebControl clone(WebControl parent, Page page, ISubmitter submitter, IAjaxSubmitter ajaxSubmitter)
	{
		Title title = (Title) super.clone(parent, page, submitter, ajaxSubmitter);
		page.setTitleControl(title);
		return title;
	}

}
