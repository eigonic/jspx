package eg.java.net.web.jspx.engine.util.bean;

import eg.java.net.web.jspx.engine.RequestHandler;
import eg.java.net.web.jspx.engine.data.DataField;
import eg.java.net.web.jspx.engine.util.ELUtility;
import eg.java.net.web.jspx.engine.util.PropertyAccessor;
import eg.java.net.web.jspx.engine.util.StringUtility;
import eg.java.net.web.jspx.ui.pages.Page;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;

public class RepeaterUtility {

    public static String evaluate(String el, Page page) {
        return evaluate(el, page, null);
    }

    public static String evaluate(String el, Page page, String dateformat) {
        Object propertyValue = evaluateObject(el, page);
        if (propertyValue != null) {
            if (propertyValue instanceof Date || propertyValue instanceof Timestamp || propertyValue instanceof java.sql.Date) {
                if (StringUtility.isNullOrEmpty(dateformat))
                    dateformat = "dd-MM-yyyy HH:mm:ss";
                return new SimpleDateFormat(dateformat).format((Date) propertyValue);
            } else
                return String.valueOf(propertyValue);
        }
        return null;
    }

    /**
     * gets the Object instance for a member in a Repeater Item.
     *
     * @param el
     * @param page
     * @return
     */
    public static Object evaluateObject(String el, Page page) {
        el = el.trim();
        if (StringUtility.isEL(el) && page != null && page.session != null) {
            // [Jul 16, 2013 12:40:19 PM] [Amr.ElAdawy] [use JEXL]
            if (RequestHandler.USE_JEXL) {
                return ELUtility.UJEXL.parse(el).evaluate(page.getPageJEXLContext());
            } else {
                // [Jul 16, 2013 1:06:43 PM] [Amr.ElAdawy] [get the repeater object directly from the page]
                Object repeaterItem = page.request.getAttribute(Page.RepeaterNamePrefix + JspxBeanUtility.getBeanName(el));
                if (repeaterItem != null) {
                    int index = el.indexOf('.');
                    if (index < 0)
                        return repeaterItem;
                    else {
                        String propertyName = el.substring(index + 1, el.length() - 1);

                        if (repeaterItem instanceof Hashtable<?, ?>) {
                            DataField dataField = ((DataField) ((Hashtable<?, ?>) repeaterItem).get(propertyName.toLowerCase()));
                            if (dataField != null)
                                return dataField.getValue();
                        } else
                            return PropertyAccessor.getProperty(repeaterItem, propertyName);
                    }
                }
            }
        }
        return null;
    }
}
