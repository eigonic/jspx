/**
 * 
 */
package eg.java.net.web.jspx.ui.controls.html.elements.fieldSet;

import eg.java.net.web.jspx.engine.parser.TagFactory;
import eg.java.net.web.jspx.ui.controls.html.GenericWebControl;
import eg.java.net.web.jspx.ui.pages.Page;

/**
 * @author Sherin.Ghonaim
 */
public class FieldSet extends GenericWebControl
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -918950237478345615L;
	protected Legend legend = new Legend();

	public FieldSet()
	{
		super(TagFactory.FieldSet);
	}

	public FieldSet(Page page)
	{
		super(TagFactory.FieldSet, page);
	}

	public Legend getLegend()
	{
		return legend;
	}

	public void setLegend(Legend legend)
	{
		this.legend = legend;
	}

}
