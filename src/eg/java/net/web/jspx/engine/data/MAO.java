/**
 *
 */
package eg.java.net.web.jspx.engine.data;

import eg.java.net.web.jspx.engine.util.PropertyAccessor;
import eg.java.net.web.jspx.ui.controls.html.elements.markers.InternalValueHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

/**
 * @author ahmed.abdelaal
 *
 */
public class MAO {
    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(MAO.class);

    public static List<Hashtable<String, DataField>> search(List<Object> list, int start, int size) throws Exception {
        List<Hashtable<String, DataField>> rows = new ArrayList<Hashtable<String, DataField>>();
        Hashtable<String, DataField> cols = null;
        try {
            String fieldName;
            Field[] fields = null;
            if (!list.isEmpty()) {
                Class<?> aClass = list.get(0).getClass();
                fields = aClass.getDeclaredFields();
            }
            size = Math.min(list.size(), size);
            for (int j = start, counter = 0; j < list.size() && counter < size; j++, counter++) {
                cols = new Hashtable<String, DataField>();
                for (int i = 0; i < fields.length; i++) {
                    fieldName = fields[i].getName();
                    cols.put(fieldName, new DataField(fieldName, PropertyAccessor.getProperty(list.get(j), fieldName)));
                }
                rows.add(cols);
            }

        } catch (Exception e) {
            logger.error("Exception while retrieving List Object : " + e);
            throw e;
        }
        return rows;
    }

    /**
     *
     * @param list
     * @return List size
     * @throws Exception
     *             NullPointer Exception
     */
    public static int totalCount(List<?> list) throws Exception {
        return list.size();
    }

    public static void mapHashToObject(Hashtable<String, String> hashTable, Hashtable<String, Object> objectTable, Object o, boolean isEdit)
            throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        Set<String> keySet = hashTable.keySet();
        String delimeter = InternalValueHolder.nameSplitter
                + (isEdit ? InternalValueHolder.nameSplitterUpdate : InternalValueHolder.nameSplitterInsert);
        for (String key : keySet) {

            int sep = key.lastIndexOf(delimeter);
            String name = key.substring(sep + delimeter.length());

            if (objectTable.get(key) != null) {
                // name = name.substring(0, name.indexOf("."));
                PropertyAccessor.setProperty(o, name, objectTable.get(key));
            } else
                PropertyAccessor.setProperty(o, name, hashTable.get(key));
        }

    }

    public static Object getObjectInstance(List<Object> list, Hashtable<String, String> table, Hashtable<String, Object> objectTable, Object o)
            throws Exception {
        mapHashToObject(table, objectTable, o, true);
        return o;
    }

    public static Object getObjectInstance(List<Object> list, Hashtable<String, String> table, Hashtable<String, Object> objectTable,
                                           String objectClass) throws Exception {
        Object o = Class.forName(objectClass).newInstance();
        mapHashToObject(table, objectTable, o, false);
        return o;
    }

    public static void insertIntoList(List<Object> list, Hashtable<String, String> table, Hashtable<String, Object> objectTable, String objectClass)
            throws Exception {
        try {
            Object o = getObjectInstance(new ArrayList<>(), table, objectTable, objectClass);
            list.add(o);
        } catch (ClassNotFoundException e) {
            throw new ClassNotFoundException(e.getMessage() + " : Please make sure to use the fully qualified name for the class ");
        }
    }

    public static void editList(List<Object> list, Hashtable<String, String> table, Hashtable<String, Object> objectTable, String selectedIndex,
                                String objectClass) throws Exception {
        if (list != null && table != null && selectedIndex != null)
            mapHashToObject(table, objectTable, list.get(Integer.parseInt(selectedIndex)), true);
    }

}
