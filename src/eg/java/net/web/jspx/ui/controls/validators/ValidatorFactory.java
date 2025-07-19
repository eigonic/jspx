/**
 *
 */
package eg.java.net.web.jspx.ui.controls.validators;

import eg.java.net.web.jspx.engine.util.StringUtility;
import eg.java.net.web.jspx.ui.controls.WebControl;
import eg.java.net.web.jspx.ui.controls.attrbs.Attribute;
import eg.java.net.web.jspx.ui.pages.Page;
import org.kxml.io.ParseException;
import org.kxml.parser.XmlParser;

/**
 * factory for the validators elements.
 *
 * @author amr.eladawy
 *
 */
public class ValidatorFactory extends Validator {

    private static final long serialVersionUID = -8629841836725073731L;

    public ValidatorFactory() {
        super("");
    }

    public WebControl deSerialize(XmlParser parser, Page page, WebControl parent) throws Exception {
        parseStartTag(parser);
        int line = parser.getLineNumber();
        int column = parser.getColumnNumber();
        String tagPlace = line + ":" + column;
        parseEndTage(parser);

        String _type = getType();
        Validator validator = constructValidator(_type);
        if (validator == null)
            throw new ParseException("Validator [" + getId() + "] at [" + tagPlace + "]has the invalid type = " + _type, null, line, column);

        this.page = page;
        setParent(parent);
        validator.merge(this);

        String controlToValidate = getAttributeValue(Validator.ControlToValidateKey);
        if (StringUtility.isNullOrEmpty(controlToValidate))
            throw new ParseException("Validator [" + getId() + "] at [" + tagPlace + "]should have a control to validate", null, line, column);
        validator.setControlToValidate(controlToValidate);

        return validator;
    }

    /**
     * creates runtime validator for controls require validations
     * [1 Sep 2015 14:25:09]
     * @author aeladawy
     * @param parser
     * @param page
     * @param parent
     * @param line
     * @param column
     * @return
     * @throws Exception
     */
    public Validator createRuntimeValidator(WebControl controlToValidate, Page page, WebControl parent, int line, int column) throws Exception {
        String _type = controlToValidate.getAttributeValue("validate");
        Validator validator = constructValidator(_type);
        if (validator == null)
            throw new ParseException("Control  [" + controlToValidate.getId() + "]  has the invalid validation type = " + _type, null, line, column);

        setParent(parent);
        validator.merge(this);
        validator.setPage(page);
        for (Attribute a : controlToValidate.getAttributes().values()) {
            validator.getAttributes().put(a.getKey().trim(), new Attribute(a.getKey(), a.getValue()));
        }
        validator.setControlToValidate(controlToValidate.getId());
        validator.setId(controlToValidate.getId() + "_V");

        return validator;
    }

    /**
     * [2 Sep 2015 09:13:20]
     * @author aeladawy
     * @param _type
     * @return
     */
    protected Validator constructValidator(String _type) {
        Validator validator = null;

        if (_type.equalsIgnoreCase(Validator.Required))
            validator = new RequiredFieldValidator();
        else if (_type.equalsIgnoreCase(Validator.Email))
            validator = new EmailValidator();
        else if (_type.equalsIgnoreCase(Validator.Length))
            validator = new LengthValidator();
        else if (_type.equalsIgnoreCase(Validator.MSISDN))
            validator = new MSISDN_Validator();
        else if (_type.equalsIgnoreCase(Validator.Numeric))
            validator = new NumericValidator();
        else if (_type.equalsIgnoreCase(Validator.Range))
            validator = new RangeValidator();
        else if (_type.equalsIgnoreCase(Validator.Custom))
            validator = new CustomValidator();
        else
            return null;
        return validator;
    }

    @Override
    protected void initStyles() {
    }

    /*
     * (non-Javadoc)
     * @see eg.java.net.web.jspx.ui.controls.validators.Validator#validate(eg.java.net.web.jspx.ui.controls.WebControl)
     */
    @Override
    public void validate() throws ValidationException {
        throw new RuntimeException("Not Supported");
    }
}
