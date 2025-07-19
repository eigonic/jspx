/**
 *
 */
package eg.java.net.web.jspx.ui.controls.html.elements.dataitem;

import eg.java.net.web.jspx.engine.parser.TagFactory;
import eg.java.net.web.jspx.ui.controls.attrbs.JspxAttribute;
import eg.java.net.web.jspx.ui.controls.html.elements.hidden.HiddenGenericWebControl;
import eg.java.net.web.jspx.ui.pages.Page;

/**
 * @author Amr.ElAdawy
 *
 */
public class DataPK extends HiddenGenericWebControl {

    /**
     *
     */
    private static final long serialVersionUID = 5591469898545945631L;
    public static String SEQ_TYPE = "seq";
    public static String SQL_TYPE = "sql";
    public static String IDENTITY_TYPE = "identity";
    public static String TRIGGER = "trigger";
    protected static String NameKey = "name";
    @JspxAttribute
    protected String type = "type";
    @JspxAttribute
    protected String sql = "sql";
    /**
     * PK can have a default value to be used.
     */
    @JspxAttribute
    protected String defaultvalue = "defaultvalue";
    @JspxAttribute
    private final String seq = "sequence";

    /**
     * @param tagName
     */
    public DataPK() {
        super(TagFactory.DataPK);
    }

    /**
     * @param tagName
     * @param page
     */
    public DataPK(Page page) {
        super(TagFactory.DataPK, page);
    }

    public String getName() {
        return getAttributeValue(NameKey);
    }

    public void setName(String name) {
        setAttributeValue(NameKey, name);
    }

    public String getSequence() {
        return getAttributeValue(seq);
    }

    public void setSequence(String sequnce) {
        setAttributeValue(seq, sequnce);
    }

    public String getType() {
        return getAttributeValue(type);
    }

    public void setType(String typeValue) {
        setAttributeValue(type, typeValue);
    }

    public String getSql() {
        return getAttributeValue(sql);
    }

    public void setSql(String sqlValue) {
        setAttributeValue(sql, sqlValue);
    }

    public String getDefaultValue() {
        return getAttributeValue(defaultvalue);
    }

    public void setDefaultValue(String defaultvalueValue) {
        setAttributeValue(defaultvalue, defaultvalueValue);
    }


}
