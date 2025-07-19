package eg.java.net.web.jspx.ui.controls.html.elements.markers;

/**
 * Interface for controls that can hold Value attribute.
 * 
 * @author amr.eladawy
 * 
 */
public interface ValueHolder
{
	public String getValue();

	public void setValue(String valueString);

	public String getValueBinding();

	public void setValueBinding(String valueBinding);

}
