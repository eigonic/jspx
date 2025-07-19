package eg.java.net.web.jspx.engine.el;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

/**
 * JEXL context implementation for HTTP Request Attributes.
 *
 * @author Amr.ElAdawy
 */
public class HttpRequestParameterMap extends HashMap<String, Object> {
    private static final long serialVersionUID = 6069785239903489873L;
    private final HttpServletRequest request;

    public HttpRequestParameterMap(HttpServletRequest request) {
        this.request = request;
    }

    public Object get(Object name) {
        return request.getParameter((String) name);
    }

    public void set(String name, Object value) {
        throw new RuntimeException("Cannot set request parameter [" + name + "] from JEXL");
    }

    public boolean has(String name) {
        return request.getParameter(name) != null;
    }

}
