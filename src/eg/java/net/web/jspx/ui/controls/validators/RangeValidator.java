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
public class RangeValidator extends Validator
{

	private static final long serialVersionUID = -6561438430565957604L;

	private String minValKey = "minval";

	private String maxValKey = "maxval";

	/**
	 * @param validationType
	 */
	public RangeValidator()
	{
		super("4");
	}

	public void setMaxValue(int maxValue)
	{
		setAttributeValue(maxValKey, String.valueOf(maxValue));
	}

	public void setMinValue(int minValue)
	{
		setAttributeValue(minValKey, String.valueOf(minValue));
	}

	public double getMaxValue()
	{
		return Double.parseDouble(getAttributeValue(maxValKey));
	}

	public double getMinValue()
	{
		return Double.parseDouble(getAttributeValue(minValKey));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eg.java.net.web.jspx.ui.controls.validators.Validator#validate(eg.java.net.web.jspx.ui.controls.WebControl)
	 */
	@Override
	public void validate() throws ValidationException
	{
		getControlToValidate();
		String s = ((ValueHolder) controlToValidate).getValue();
		if (StringUtility.isNullOrEmpty(s))
			return;

		double max = Double.MAX_VALUE;
		if (!StringUtility.isNullOrEmpty(getAttributeValue(maxValKey)))
			max = getMaxValue();
		double min = Double.MIN_VALUE;
		if (!StringUtility.isNullOrEmpty(getAttributeValue(minValKey)))
			min = getMinValue();

		double data = Double.parseDouble(s);
		if (data < min || data > max)
		{
			invalid = true;
			throw new ValidationException("Control [" + controlToValidate.getId() + "] has data [" + data + "] out of range[" + min + "," + max
					+ "] ");
		}
	}

	public String getValidatorString()
	{
		double max = Double.MAX_VALUE;
		if (!StringUtility.isNullOrEmpty(getAttributeValue(maxValKey)))
			max = getMaxValue();
		double min = Double.MIN_VALUE;
		if (!StringUtility.isNullOrEmpty(getAttributeValue(minValKey)))
			min = getMinValue();
		return validationType + "," + min + "," + max;
	}
}
