/**
 * 
 */
package eg.java.net.web.jspx.ui.controls.html.elements.dataitem;

import eg.java.net.web.jspx.engine.parser.TagFactory;
import eg.java.net.web.jspx.engine.util.StringUtility;
import eg.java.net.web.jspx.engine.util.ui.RenderPrinter;
import eg.java.net.web.jspx.ui.controls.WebControl;
import eg.java.net.web.jspx.ui.controls.attrbs.JspxAttribute;
import eg.java.net.web.jspx.ui.controls.html.elements.hidden.HiddenGenericWebControl;
import eg.java.net.web.jspx.ui.controls.html.elements.markers.IAjaxSubmitter;
import eg.java.net.web.jspx.ui.controls.html.elements.markers.ISubmitter;
import eg.java.net.web.jspx.ui.pages.Page;

/**
 * Control that is containing Template for Item within DataColumn.
 * 
 * @author amr.eladawy
 * 
 */
public class ItemTemplate extends HiddenGenericWebControl
{

	private static final long serialVersionUID = -6837715425525722654L;

	protected String var;

	protected Object row;

	public ItemTemplate()
	{
		super(TagFactory.ItemTemplate);
	}

	public ItemTemplate(Page page)
	{
		super(TagFactory.ItemTemplate, page);
	}

	@Override
	/**
	 * overridden to make the custom render of the column.
	 */
	public void render(RenderPrinter outputStream) throws Exception
	{
		if (!isRendered())
			return;
		if (getOverrideId())
			setIdSuffix(Integer.toString(rowIndex));
		// [Sep 11, 2013 6:10:23 PM] [amr.eladawy] [add row index to jxel]
		page.getPageJEXLContext().set("jspxRowIndex", rowIndex);
		renderChildren(outputStream);
	}

	@Override
	/**
	 * set the parent (DataColumn) to has an Item Template
	 */
	public WebControl clone(WebControl parent, Page page, ISubmitter submitter, IAjaxSubmitter ajaxSubmitter)
	{
		WebControl me = super.clone(parent, page, submitter, ajaxSubmitter);
		((DataColumn) me.getParent()).setHasItemTemplate(true);
		return me;
	}

	protected int rowIndex;

	public void setRowIndex(int rowIndex)
	{
		this.rowIndex = rowIndex;
	}

	/**
	 * boolean wheather to change id of the controls inside the item template.
	 */
	@JspxAttribute
	protected static String overrideId = "overrideId";

	public void setOverrideId(boolean overrideIdVal)
	{
		setAttributeBooleanValue(overrideId, overrideIdVal);
	}

	public boolean getOverrideId()
	{
		String val = getAttribute(overrideId.toLowerCase()).getValue(page);
		val = StringUtility.isNullOrEmpty(val) ? TRUE : val;

		return TRUE.equalsIgnoreCase(val);
	}
}
