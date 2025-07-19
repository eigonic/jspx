/**
 *
 */
package eg.java.net.web.jspx.engine.util;

import java.util.HashMap;

/**
 * Carriage contains shared information for a page. live only on the request level.
 *
 * @author amr.eladawy
 *
 */
public class PageCarriage extends HashMap<String, Object> {
    public static final String CURRENT_INTERNAL_VALUE_HOLDER_ID = "CURRENT_INTERNAL_VALUE_HOLDER_ID";
    public static final String CURRENT_INTERNAL_VALUE_HOLDER = "CURRENT_INTERNAL_VALUE_HOLDER";
    public static final String ITEM_TEMPLATE_PARENT = "ITEM_TEMPLATE_PARENT";
    private static final long serialVersionUID = -9010578970004230638L;
    HashMap<String, Object> carriage = new HashMap<String, Object>();

    public Object get(String name) {
        return carriage.get(name);
    }

    public void set(String name, Object object) {
        carriage.put(name, object);
    }
}
