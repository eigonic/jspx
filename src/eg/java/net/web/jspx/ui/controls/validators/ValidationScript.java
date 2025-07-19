/**
 *
 */
package eg.java.net.web.jspx.ui.controls.validators;

import eg.java.net.web.jspx.engine.util.StringUtility;
import eg.java.net.web.jspx.engine.util.ui.RenderPrinter;
import eg.java.net.web.jspx.ui.controls.html.w3.Script;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Script Node for the validation.
 *
 * @author amr.eladawy
 *
 */
public class ValidationScript extends Script {

    /**
     *
     */
    private static final long serialVersionUID = -6533160005249146341L;
    /**
     * the controls to be validated String controlId, Validation group.
     */
    protected HashMap<String, ValidationGroup> controlGroups = new HashMap<String, ValidationGroup>();

    /**
     * Overridden to inject the controls list first.
     */
    @Override
    public void render(RenderPrinter outputStream) throws Exception {
        if (!controls.isEmpty()) {
            Script script = new Script();
            // script.setSrc(ResourceHandler.ResourcePrefix + ResourceUtility.ValidationScript);
            script.setPage(page);
            script.render(outputStream);
            // add the controls and the validation script
            setScriptCode(getControlsString()
                    /** + ResourceUtility.getResource(ResourceUtility.ValidationScript) **/
            );
            super.render(outputStream);
        }

    }

    /**
     * adds a control to the controls to be validated.
     *
     * @param controlId
     * @param group
     * @param validator
     *            TODO
     */
    public void addControlToValidate(String controlId, String group, String validationType, Validator validator) {
        ValidationGroup validationGroup = controlGroups.get(group);
        if (validationGroup == null) {
            validationGroup = new ValidationGroup();
            controlGroups.put(group, validationGroup);
        }
        validationGroup.controlIds.add("'".concat(controlId).concat("'"));
        validationGroup.types.add("'".concat(validator.getValidatorString()).concat("'"));
    }

    /**
     * gets the whole controls Script.
     *
     * @return
     */
    protected String getControlsString() {
        StringBuffer buffer = new StringBuffer();
        for (Iterator<String> i = controlGroups.keySet().iterator(); i.hasNext(); ) {
            String group = i.next();
            buffer.append(getGroupString(group));
            buffer.append("\n");
        }
        return buffer.toString();
    }

    /**
     * gets the script String for certain group.
     *
     * @param groupName
     * @return
     */
    protected String getGroupString(String groupName) {
        // [Oct 11, 2013 3:45:36 PM] [Amr.ElAdawy] [here we might want to check if this is an ajax call and the multipart, then
        // modify the parent frame items.]
        String declaration = "var ";
        String access = "";
        if (page.isAjaxPostBack && page.isMultiPartForm) {
            declaration = "window.parent.";
            access = "window.parent.";
        }
        String buffer = declaration + "controls_" + groupName + "= new Array (" +
                StringUtility.join(controlGroups.get(groupName).controlIds, ",") +
                ");\n " + declaration + "types_" + groupName + "= new Array (" +
                StringUtility.join(controlGroups.get(groupName).types, ",") +
                ");\n " + declaration + groupName + "= new Array (" + access + "controls_" + groupName +
                "," + access + "types_" + groupName + ");";

        return buffer;
    }

    private static class ValidationGroup {
        List<String> controlIds = new ArrayList<String>();

        List<String> types = new ArrayList<String>();

    }
}
