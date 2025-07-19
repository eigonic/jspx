package eg.java.net.web.jspx.engine.el;

import javax.servlet.http.HttpSession;
import java.util.HashMap;

/**
 * JEXL context implementation for HTTP Session.
 *
 * @author Amr.ElAdawy
 */
public class HttpSessionMap extends HashMap<String, Object> {
    private static final long serialVersionUID = -5137105920307285493L;
    private final HttpSession session;

    public HttpSessionMap(HttpSession session) {
        this.session = session;
    }

    public Object get(Object name) {
        return session.getAttribute((String) name);
    }

    public void set(String name, Object value) {
        throw new RuntimeException("Cannot set session attribute [" + name + "] from JEXL");
    }

    public boolean has(String name) {
        return session.getAttribute(name) != null;
    }
}
