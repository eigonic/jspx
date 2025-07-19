/**
 *
 */
package eg.java.net.web.jspx.ui.controls.html.elements.dataitem.action;

import eg.java.net.web.jspx.engine.parser.TagFactory;
import eg.java.net.web.jspx.engine.util.ui.Render;
import eg.java.net.web.jspx.ui.controls.WebControl;
import eg.java.net.web.jspx.ui.controls.html.elements.Input;
import eg.java.net.web.jspx.ui.controls.html.elements.LinkCommand;
import eg.java.net.web.jspx.ui.controls.html.elements.dataitem.Table;
import eg.java.net.web.jspx.ui.controls.html.elements.hidden.HiddenGenericWebControl;
import eg.java.net.web.jspx.ui.controls.html.elements.inputs.Button;
import eg.java.net.web.jspx.ui.controls.html.elements.inputs.ImageButton;
import eg.java.net.web.jspx.ui.controls.html.elements.inputs.Submit;
import eg.java.net.web.jspx.ui.controls.html.elements.markers.IAjaxSubmitter;
import eg.java.net.web.jspx.ui.controls.html.elements.markers.ISubmitter;
import eg.java.net.web.jspx.ui.pages.Page;

/**
 * Template for Action panel in Table control.
 * @author amr.eladawy
 *
 */
public class ActionTempate extends HiddenGenericWebControl {

    /**
     *
     */
    private static final long serialVersionUID = 1595470828052826587L;

    public ActionTempate() {
        super(TagFactory.ActionTempalte);
    }

    public ActionTempate(Page page) {
        super(TagFactory.ActionTempalte, page);
    }

    /**
     * overrides the action of the child control of save/update/cancel template
     * @param child
     * @param parentTable
     * @param mySubmitter
     * @param myAjaxSubmitter
     */
    public static void overrideAction(WebControl child, String eventName, Table parentTable, ISubmitter mySubmitter, IAjaxSubmitter myAjaxSubmitter) {
        boolean isLink = child instanceof LinkCommand, isInput = child instanceof Button || child instanceof ImageButton || child instanceof Submit;
        String confirmation = null, onclick = null;
        boolean postNormal = false;
        if (isLink) {
            confirmation = ((LinkCommand) child).getConfirmation();
            onclick = ((LinkCommand) child).getOnClick();
            postNormal = ((LinkCommand) child).getPostNormal();
        } else if (isInput) {
            confirmation = ((Input) child).getConfirmation();
            onclick = ((Input) child).getOnClientClick();
            postNormal = ((LinkCommand) child).getPostNormal();
        }

        // TODO now render the submit controls (Save, Update)
        String action = Render.composeAction(parentTable.getId(), eventName, "", confirmation, true, onclick, postNormal, isLink, mySubmitter,
                myAjaxSubmitter);
        if (isLink)
            child.setAttributeValue("href", action);
        else if (isInput)
            child.setAttributeValue("onclick", action);
    }
}
