/**
 *
 */
package eg.java.net.web.jspx.engine.data;

import java.io.Serializable;

/**
 * Database field , when retrieved from DataBase.
 *
 * @author amr.eladawy
 *
 */
public class DataField implements Serializable {
    private static final long serialVersionUID = -7073038455388425082L;

    private String originalName;
    private String name;

    private Object value;

    private String type;

    private int index;

    public DataField(String name, Object value) {
        super();
        this.name = name;
        this.value = value;
    }

    public DataField(String name, Object value, String type, int index, String originalName) {
        this(name, value);
        this.type = type;
        this.index = index;
        this.originalName = originalName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value == null ? "" : value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * @return the originalName
     */
    public String getOriginalName() {
        return originalName;
    }

    /**
     * @param originalName
     *            the originalName to set
     */
    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

}
