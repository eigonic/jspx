/**
 *
 */
package eg.java.net.web.jspx.ui.controls.validators;

import eg.java.net.web.jspx.engine.util.StringUtility;
import eg.java.net.web.jspx.ui.controls.html.elements.markers.ValueHolder;

/**
 * @author amr.eladawy
 *
 */
public class LengthValidator extends Validator {

    /**
     *
     */
    private static final long serialVersionUID = -8969440410780645127L;

    protected String maxLengthKey = "maxlength";

    protected String minLengthKey = "minlength";

    /**
     * @param validationType
     */
    public LengthValidator() {
        super("3");
    }

    public int getMaxLength() {
        return getAttributeIntValue(maxLengthKey);
    }

    public void setMaxLength(int maxLength) {
        setAttributeIntValue(minLengthKey, maxLength);
    }

    public int getMinLength() {
        return getAttributeIntValue(minLengthKey);
    }

    public void setMinLength(int minLength) {
        setAttributeIntValue(minLengthKey, minLength);
    }

    /*
     * (non-Javadoc)
     * @see eg.java.net.web.jspx.ui.controls.validators.Validator#validate(eg.java.net.web.jspx.ui.controls.WebControl)
     */
    @Override
    public void validate() throws ValidationException {
        getControlToValidate();
        String s = ((ValueHolder) controlToValidate).getValue();
        if (StringUtility.isNullOrEmpty(s))
            return;
        String data = s;
        int max = Integer.MAX_VALUE;
        if (!StringUtility.isNullOrEmpty(getAttributeValue(maxLengthKey)))
            max = getMaxLength();
        int min = Integer.MIN_VALUE;
        if (!StringUtility.isNullOrEmpty(getAttributeValue(minLengthKey)))
            min = getMinLength();
        if (data == null || data.length() < min || data.length() > max) {
            invalid = true;
            throw new ValidationException("Control [" + controlToValidate.getId() + "] has data [" + data + "] out of length range[" + getMinLength()
                    + "," + getMaxLength() + "] ");
        }
    }

    public String getValidatorString() {
        int max = Integer.MAX_VALUE;
        if (!StringUtility.isNullOrEmpty(getAttributeValue(maxLengthKey)))
            max = getMaxLength();
        int min = Integer.MIN_VALUE;
        if (!StringUtility.isNullOrEmpty(getAttributeValue(minLengthKey)))
            min = getMinLength();
        return validationType + "," + min + "," + max;
    }
}
