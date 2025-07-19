/**
 *
 */
package eg.java.net.web.jspx.engine.jmx;

import eg.java.net.web.jspx.engine.RequestHandler;
import eg.java.net.web.jspx.engine.parser.PageModelComposer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Jspx Bean Implementation for JMX.
 *
 * @author amr.eladawy
 *
 */
public class JspxAdmin implements JspxAdminMBean {
    public static final String BEAN_NAME = "eg.java.net.web.jspx.engine.jmx:type=JspxAdmin,name=JspxAdmin";
    private static final Logger logger = LoggerFactory.getLogger(JspxAdmin.class);

    public JspxAdmin() {
    }

    public void clearAllChachedPages() {
        logger.info("clearAllChachedPages() - start");

        PageModelComposer.clearChachedPages();

        logger.info("clearAllChachedPages() - end");
    }

    public void removeChachedPage(String pageName) {
        logger.info("removeChachedPage(pageName=" + pageName + ") - start");

        PageModelComposer.removeChachedPage(pageName);

        logger.info("removeChachedPage(pageName) - end");
    }

    public List<String> getCachedPages() {
        logger.info("getCachedPages() - start");

        List<String> returnList = PageModelComposer.getCachedPages();
        logger.info("getCachedPages() - end");
        return returnList;
    }

    public List<String> getCachedMasterPages() {
        logger.info("getCachedMasterPages() - start");

        List<String> returnList = PageModelComposer.getCachedMasterPages();
        logger.info("getCachedMasterPages() - end");
        return returnList;
    }

    public String getCharSet() {
        logger.info("getCharSet() - start");

        String returnString = RequestHandler.getCharSet();
        logger.info("getCharSet() - end");
        return returnString;
    }

    public void setCharSet(String charset) {
        logger.info("setCharSet(charset=" + charset + ") - start");

        RequestHandler.setCharSet(charset);

        logger.info("setCharSet(charset) - end");
    }

    public String getJspxVersion() {
        logger.info("getJspxVersion() - start");

        String returnString = RequestHandler.getJspxversion();
        logger.info("getJspxVersion() - end");
        return returnString;
    }

    public boolean isJEXL() {
        return RequestHandler.USE_JEXL;
    }

    public void setUseJEXL(boolean useJEXL) {
        RequestHandler.USE_JEXL = useJEXL;
    }

    /*
     * (non-Javadoc)
     *
     * @see eg.java.net.web.jspx.engine.jmx.JspxAdminMBean#isThemeTwitter()
     */
    public boolean isThemeTwitter() {
        return RequestHandler.THEME_TWITTER;
    }

    /*
     * (non-Javadoc)
     *
     * @see eg.java.net.web.jspx.engine.jmx.JspxAdminMBean#setThemeTwitter(boolean)
     */
    public void setThemeTwitter(boolean themeTwitter) {
        RequestHandler.THEME_TWITTER = themeTwitter;
    }

    public boolean isShowSQL() {
        return RequestHandler.SHOW_SQL;
    }

    public void setShowSQL(boolean showSql) {
        RequestHandler.SHOW_SQL = showSql;
    }

    public int getJspxBeanExpirationMinutes() {
        return RequestHandler.JSPX_BEAN_EXPIRATION_PERIOD_MINUTES;
    }

    public void setJspxBeanExpirationMinutes(int time) {
        RequestHandler.JSPX_BEAN_EXPIRATION_PERIOD_MINUTES = time;
    }

    public String getJspFilesPathe() {
        return RequestHandler.JSPX_FILES_PATH;
    }

    public void setJspFilesPathe(String path) {
        RequestHandler.JSPX_FILES_PATH = path;
    }
}
