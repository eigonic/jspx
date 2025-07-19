package eg.java.net.web.jspx.ui.controls.html.custom;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eg.java.net.web.jspx.engine.parser.TagFactory;
import eg.java.net.web.jspx.engine.util.ui.RenderPrinter;
import eg.java.net.web.jspx.ui.controls.WebControl;
import eg.java.net.web.jspx.ui.controls.html.GenericWebControl;
import eg.java.net.web.jspx.ui.controls.html.elements.markers.IAjaxSubmitter;
import eg.java.net.web.jspx.ui.controls.html.elements.markers.ISubmitter;
import eg.java.net.web.jspx.ui.pages.Page;
import eg.java.net.web.jspx.ui.pages.PortletPage;

/**
 * Base Control for all User Controls
 * @author amr.eladawy
 *
 */
public class UserControl extends GenericWebControl
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8350298103409261533L;

	/**
	* Logger for this class
	*/
	private static final Logger logger = LoggerFactory.getLogger(UserControl.class);

	private Page userPage;

	public void setUserPage(Page userPage)
	{
		this.userPage = userPage;
	}

	/**
	 * @param tagName
	 */
	public UserControl()
	{
		super(TagFactory.Control);
	}

	/**
	 * @param tagName
	 * @param page
	 */
	public UserControl(Page page)
	{
		super(TagFactory.Control, page);
	}

	@Override
	public List<WebControl> getControls()
	{
		return userPage.getControls();
	}

	private String location;

	public String getLocation()
	{
		return location;
	}

	public void setLocation(String location)
	{
		this.location = location;
	}

	@Override
	public WebControl clone(WebControl parent, Page page, ISubmitter submitter, IAjaxSubmitter ajaxSubmitter)
	{
		try
		{
			UserControl userControl = (UserControl) super.clone(parent, page, submitter, ajaxSubmitter);
			userControl.setUserPage(page.loadPortletPage(getLocation()));
			/////////////////////page.addPortletPage(userControl.userPage);
			return userControl;
		}
		catch (Exception e)
		{
			logger.error("cannot load User control for " + getLocation(), e);
		}
		return this;
	}

	@Override
	public void render(RenderPrinter outputStream) throws Exception
	{
		((PortletPage) userPage).renderContent();
	}
}
