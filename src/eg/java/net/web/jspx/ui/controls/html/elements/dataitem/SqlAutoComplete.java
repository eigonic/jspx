/**
 *
 */
package eg.java.net.web.jspx.ui.controls.html.elements.dataitem;

import eg.java.net.web.jspx.engine.data.DAO;
import eg.java.net.web.jspx.engine.error.JspxException;
import eg.java.net.web.jspx.engine.parser.TagFactory;
import eg.java.net.web.jspx.engine.util.SqlUtility;
import eg.java.net.web.jspx.engine.util.StringUtility;
import eg.java.net.web.jspx.ui.controls.WebControl;
import eg.java.net.web.jspx.ui.controls.attrbs.JspxAttribute;
import eg.java.net.web.jspx.ui.controls.html.elements.hidden.HiddenGenericWebControl;
import eg.java.net.web.jspx.ui.controls.html.elements.markers.IAjaxSubmitter;
import eg.java.net.web.jspx.ui.controls.html.elements.markers.ISubmitter;
import eg.java.net.web.jspx.ui.pages.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Amr.ElAdawy
 *
 */
public class SqlAutoComplete extends HiddenGenericWebControl {
    private static final long serialVersionUID = 3178121943601626300L;
    private static final Logger logger = LoggerFactory.getLogger(SqlAutoComplete.class);
    @JspxAttribute
    protected static String dataSource = "datasource";
    protected List<DataParam> parameters = new ArrayList<DataParam>();
    @JspxAttribute
    protected String sql = "sql";
    @JspxAttribute
    protected String parameter = "parameter";
    @JspxAttribute
    protected String caseSensitiveKey = "casesensitive";

    /**
     * @param tagName
     */
    public SqlAutoComplete() {
        super(TagFactory.SqlAutoComplete);
    }

    /**
     * @param tagName
     * @param page
     */
    public SqlAutoComplete(Page page) {
        super(TagFactory.SqlAutoComplete, page);
    }

    public String getDataSource() {
        return getAttributeValue(dataSource);
    }

    public void setDataSource(String dataSourceVal) {
        setAttributeValue(dataSource, dataSourceVal);
    }

    public String getSql() {
        return getAttributeValue(sql);
    }

    public void setSql(String sqlValue) {
        setAttributeValue(sql, sqlValue);
    }

    public String getParameter() {
        return getAttributeValue(parameter);
    }

    public void setParameter(String parameterValue) {
        setAttributeValue(parameter, parameterValue);
    }

    public boolean getCaseSensitive() {
        return getAttributeBooleanValue(caseSensitiveKey);
    }

    public void setCaseSenstive(boolean caseSenstive) {
        setAttributeBooleanValue(caseSensitiveKey, caseSenstive);
    }

    public WebControl clone(WebControl parent, Page page, ISubmitter submitter, IAjaxSubmitter ajaxSubmitter) {
        SqlAutoComplete thisControl = (SqlAutoComplete) super.clone(parent, page, submitter, ajaxSubmitter);
        for (WebControl control : thisControl.controls)
            if (control instanceof DataParam)
                thisControl.parameters.add((DataParam) control);
        return thisControl;
    }

    /**
     * gets the list of data based on the given search key
     *
     * @return
     */
    public List<Object> getData(String paramValue) {
        String sanitizedParamValue = SqlUtility.encodeForSQL(paramValue);
        logger.debug("ParamValue converted from [" + paramValue + "] to [" + sanitizedParamValue + "]");
        if (StringUtility.isNullOrEmpty(getDataSource()))
            throw new JspxException("The datasource attribute cannot be null in the SqlAutoComplete [" + this.getId() + "]");
        String sql = getSql();
        if (StringUtility.isNullOrEmpty(sql))
            throw new JspxException("The sql attribute cannot be null in the SqlAutoComplete [" + this.getId() + "]");
        for (DataParam param : parameters)
            sql = param.formatSql(sql, getCaseSensitive());
        String name = getParameter();
        if (StringUtility.isNullOrEmpty(name))
            name = "?";

        if (!sql.toLowerCase().contains(name.toLowerCase())) {
            logger.error("sql [" + sql + "] does not contain the name [" + name + "]");
            throw new JspxException("SqlAutoComplete [" + getId() + "] has the sql statement [" + sql + "] that does not contain the parameter ["
                    + name + "] Please make sure of the parameter exists and matches case");
        }
        sql = sql.replace(name, paramValue);

        return DAO.getAutoCompleteList(getDataSource(), sql);
    }
}
