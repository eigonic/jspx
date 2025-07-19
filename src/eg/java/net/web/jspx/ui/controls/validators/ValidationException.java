/**
 *
 */
package eg.java.net.web.jspx.ui.controls.validators;

/**
 * When server side validation fails , this exception is thrown.
 *
 * @author Amr.ElAdawy
 *
 */
public class ValidationException extends Exception {

    private static final long serialVersionUID = -1720999710816845279L;

    public ValidationException() {
        super();
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(Throwable cause) {
        super(cause);
    }

}
