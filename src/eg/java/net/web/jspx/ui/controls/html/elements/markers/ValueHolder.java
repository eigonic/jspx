package eg.java.net.web.jspx.ui.controls.html.elements.markers;

/**
 * Interface for controls that can hold Value attribute.
 *
 * @author amr.eladawy
 */
public interface ValueHolder {
    String getValue();

    void setValue(String valueString);

    String getValueBinding();

    void setValueBinding(String valueBinding);

}
