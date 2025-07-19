/**
 *
 */
package eg.java.net.web.jspx.engine.error;

/**
 * @author amr.eladawy
 *
 */
public class PageNotAccessibleException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 3179061752618256879L;

    /**
     *
     */
    public PageNotAccessibleException() {

    }

    /**
     * @param message
     */
    public PageNotAccessibleException(String message) {
        super(message);

    }

    /**
     * @param cause
     */
    public PageNotAccessibleException(Throwable cause) {
        super(cause);

    }

    /**
     * @param message
     * @param cause
     */
    public PageNotAccessibleException(String message, Throwable cause) {
        super(message, cause);

    }

}
