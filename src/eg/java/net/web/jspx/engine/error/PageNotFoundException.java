package eg.java.net.web.jspx.engine.error;

@SuppressWarnings("serial")
public class PageNotFoundException extends Exception {

    public PageNotFoundException() {
        super();
    }

    public PageNotFoundException(String message) {
        super(message);
    }

}
