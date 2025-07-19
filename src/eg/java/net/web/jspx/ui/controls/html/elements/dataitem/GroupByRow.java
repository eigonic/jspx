package eg.java.net.web.jspx.ui.controls.html.elements.dataitem;

import java.util.Hashtable;
import java.util.Map;

public class GroupByRow<T, J> extends Hashtable<T, J>
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public GroupByRow()
	{
		super();

	}

	public GroupByRow(int initialCapacity, float loadFactor)
	{
		super(initialCapacity, loadFactor);

	}

	public GroupByRow(int initialCapacity)
	{
		super(initialCapacity);

	}

	public GroupByRow(Map<? extends T, ? extends J> m)
	{
		super(m);

	}

}
