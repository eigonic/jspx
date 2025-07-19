/**
 * 
 */
package eg.java.net.web.jspx.ui.pages;

/**
 * @author amr.eladawy
 * 
 */
public class MasterPage extends Page
{
	private static final long serialVersionUID = 1L;

	protected ContentPage contentPage = new ContentPage();

	public MasterPage()
	{
		super();
		//		pageType = Page.MasterPage;
	}

	public ContentPage getContentPage()
	{
		return contentPage;
	}

	public void setContentPage(ContentPage contentPage)
	{
		this.contentPage = contentPage;
	}

}
