/**
 *
 */
package eg.java.net.web.jspx.ui.controls.html.elements.inputs;

import eg.java.net.web.jspx.engine.error.JspxException;
import eg.java.net.web.jspx.engine.util.StringUtility;
import eg.java.net.web.jspx.ui.controls.WebControl;
import eg.java.net.web.jspx.ui.controls.html.elements.Input;
import eg.java.net.web.jspx.ui.pages.Page;
import org.kxml.parser.XmlParser;

/**
 * factory for the Input elements.
 *
 * @author amr.eladawy
 *
 */
public class InputFactory extends Input {

    /**
     *
     */
    private static final long serialVersionUID = 7666688087871519007L;

    public InputFactory() {
    }

    /**
     * Gets the Input control that is corresponding to the type of this input
     * @param _type
     * @param input
     * @return
     */
    public static Input getConcreteInput(String _type) {
        Input input = null;
        if (_type.equalsIgnoreCase(Input.Button))
            input = new Button();
        else if (_type.equalsIgnoreCase(Input.Submit))
            input = new Submit();
        else if (_type.equalsIgnoreCase(Input.HiddenField))
            input = new Hidden();
        else if (_type.equalsIgnoreCase(Input.TextBox))
            input = new TextBox();
        else if (_type.equalsIgnoreCase(Input.Password))
            input = new Password();
        else if (_type.equalsIgnoreCase(Input.CheckBox))
            input = new CheckBox();
        else if (_type.equalsIgnoreCase(Input.Radio))
            input = new RadioButton();
        else if (_type.equalsIgnoreCase(Input.Reset))
            input = new ResetButton();
        else if (_type.equalsIgnoreCase(Input.Image))
            input = new ImageButton();
        else if (_type.equalsIgnoreCase(Input.File))
            input = new FileUpload();
        if (input == null)
            throw new JspxException("The input type [" + _type + "] is not supported");
        return input;
    }

    public WebControl deSerialize(XmlParser parser, Page page, WebControl parent) throws Exception {
        parseStartTag(parser);
        parseEndTage(parser);

        String _type = getType();
        if (StringUtility.isNullOrEmpty(_type))
            throw new JspxException("The input element [" + this + "] has no type attribute");
        Input input = null;
        input = getConcreteInput(_type);

        setPage(page);
        setParent(parent);
        return input.merge(this);
    }
}
