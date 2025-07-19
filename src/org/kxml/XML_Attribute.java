package org.kxml;

/**
 * Attribute class, used by both kDom and the pullparser. The instances of this
 * class are immutable. This restriction allows manipulation aware element
 * implementations without needing to care about hidden changes in attributes.
 */

public class XML_Attribute
{

	protected String namespace;

	protected String key;

	protected String value;

	/**
	 * Creates a new Attribute instance with the given name and value. The
	 * namespace is set to "".
	 */

	public XML_Attribute(String key, String value)
	{
		this.namespace = "";
		this.key = key;
		this.value = value;
	}

	/**
	 * creates a new Attribute with the given namespace, name and value
	 */

	public XML_Attribute(String namespace, String key, String value)
	{
		this.namespace = namespace == null ? "" : namespace;
		this.key = key;
		this.value = value;
	}

	/** returns the string value of the attribute */

	public String getValue()
	{
		return value;
	}

	/** returns the name of the attribute */

	public String getKey()
	{
		return key;
	}

	/** returns the namespace of the attribute */

	public String getNamespace()
	{
		return namespace;
	}

	public String toString()
	{
		return (!namespace.isEmpty() ? ("{" + namespace + "}" + key) : key) + "=\"" + value + "\"";
	}
}
