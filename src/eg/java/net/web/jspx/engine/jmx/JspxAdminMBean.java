package eg.java.net.web.jspx.engine.jmx;

import java.util.List;

/**
 * MXBean interface for JSPX Administration.
 * 
 * @author amr.eladawy
 * 
 */
public interface JspxAdminMBean
{
	public void clearAllChachedPages();

	public void removeChachedPage(String pageName);

	public List<String> getCachedPages();

	public List<String> getCachedMasterPages();

	public String getCharSet();

	public void setCharSet(String charset);

	public String getJspxVersion();

	public boolean isJEXL();

	public void setUseJEXL(boolean themeTwitter);

	public boolean isThemeTwitter();

	public void setThemeTwitter(boolean themeTwitter);

	public boolean isShowSQL();

	public void setShowSQL(boolean showSql);

	public void setJspxBeanExpirationMinutes(int time);

	public int getJspxBeanExpirationMinutes();

	public void setJspFilesPathe(String path);

	public String getJspFilesPathe();

}
