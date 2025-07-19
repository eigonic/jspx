package eg.java.net.web.jspx.engine.el;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

/**
 * JEXL context implementation for HTTP Request Attributes. 
 * @author Amr.ElAdawy
 *
 */
public class HttpRequestAttributeMap extends HashMap<String, Object>
{
	private static final long serialVersionUID = 6069785239903489873L;
	private HttpServletRequest request;

	public HttpRequestAttributeMap(HttpServletRequest request)
	{
		this.request = request;
	}

	public Object get(Object name)
	{
		return request.getAttribute((String) name);
	}

	public void set(String name, Object value)
	{
		throw new RuntimeException("Cannot set request attribute [" + name + "] from JEXL");
	}

	public boolean has(String name)
	{
		return request.getAttribute(name) != null;
	}

}
