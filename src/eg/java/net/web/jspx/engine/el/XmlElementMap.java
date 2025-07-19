package eg.java.net.web.jspx.engine.el;

import java.util.HashMap;

/**
 * JEXL context implementation for XML Element. 
 * @author Amr.ElAdawy
 *
 */
public class XmlElementMap extends HashMap<String, Object>
{
	private static final long serialVersionUID = 6069785239903489873L;
	private HashMap<String, String> attributes = new HashMap<String, String>();
	private HashMap<String, XmlElementMap> elements = new HashMap<String, XmlElementMap>();
	private Object value;

	public Object get(Object name)
	{
		if ("attributes".equalsIgnoreCase((String) name))
			return attributes.get((String) name);
		else
			return elements.get(name);
	}

	public void set(String name, Object value)
	{
		if ("attributes".equalsIgnoreCase(name))
			attributes.put(name, (String) value);
		else
			elements.put(name, (XmlElementMap) value);
	}

	public boolean has(String name)
	{
		if ("attributes".equalsIgnoreCase(name))
			return attributes.containsKey(name);
		return elements.containsKey(name);
	}

	public HashMap<String, String> getAttributes()
	{
		return attributes;
	}

	public void setAttributes(HashMap<String, String> attributes)
	{
		this.attributes = attributes;
	}

	public HashMap<String, XmlElementMap> getElements()
	{
		return elements;
	}

	public void setElements(HashMap<String, XmlElementMap> elements)
	{
		this.elements = elements;
	}

	public Object getValue()
	{
		return elements.size() == 0 ? elements : value;
	}

	public void setValue(Object value)
	{
		if (value instanceof HashMap<?, ?>)
			this.elements = (HashMap<String, XmlElementMap>) value;
		else if (value instanceof String)
			this.value = value;
	}

}
