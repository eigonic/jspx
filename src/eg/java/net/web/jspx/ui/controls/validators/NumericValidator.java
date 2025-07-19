/**
 *
 */
package eg.java.net.web.jspx.ui.controls.validators;

import eg.java.net.web.jspx.engine.util.StringUtility;
import eg.java.net.web.jspx.engine.util.ui.RenderPrinter;
import eg.java.net.web.jspx.ui.controls.html.elements.markers.ValueHolder;

/**
 * @author amr.eladawy
 *
 */
public class NumericValidator extends Validator {

    /**
     *
     */
    private static final long serialVersionUID = -4171091948057013737L;

    /**
     * @param validationType
     */
    public NumericValidator() {
        super("6");
    }

    public String getDigits() {
        String digitsKey = "length";
        return getAttributeValue(digitsKey);
    }

    public String getMaxDigits() {
        String maxDigitsKey = "maxdigits";
        return getAttributeValue(maxDigitsKey);
    }

    public String getMinDigits() {
        String minDigitsKey = "mindigits";
        return getAttributeValue(minDigitsKey);
    }

    /*
     * (non-Javadoc)
     *
     * @see eg.java.net.web.jspx.ui.controls.validators.Validator#validate(eg.java .net.web.jspx.ui.controls.WebControl)
     */
    @Override
    public void validate() throws ValidationException {
        getControlToValidate();
        String data = ((ValueHolder) controlToValidate).getValue();
        try {
            // Basma : I commented this, because the field may numeric but not required
            if (StringUtility.isNullOrEmpty(data))
                return;
            char c;
            for (int i = 0; i < data.length(); i++) {
                c = data.charAt(i);
                if (c == '.' || c == '-')
                    continue;
                Integer.parseInt(String.valueOf(c));
            }

            if (!StringUtility.isNullOrEmpty(getDigits())) {
                if (data.length() != Integer.parseInt(getDigits()))
                    throw new Exception("");
            }
            if (!StringUtility.isNullOrEmpty(getMaxDigits())) {
                if (data.length() > Integer.parseInt(getMaxDigits()))
                    throw new Exception("");
            }
            if (!StringUtility.isNullOrEmpty(getMinDigits())) {
                if (data.length() < Integer.parseInt(getMinDigits()))
                    throw new Exception("");
            }

        } catch (Exception e) {
            invalid = true;
            throw new ValidationException("control [" + controlToValidate.getId() + "] has invalid number [" + data + "]");
        }
    }

    public String getValidatorString() {
        return validationType + "," + getDigits() + "," + getMinDigits() + "," + getMaxDigits();
    }

    /**
     * overridded to add mask on the text field
     */
    @Override
    public void render(RenderPrinter outputStream) throws Exception {
        super.render(outputStream);
        // [Mar 19, 2012 12:16:34 PM] [amr.eladawy] [add Mask numeric on the field.]
        if (!StringUtility.isNullOrEmpty(getMaxDigits())) {
            int max = Integer.parseInt(getMaxDigits());
            if (max > 0) {

                String document = "";
                if (page != null && page.isAjaxPostBack && page.isMultiPartForm)
                    document = ",window.parent.document";
                StringBuffer sb = new StringBuffer("$('#").append(getControlToValidate().getId()).append("'").append(document).append(").mask('");

                for (int i = 0; i < max; i++)
                    sb.append("9");
                sb.append("',{placeholder:\"\"})");
                page.addOnloadScript(sb.toString());
            }
        }

    }
}
