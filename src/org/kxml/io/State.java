package org.kxml.io;

import org.kxml.PrefixMap;

public class State
{
	public State prev;

	public PrefixMap prefixMap;

	public String namespace;

	public String name;

	public String tag; // for auto-endtag writing

	public State(State prev, PrefixMap prefixMap,
	// String namespace, String name,
			String tag)
	{

		this.prev = prev;
		this.prefixMap = prefixMap;
        this.tag = tag;
	}
}
