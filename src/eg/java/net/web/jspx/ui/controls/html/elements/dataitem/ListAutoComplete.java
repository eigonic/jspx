/**
 *
 */
package eg.java.net.web.jspx.ui.controls.html.elements.dataitem;

import eg.java.net.web.jspx.engine.parser.TagFactory;
import eg.java.net.web.jspx.engine.util.ELUtility;
import eg.java.net.web.jspx.engine.util.PropertyAccessor;
import eg.java.net.web.jspx.engine.util.StringUtility;
import eg.java.net.web.jspx.ui.controls.attrbs.JspxAttribute;
import eg.java.net.web.jspx.ui.controls.html.elements.hidden.HiddenGenericWebControl;
import eg.java.net.web.jspx.ui.pages.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Amr.ElAdawy
 *
 */
public class ListAutoComplete extends HiddenGenericWebControl {

    protected static final String searchMode_contains = "contains";
    protected static final String searchMode_startsWith = "startsWith";
    protected static final String searchMode_endsWith = "endsWith";
    /**
     *
     */
    private static final long serialVersionUID = -4246018420748366044L;
    private static final Logger logger = LoggerFactory.getLogger(ListAutoComplete.class);
    @JspxAttribute
    protected String items = "items";
    @JspxAttribute
    protected String searchMode = "searchMode";
    @JspxAttribute
    protected String caseSensitiveKey = "casesensitive";

    /**
     * @param tagName
     */
    public ListAutoComplete() {
        super(TagFactory.SqlAutoComplete);
    }

    /**
     * @param tagName
     * @param page
     */
    public ListAutoComplete(Page page) {
        super(TagFactory.SqlAutoComplete, page);
    }

    public String getItems() {
        return getAttributeValue(items);
    }

    public void setItems(String itemsValue) {
        setAttributeValue(items, itemsValue);
    }

    public String getSearchMode() {
        return getAttributeValue(searchMode);
    }

    public void setSearchMode(String searchModeValue) {
        setAttributeValue(searchMode, searchModeValue);
    }

    public boolean getCaseSensitive() {
        return getAttributeBooleanValue(caseSensitiveKey);
    }

    public void setCaseSenstive(boolean caseSenstive) {
        setAttributeBooleanValue(caseSensitiveKey, caseSenstive);
    }

    /**
     * gets the list of data based  on the given search key
     * @return
     */
    public List<Object> getData(String paramValue) {

        List<Object> finalList = new ArrayList<Object>();
        String itemsName = null;
        if (attributes.get(items) != null)
            itemsName = attributes.get(items).getValue();
        if (!StringUtility.isNullOrEmpty(itemsName)) {
            List<Object> source = evalItemsList(itemsName);
            if (source == null || StringUtility.isNullOrEmpty(paramValue))
                return finalList;
            for (Object o : source) {
                if (getCaseSensitive()) {
                    if (getSearchMode().equalsIgnoreCase(searchMode_contains) && String.valueOf(o).contains(paramValue))
                        finalList.add(o);
                    else if (getSearchMode().equalsIgnoreCase(searchMode_startsWith) && String.valueOf(o).startsWith(paramValue))
                        finalList.add(o);
                    else if (getSearchMode().equalsIgnoreCase(searchMode_endsWith) && String.valueOf(o).endsWith(paramValue))
                        finalList.add(o);
                } else {
                    if (getSearchMode().equalsIgnoreCase(searchMode_contains) && String.valueOf(o).toLowerCase().contains(paramValue.toLowerCase()))
                        finalList.add(o);
                    else if (getSearchMode().equalsIgnoreCase(searchMode_startsWith)
                            && String.valueOf(o).toLowerCase().startsWith(paramValue.toLowerCase()))
                        finalList.add(o);
                    else if (getSearchMode().equalsIgnoreCase(searchMode_endsWith)
                            && String.valueOf(o).toLowerCase().endsWith(paramValue.toLowerCase()))
                        finalList.add(o);

                }
            }
        }
        return finalList;
    }

    /*
     * evaluates the value of the Items List based on the variable name
     * @param itemsName
     * @return
     */
    @SuppressWarnings("unchecked")
    protected List<Object> evalItemsList(String itemsName) {
        if (StringUtility.isEL(itemsName))
            return (List<Object>) ELUtility.evaluateEL(itemsName, page);
        else
            return (List<Object>) PropertyAccessor.getProperty(this.page, itemsName);
    }
}
