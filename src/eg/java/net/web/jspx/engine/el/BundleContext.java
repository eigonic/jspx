package eg.java.net.web.jspx.engine.el;

import java.util.Properties;

import org.apache.commons.jexl2.JexlContext;

/**
 * JEXL context implementation for JSPX resource Bundles. 
 * @author Amr.ElAdawy
 *
 */
public class BundleContext implements JexlContext
{

	Properties properties;

	public BundleContext(Properties properties)
	{
		this.properties = properties;
	}

	public Object get(String name)
	{
		return properties.get(name);
	}

	public void set(String name, Object value)
	{
		properties.setProperty(name, value == null ? null : value.toString());
	}

	public boolean has(String name)
	{
		return properties.containsKey(name);
	}

}
