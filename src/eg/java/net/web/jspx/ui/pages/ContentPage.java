/**
 *
 */
package eg.java.net.web.jspx.ui.pages;

import eg.java.net.web.jspx.engine.util.StringUtility;

import java.io.Serializable;

/**
 * content page that is using master pages.
 *
 * @author amr.eladawy
 *
 */
public class ContentPage extends Page implements Serializable {
    private static final long serialVersionUID = 1L;

    protected MasterPage masterPage;

    protected String master;

    public ContentPage() {
        super();
        //		pageType = Page.ContentPage;
    }

    public String getMaster() {
        if (StringUtility.isNullOrEmpty(master))
            return master;
        if (master.charAt(0) != '/')
            master = "/" + master;
        return master;
    }

    public void setMaster(String master) {
        this.master = master;
    }

    public MasterPage getMasterPage() {
        return masterPage;
    }

    public void setMasterPage(MasterPage masterPage) {
        this.masterPage = masterPage;
        this.masterPage.setContentPage(this);
    }

    public void populateWith(Page page) {
        setMaster(((ContentPage) page).getMaster());
        super.populateWith(page);
    }

}
