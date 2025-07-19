/**
 *
 */
package eg.java.net.web.jspx.ui.controls.html.elements.markers;

import java.util.Hashtable;

/**
 * All controls that has internal inputs must implement this interface to receive the value of these data. on post back.
 *
 * @author amr.eladawy
 *
 */
public interface InternalValueHolder {
    String nameSplitter = "_0_";

    String nameSplitterInsert = "I_";
    String nameSplitterUpdate = "U_";

    void setInternalInputValues(Hashtable<String, String> values);
}