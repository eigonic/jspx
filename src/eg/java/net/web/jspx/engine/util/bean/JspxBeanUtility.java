package eg.java.net.web.jspx.engine.util.bean;

import eg.java.net.web.jspx.engine.RequestHandler;
import eg.java.net.web.jspx.engine.annotation.JspxBeanValue;
import eg.java.net.web.jspx.engine.util.ELUtility;
import eg.java.net.web.jspx.engine.util.PropertyAccessor;
import eg.java.net.web.jspx.engine.util.StringUtility;
import eg.java.net.web.jspx.ui.pages.Page;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class JspxBeanUtility {

    public static String evaluate(String el, Page page) {
        return evaluate(el, page, null);
    }

    public static String evaluate(String el, Page page, String dateformat) {
        Object propertyValue = evaluateObject(el, page);
        if (propertyValue != null) {
            if (propertyValue instanceof Date || propertyValue instanceof Timestamp || propertyValue instanceof java.sql.Date) {
                if (StringUtility.isNullOrEmpty(dateformat))
                    dateformat = page.getJspxBeanByName(JspxBeanUtility.getBeanName(el)).getJspxBean().dateformat();
                return new SimpleDateFormat(dateformat).format((Date) propertyValue);
            } else
                return String.valueOf(propertyValue);
        }
        return null;
    }

    /**
     * gets an object instance for a property in a jspx bean.
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
                JspxBeanValue jspxBeanValue = page.getJspxBeanByName(JspxBeanUtility.getBeanName(el));
                if (jspxBeanValue != null) {
                    int index = el.indexOf('.');
                    if (index < 0)
                        return jspxBeanValue.getValue();
                    else {
                        if (jspxBeanValue.getValue() == null)
                            jspxBeanValue.setValue(page.getJspxBeanValue(jspxBeanValue.getJspxBean().name()));
                        String propertyName = el.substring(index + 1, el.length() - 1);
                        return PropertyAccessor.getProperty(jspxBeanValue.getValue(), propertyName);
                    }
                }
            }
        }
        return null;
    }

    /**
     * extracts the bean name out of EL
     *
     * @param el
     * @return
     */
    public static String getBeanName(String el) {
        el = el.trim();
        int index = el.indexOf('.');

        if (index < 0)
            return el.substring(2, el.length() - 1);
        else
            return el.substring(2, index);

    }

}
