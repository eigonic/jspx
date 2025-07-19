/**
 * 
 */
package eg.java.net.web.jspx.engine.util;

import java.util.Comparator;

/**
 * A custom comparator class used to compare objects based on any property inside them. used by the ListTable to make sorting.
 * @author aeladawy 21 May 2015 12:18:46
 *
 */
public class JspxComparator implements Comparator<Object>
{
	String fieldName;
	int sortingDir = 1;;

	public JspxComparator(String fieldName, String sortingDir)
	{
		this.fieldName = fieldName;
		this.sortingDir = ("desc".equalsIgnoreCase(sortingDir) ? -1 : 1);
	}

	public int compare(Object o1, Object o2)
	{
		Object a, b;
		a = PropertyAccessor.getProperty(o1, fieldName);
		b = PropertyAccessor.getProperty(o2, fieldName);
		return (a == null ? (b == null ? 0 : -1) : (b == null ? 1 : doCompare(a, b))) * sortingDir;
	}

	protected int doCompare(Object a, Object b)
	{
		if (a instanceof Comparable && b instanceof Comparable)
			return ((Comparable<Object>) a).compareTo(b);
		return 0;
	}
}
