package org.kxml.parser;

import java.util.List;

import org.kxml.PrefixMap;
import org.kxml.Xml;

import eg.java.net.web.jspx.ui.controls.attrbs.Attribute;

/** A class for events indicating the start of a new element */

public class StartTag extends Tag
{

	List<Attribute> attributes;

	boolean degenerated;

	PrefixMap prefixMap;

	/**
	 * creates a new StartTag. The attributes are not copied and may be reused
	 * in e.g. the DOM. So DO NOT CHANGE the attribute vector after handing
	 * over, the effects are undefined
	 */

	public StartTag(StartTag parent, String namespace, String name, List<Attribute> attributes, boolean degenerated, boolean processNamespaces)
	{

		super(Xml.START_TAG, parent, namespace, name);

		this.attributes = (attributes == null || attributes.isEmpty()) ? null : attributes;

		this.degenerated = degenerated;

		prefixMap = parent == null ? PrefixMap.DEFAULT : parent.prefixMap;

		if (!processNamespaces)
			return;

		boolean any = false;

		for (int i = getAttributeCount() - 1; i >= 0; i--)
		{
			Attribute attr = attributes.get(i);
			String attrName = attr.getKey();
			int cut = attrName.indexOf(':');
			String prefix;

			if (cut != -1)
			{
				prefix = attrName.substring(0, cut);
				attrName = attrName.substring(cut + 1);
			}
			else if (attrName.equals("xmlns"))
			{
				prefix = attrName;
				attrName = "";
			}
			else
				continue;

			if (!prefix.equals("xmlns"))
			{
				if (!prefix.equals("xml"))
					any = true;
			}
			else
			{
				prefixMap = new PrefixMap(prefixMap, attrName, attr.getValue());

				// System.out.println (prefixMap);
				attributes.remove(i);
			}
		}

		int len = getAttributeCount();

		if (any)
		{
			for (int i = 0; i < len; i++)
			{
				Attribute attr = attributes.get(i);
				String attrName = attr.getKey();
				int cut = attrName.indexOf(':');

				if (cut == 0)
					throw new RuntimeException("illegal attribute name: " + attrName + " at " + this);

				else if (cut != -1)
				{
					String attrPrefix = attrName.substring(0, cut);
					if (!attrPrefix.equals("xml"))
					{
						attrName = attrName.substring(cut + 1);

						String attrNs = prefixMap.getNamespace(attrPrefix);

						if (attrNs == null)
							throw new RuntimeException("Undefined Prefix: " + attrPrefix + " in " + this);

						attributes.set(i, new Attribute(attrNs, attrName, attr.getValue()));
					}
				}
			}
		}

		int cut = name.indexOf(':');

		String prefix;
		if (cut == -1)
			prefix = "";
		else if (cut == 0)
			throw new RuntimeException("illegal tag name: " + name + " at " + this);
		else
		{
			prefix = name.substring(0, cut);
			this.name = name.substring(cut + 1);
		}

		this.namespace = prefixMap.getNamespace(prefix);

		if (this.namespace == null)
		{
			if (!prefix.isEmpty())
				throw new RuntimeException("undefined prefix: " + prefix + " in " + prefixMap + " at " + this);
			this.namespace = Xml.NO_NAMESPACE;
		}
	}

	/** returns the attribute vector. May return null for no attributes. */

	public List<Attribute> getAttributes()
	{
		return attributes;
	}

	public boolean getDegenerated()
	{
		return degenerated;
	}

	public PrefixMap getPrefixMap()
	{
		return prefixMap;
	}

	/**
	 * Simplified (!) toString method for debugging purposes only. In order to
	 * actually write valid XML, please use a XmlWriter.
	 */

	public String toString()
	{
		return "StartTag <" + name + "> line: " + lineNumber + " attr: " + attributes;
	}

	public void setPrefixMap(PrefixMap map)
	{
		this.prefixMap = map;
	}

}
