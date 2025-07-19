/**
 *
 */
package eg.java.net.web.jspx.ui.pages;

import java.io.Serializable;

/**
 * @author amr.eladawy
 *
 */
public class MasterPage extends Page implements Serializable {
    private static final long serialVersionUID = 1L;

    protected ContentPage contentPage = new ContentPage();

    public MasterPage() {
        super();
        //		pageType = Page.MasterPage;
    }

    public ContentPage getContentPage() {
        return contentPage;
    }

    public void setContentPage(ContentPage contentPage) {
        this.contentPage = contentPage;
    }

}
