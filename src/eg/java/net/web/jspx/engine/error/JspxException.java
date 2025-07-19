/**
 *
 */
package eg.java.net.web.jspx.engine.error;

/**
 * Jspx Exception thrown based on violation of the framework rules and resitrictions. 
 * @author amr.eladawy
 *
 */
public class JspxException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = -8308740827970495021L;

    public JspxException() {

    }

    /**
     * @param message
     */
    public JspxException(String message) {
        super(message);

    }

    /**
     * @param cause
     */
    public JspxException(Throwable cause) {
        super(cause);

    }

    /**
     * @param message
     * @param cause
     */
    public JspxException(String message, Throwable cause) {
        super(message, cause);

    }

}
