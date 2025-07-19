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
public interface InternalValueHolder
{
	public static String nameSplitter = "_0_";

	public static String nameSplitterInsert = "I_";
	public static String nameSplitterUpdate = "U_";

	public void setInternalInputValues(Hashtable<String, String> values);
}