package eg.java.net.web.jspx.ui.controls.html.elements.markers;

import eg.java.net.web.jspx.engine.util.ui.RenderPrinter;
import eg.java.net.web.jspx.ui.controls.attrbs.JspxAttribute;

/**
 * marker for all Ajax Submittter Controls. initially AjaxPanel
 *
 * @author amr.eladawy
 */
public interface IAjaxSubmitter {

    @JspxAttribute
    String action = "action";

    /**
     * Only controls implementing this interface should expose their renderChildren method, in order to render the content only
     * not the control itself again.
     *
     * @param outputStream
     * @throws Exception
     */
    void renderChildren(RenderPrinter outputStream) throws Exception;

    /**
     * returns a javascript code to be executed when this control is rendered after ajax call back.
     *
     * @return
     * @author Amr.ElAdawy May 13, 2012 12:57:52 PM
     */
    String getOnRender();

    /**
     * sets a javascript code to be executed when this control is rendered after ajax call back.
     *
     * @return
     * @author Amr.ElAdawy May 13, 2012 12:57:52 PM
     */
    void setOnRender(String script);

    /**
     * gets the ID to be sent as AjaxPanel header in the response.
     *
     * @return
     */
    String getAjaxId();

    /**
     * gets the URL that this control will post on.
     *
     * @return
     * @Author Amr.ElAdawy Nov 2, 2013 9:59:05 AM
     */
    String getAction();
}
