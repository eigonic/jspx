/**
 *
 */
package eg.java.net.web.jspx.ui.controls.html.elements.dataitem;

import eg.java.net.web.jspx.engine.data.DAO;
import eg.java.net.web.jspx.engine.parser.TagFactory;
import eg.java.net.web.jspx.engine.util.StringUtility;
import eg.java.net.web.jspx.ui.controls.attrbs.JspxAttribute;
import eg.java.net.web.jspx.ui.controls.html.elements.hidden.HiddenGenericWebControl;
import eg.java.net.web.jspx.ui.pages.Page;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * FK is an internal control that works as Lookup providers. there is a need to populate a dorp down from a lookup DB table. Data
 * Foreign Key is
 *
 * @author Amr.ElAdawy
 *
 */
public class DataLookup extends HiddenGenericWebControl {

    /**
     *
     */
    private static final long serialVersionUID = -731537765225717903L;
    @JspxAttribute
    protected static String items = "items";
    protected Hashtable<String, String> elements;
    protected List<String> keys = new ArrayList<String>();
    protected String dataSource = "datasource";
    @JspxAttribute
    protected String caseSenstiveKey = "casesenstive";
    @JspxAttribute
    private final String key = "key";
    @JspxAttribute
    private final String value = "value";
    @JspxAttribute
    private final String tableName = "table";
    @JspxAttribute
    private final String sqlKey = "sql";
    @JspxAttribute
    private final String lazyKey = "lazy";

    /**
     * @param tagName
     */
    public DataLookup() {
        super(TagFactory.DataLookup);
    }

    /**
     * @param tagName
     * @param page
     */
    public DataLookup(Page page) {
        super(TagFactory.DataLookup, page);
    }

    public String getKey() {
        return getAttributeValue(key);
    }

    public void setKey(String fieldName) {
        setAttributeValue(key, fieldName);
    }

    public String getValue() {
        return getAttributeValue(value);
    }

    public void setValue(String valueFieldName) {
        setAttributeValue(value, valueFieldName);
    }

    public String getTableName() {
        return getAttributeValue(tableName);
    }

    public void setTableName(String dbTable) {
        setAttributeValue(tableName, dbTable);
    }

    public Object getItems() {
        return getAttributeValueObject(items);
    }

    public void setItems(String itemsVal) {
        setAttributeValue(items, itemsVal);
    }

    /**
     * Retrieves the data from the Lookup DB.
     *
     * @return
     */
    public Hashtable<String, String> dataBind() {
        if (elements == null) {
            Object itmesObject = getItems();
            if (itmesObject instanceof Map<?, ?>) {
                elements = (Hashtable<String, String>) itmesObject;
                for (Object o : elements.keySet())
                    keys.add(String.valueOf(o));
            } else {
                loadDataSource();
                if (StringUtility.isNullOrEmpty(getSql()))
                    elements = DAO.loadLookup(getDataSource(),
                            "SELECT " + getKey() + " , " + getValue() + " FROM  " + getTableName() +
                                    " ORDER BY " + getKey() + " ASC", keys);
                else
                    elements = DAO.loadLookup(getDataSource(), getSql(), keys);
            }
        }
        return elements;
    }

    /**
     * returns the SQL that can be used to reverse lookup
     * [14 Apr 2015 16:20:12]
     * @author aeladawy
     * @return
     */
    public String getReverseSql(String value) {
        return "SELECT " + getKey() + "  FROM  " + getTableName() + " WHERE " + getValue() +
                " = '" + value + "'";
    }

    public String getDataSource() {
        return getAttributeValue(dataSource);
    }

    public void setDataSource(String dataSourceVal) {
        setAttributeValue(dataSource, dataSourceVal);
    }

    /**
     * looks up the given value in this lookup table.
     *
     * @param Value
     * @return
     */
    public Object lookup(String key) {
        String value = null;
        if (elements == null && !getLazy())
            dataBind();
        if (elements != null)
            value = elements.get(key);
        if (value == null) {
            String where = " WHERE " + getKey() + "='" + key + "'   ";
            String finalString = getSqlString(where);
            loadDataSource();
            value = DAO.lookup(getDataSource(), finalString, key, getValue()).toString();
        }
        return value;
    }

    private String getSqlString(String where) {
        String sqlString = getSql().toLowerCase();
        // if there is no SQL, construct one
        String finalString = "";
        if (StringUtility.isNullOrEmpty(sqlString)) {
            finalString = "SELECT " + getValue() + " FROM  " + getTableName() + where;
        } else {
            // use the SQL
            // [Jun 7, 2009 12:02:54 PM] [amr.eladawy] if the developer provided a sql then wrap it with the select * from ()
            // to be easily used.
            finalString = "select * from (" + sqlString + ") lutemp" + where;

        }
        return finalString;
    }

    protected void loadDataSource() {
        if (parent instanceof DataTable && StringUtility.isNullOrEmpty(getDataSource()))
            setDataSource(((DataTable) parent).getDataSource());
    }

    public String getSql() {
        return getAttributeValue(sqlKey);
    }

    public void setSql(String sqlVal) {
        setAttributeValue(sqlKey, sqlVal);
    }

    /**
     * if this data lookup is lazy it will not cache the lookup table values in the memory, used when the lookup table is sooooo
     * big.
     *
     * @return
     */
    public boolean getLazy() {
        return getAttributeBooleanValue(lazyKey);
    }

    /**
     * if this data lookup is lazy it will not cache the lookup table values in the memory, used when the lookup table is sooooo
     * big.
     *
     * @return
     */
    public void setLazy(boolean lazy) {
        setAttributeBooleanValue(lazyKey, lazy);
    }

    public List<String> getKeys() {
        return keys;
    }

    public void setKeys(List<String> keys) {
        this.keys = keys;
    }

    public boolean getcaseSenstive() {
        return getAttributeBooleanValue(caseSenstiveKey);
    }

    public void setcaseSenstive(boolean caseSenstive) {
        setAttributeBooleanValue(caseSenstiveKey, caseSenstive);
    }

}
