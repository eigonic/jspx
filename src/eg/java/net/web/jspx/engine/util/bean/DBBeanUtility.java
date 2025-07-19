package eg.java.net.web.jspx.engine.util.bean;

import eg.java.net.web.jspx.engine.util.StringUtility;
import eg.java.net.web.jspx.ui.controls.html.elements.dataitem.DBBean;
import eg.java.net.web.jspx.ui.pages.Page;

public class DBBeanUtility {

    public static String evaluate(String el, Page page, String dateformat) {
        el = el.trim();
        if (StringUtility.isEL(el) && page != null && page.session != null) {
            int index = el.indexOf('.');
            if (index > 0) {
                String beanName = el.substring(2, index);
                Object beanObj = page.request.getAttribute(Page.DBBeanNamePrefix + beanName);
                String value = null;
                if (beanObj != null) {
                    DBBean bean = (DBBean) beanObj;
                    String propertyName = null;
                    String remaining = el.substring(index + 1, el.length() - 1);
                    if (remaining.contains("=")) {
                        propertyName = remaining.substring(0, remaining.indexOf('='));
                        String x = remaining.substring(remaining.indexOf('=') + 1);
                        String v = bean.getBeanAttributeValue(propertyName);
                        if (v == null)
                            return "false";
                        return "" + v.equals(x);
                    } else {
                        propertyName = remaining;
                        return bean.getBeanAttributeValue(propertyName);
                    }
                }
                return value;
            }
        }
        return null;
    }

    public static String evaluate(String el, Page page) {
        return evaluate(el, page, null);
    }
}
