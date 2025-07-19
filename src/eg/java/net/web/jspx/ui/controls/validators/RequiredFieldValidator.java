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
public class RequiredFieldValidator extends Validator
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1263869502200554502L;

	public RequiredFieldValidator()
	{
		super("1");
		setType(Validator.Required);
		if (getShowIndicator())
			setIndicator("*");
		setMessage("Required");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eg.java.net.web.jspx.ui.controls.validators.Validator#validate()
	 */
	@Override
	public void validate() throws ValidationException
	{
		getControlToValidate();
		if (controlToValidate instanceof ValueHolder && StringUtility.isNullOrEmpty(((ValueHolder) controlToValidate).getValue()))
		{
			invalid = true;
			throw new ValidationException("control [" + controlToValidate.getId() + "] is empty");
		}
	}

}
