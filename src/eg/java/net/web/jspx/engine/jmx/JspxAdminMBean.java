package eg.java.net.web.jspx.engine.jmx;

import java.util.List;

/**
 * MXBean interface for JSPX Administration.
 *
 * @author amr.eladawy
 */
public interface JspxAdminMBean {
    void clearAllChachedPages();

    void removeChachedPage(String pageName);

    List<String> getCachedPages();

    List<String> getCachedMasterPages();

    String getCharSet();

    void setCharSet(String charset);

    String getJspxVersion();

    boolean isJEXL();

    void setUseJEXL(boolean themeTwitter);

    boolean isThemeTwitter();

    void setThemeTwitter(boolean themeTwitter);

    boolean isShowSQL();

    void setShowSQL(boolean showSql);

    int getJspxBeanExpirationMinutes();

    void setJspxBeanExpirationMinutes(int time);

    String getJspFilesPathe();

    void setJspFilesPathe(String path);

}
