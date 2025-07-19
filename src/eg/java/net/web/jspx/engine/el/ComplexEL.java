/**
 * 
 */
package eg.java.net.web.jspx.engine.el;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Model for the Complex EL
 * <br />
 * it contains a string with place holders and list of EL to be injected in these place holders 
 * @author amr.eladawy
 *
 */
public class ComplexEL implements Serializable
{
	private static final long serialVersionUID = 7214159548351003005L;

	private String string = "";

	private List<String> values = new ArrayList<String>();

	/**
	 * gets the parameterized String 
	 * @return
	 */
	public String getString()
	{
		return string;
	}

	public void setString(String string)
	{
		this.string = string;
	}

	/**
	 * gets the list of EL expressions
	 * @return
	 */
	public List<String> getValues()
	{
		return values;
	}

	public void setValues(List<String> values)
	{
		this.values = values;
	}
}
