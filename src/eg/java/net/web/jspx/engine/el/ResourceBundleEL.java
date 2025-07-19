package eg.java.net.web.jspx.engine.el;

import eg.java.net.web.jspx.engine.util.StringUtility;
import eg.java.net.web.jspx.ui.controls.html.elements.ResourceBundle;
import eg.java.net.web.jspx.ui.pages.Page;

public class ResourceBundleEL
{

	private ResourceBundleEL()
	{
	}

	public static String evaluate(String el, Page page)
	{
		el = el.trim();
		if (StringUtility.isEL(el) && page != null)
		{
			int index = el.indexOf('.');
			ResourceBundle rb = page.getBundle(el.substring(2, index));
			String key = el.substring(index + 1, el.length() - 1);
			String value = key;
			if (rb != null)
				value = rb.getString(key);
			// [Jan 16, 2011 2:57:00 PM] [amr.eladawy] [here we only check that the String is null, to allow empty Strings in the properties file]
			if (value == null)
				value = key;
			return value;
		}
		else
			return el;
	}

	/**
	 * checks whether the given expression is a resource bundle expression.
	 * @param el
	 * @param page
	 * @return
	 */
	public static boolean inBundle(String el, Page page)
	{
		if (StringUtility.isEL(el) && page != null)
		{
			int index = el.indexOf('.');
			ResourceBundle rb;
			if (index < 0)
				rb = page.getBundle(el);
			else
				rb = page.getBundle(el.substring(2, index));
			if (rb != null)
				return true;
			else
				return false;
		}
		else
			return false;
	}

}
