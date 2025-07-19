/**
 * 
 */
package eg.java.net.web.jspx.ui.controls.html.elements.fieldSet;

import eg.java.net.web.jspx.engine.parser.TagFactory;
import eg.java.net.web.jspx.ui.controls.WebControl;
import eg.java.net.web.jspx.ui.controls.html.GenericWebControl;
import eg.java.net.web.jspx.ui.controls.html.elements.markers.IAjaxSubmitter;
import eg.java.net.web.jspx.ui.controls.html.elements.markers.ISubmitter;
import eg.java.net.web.jspx.ui.pages.Page;

/**
 * @author Sherin.Ghonaim Legend Control for the FieldSet Control
 */
public class Legend extends GenericWebControl
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7188595477182145127L;

	public Legend()
	{
		super(TagFactory.Legend);
	}

	public Legend(Page page)
	{
		super(TagFactory.Legend, page);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eg.java.net.web.jspx.ui.controls.html.GenericWebControl#clone(eg.java
	 * .net.web.jspx.ui.controls.WebControl, eg.java.net.web.jspx.ui.pages.Page,
	 * eg.java.net.web.jspx.ui.controls.html.elements.markers.ISubmitter,
	 * eg.java.net.web.jspx.ui.controls.html.elements.markers.IAjaxSubmitter)
	 */
	@Override
	public WebControl clone(WebControl parent, Page page, ISubmitter submitter, IAjaxSubmitter ajaxSubmitter)
	{

		Legend legend = (Legend) super.clone(parent, page, submitter, ajaxSubmitter);
		if (parent instanceof FieldSet)
			((FieldSet) parent).setLegend(legend);
		return legend;

	}
}
