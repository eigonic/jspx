package eg.java.net.web.jspx.engine.el;

import eg.java.net.web.jspx.engine.data.DataField;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Set;

/**
 * JEXL Map  implementation for DataFields.
 *
 * @author Amr.ElAdawy
 */
public class DataFieldMap extends HashMap<String, Object> {
    private static final long serialVersionUID = 6069785239903489873L;
    private final Hashtable<String, DataField> dataFields;

    public DataFieldMap(Hashtable<String, DataField> dataFields) {
        this.dataFields = dataFields;
    }

    public Object get(Object name) {
        if (name != null) {
            DataField dataField = dataFields.get(((String) name).toLowerCase());
            if (dataField != null)
                return dataField.getValue();
        }
        return null;
    }

    public void set(Object name, Object value) {
        throw new RuntimeException("Cannot set DataFieldMap with key[" + name + "] from JEXL");
    }

    public boolean has(String name) {
        return dataFields.containsKey(name);
    }

    @Override
    public Set<java.util.Map.Entry<String, Object>> entrySet() {
        return null;
    }

}
