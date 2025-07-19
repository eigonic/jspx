/**
 *
 */
package eg.java.net.web.jspx.engine.util;

import eg.java.net.web.jspx.engine.RequestHandler;
import eg.java.net.web.jspx.engine.data.DataField;
import eg.java.net.web.jspx.engine.el.DataFieldMap;
import eg.java.net.web.jspx.engine.el.ResourceBundleEL;
import eg.java.net.web.jspx.engine.util.bean.JspxBeanUtility;
import eg.java.net.web.jspx.engine.util.bean.RepeaterUtility;
import eg.java.net.web.jspx.ui.pages.Page;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.UnifiedJEXL;

import java.util.Hashtable;

/**
 * Common utility for EL evaluation.
 * it is 
 * @author amr.eladawy
// [Jul 15, 2013 2:23:57 PM] [Amr.ElAdawy] [Created to encapsulate all EL evaluation based on JEXL or JSPX EL engine.]
 */
public class ELUtility {

    private static final JexlEngine jexl = new JexlEngine();
    public static UnifiedJEXL UJEXL = new UnifiedJEXL(jexl);

    /**
     * evaluate EL and obtains object instance.
     * @param el
     * @param page
     * @return
     */
    public static Object evaluateEL(String el, Page page) {
        el = el.trim();
        Object object = null;
        if (RequestHandler.USE_JEXL) {
            object = UJEXL.parse(el).evaluate(page.getPageJEXLContext());
        } else {
            if (isPageBean(el))
                object = PropertyAccessor.getProperty(page, el.replace("${this.", "").replace("}", "").trim());
            else if (isSessionVariable(el))
                object = page.session.getAttribute(el.replace("${session.", "").replace("}", "").trim());
            else if (isRequestAttributeVariable(el))
                object = page.request.getAttribute(el.replace("${request.attributes.", "").replace("}", "").trim());
            else if (isRequestParameterVariable(el))
                object = page.request.getParameter(el.replace("${request.parameters.", "").replace("}", "").trim());
            else {
                if (ResourceBundleEL.inBundle(el, page))
                    object = ResourceBundleEL.evaluate(el, page);
                if (object == null)
                    object = JspxBeanUtility.evaluateObject(el, page);
                if (object == null)
                    object = RepeaterUtility.evaluateObject(el, page);
            }
        }
        return object;
    }

    private static boolean isPageBean(String el) {
        return el.startsWith("${this.");
    }

    private static boolean isSessionVariable(String el) {
        return el.startsWith("${session.");
    }

    private static boolean isRequestAttributeVariable(String el) {
        return el.startsWith("${request.attributes.");
    }

    private static boolean isRequestParameterVariable(String el) {
        return el.startsWith("${request.parameters.");
    }

    /**
     * adds the passed repeater object to the JEXL map of the page . if the repeater is database row (HashMap<String,DataField>)
     * then warp inside a DataFiledMap
     *
     * @param page
     * @param name
     *            the name of the repeater
     * @param repeater
     */
    @SuppressWarnings("unchecked")
    public static void addObjectToJexlMap(Page page, String name, Object repeater) {
        if (repeater instanceof Hashtable<?, ?>) {
            try {
                page.getPageJEXLContext().set(name, new DataFieldMap((Hashtable<String, DataField>) repeater));
                return;
            } catch (ClassCastException ee) {
            }
        }
        page.getPageJEXLContext().set(name, repeater);
    }
}
