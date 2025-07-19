/**
 * 
 */
package eg.java.net.web.jspx.ui.controls.html.elements;

import eg.java.net.web.jspx.engine.parser.TagFactory;
import eg.java.net.web.jspx.engine.util.StringUtility;
import eg.java.net.web.jspx.engine.util.ui.RenderPrinter;
import eg.java.net.web.jspx.ui.controls.attrbs.Attribute;
import eg.java.net.web.jspx.ui.controls.html.GenericWebControl;
import eg.java.net.web.jspx.ui.pages.Page;

/**
 * @author amr.eladawy
 *
 */
public class CollapsePanel extends GenericWebControl
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 337045636711143539L;
	protected static String ImageSufix = "_image";
	protected static String PanelSufix = "_panel";

	public CollapsePanel()
	{
		super(TagFactory.CollapsePanel);
	}

	public CollapsePanel(Page page)
	{
		super(TagFactory.CollapsePanel, page);
	}

	@Override
	public void render(RenderPrinter outputStream) throws Exception
	{
		if (!isRendered() || !isAccessible())
			return;
		String parentSuffixId = (parent != null && !StringUtility.isNullOrEmpty(parent.getIdSuffix())) ? parent.getIdSuffix() : "";
		Panel imagePanel = new Panel(page);
		// [Jun 9, 2009 8:07:10 PM] [amr.eladawy]  in order to make this item available for iterative IDs, 
		// as when located within ItemTemplate, the parent should be set
		imagePanel.setParent(parent);
		imagePanel.setId(getId() + ImageSufix);
		//image.setSrc(ResourceHandler.ResourcePrefix + ResourceUtility.Down);
		//		image.addAttribute(new Attribute(Input.onClientClickKey, "slidePanel('" + getId() + PanelSufix + parentSuffixId + "',this.id,'"
		//				+ composeURL(ResourceHandler.ResourcePrefix + ResourceUtility.Up) + "')"));
		imagePanel.addAttribute(new Attribute(Input.onClientClickKey, "$jspx('#" + getId() + PanelSufix + parentSuffixId
				+ "').toggle('slow');$jspx('#'+this.id).toggleClass('jspx_ms');setActivePanel('" + getId() + PanelSufix + parentSuffixId
				+ "',this.id);"));
		//		image.addAttribute(new Attribute("onmouseover", "$jspx('#" + getId() + PanelSufix + parentSuffixId + "').fadeIn('slow');"));
		//		image.addAttribute(new Attribute("onmouseout", "$jspx('#" + getId() + PanelSufix + parentSuffixId + "').fadeOut('slow');"));
		//		image.getStyle().put("cursor", new Attribute("cursor", "pointer"));

		imagePanel.setCssClass("jspx_ps");

		imagePanel.render(outputStream);

		Panel panel = new Panel(page);
		panel.setParent(parent);
		panel.setId(getId() + PanelSufix);
		panel.setControls(controls);

		panel.setCssClass(getCssClass());

		panel.setStyle(getStyle());
		//margin-left: auto; margin-right: auto; position: absolute;\
		panel.setVisible(false);
		//		panel.getStyle().put("z-index", new Attribute("z-index", "100"));
		panel.getStyle().put("position", new Attribute("position", "absolute"));
		panel.getStyle().put("margin-left", new Attribute("margin-left", "auto"));
		panel.getStyle().put("margin-right", new Attribute("margin-right", "auto"));

		panel.render(outputStream);

	}
}
