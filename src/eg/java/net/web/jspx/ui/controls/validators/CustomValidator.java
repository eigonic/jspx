package eg.java.net.web.jspx.ui.controls.validators;

import java.lang.reflect.InvocationTargetException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eg.java.net.web.jspx.engine.util.PropertyAccessor;
import eg.java.net.web.jspx.engine.util.StringUtility;

/**
 * provides a customized server side validation. the validation is performed by invoking a method in the Page controller.
 * 
 * @author amr.eladawy
 * 
 */
public class CustomValidator extends Validator
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2161660519455494244L;
	private static final Logger logger = LoggerFactory.getLogger(CustomValidator.class);

	public CustomValidator()
	{
		super("7");
	}

	@Override
	public void validate() throws ValidationException
	{
		getControlToValidate();
		// [Nov 13, 2011 7:28:54 PM] [amr.eladawy] [call the page method to invoke validation]
		String methodName = getOnServerValidate();
		if (StringUtility.isNullOrEmpty(methodName))
		{
			logger.warn("The custom validator [" + getId() + "] has no server side validation method. validation is skipped");
			return;
		}
		try
		{
			PropertyAccessor.invokePageMethod(page, page.getClass(), methodName, controlToValidate, "");
		}
		catch (InvocationTargetException e)
		{
			invalid = true;
			throw new ValidationException("Custom Validation Exception", e);
		}
		catch (Exception e)
		{
			invalid = true;
			throw new ValidationException("Custom Validation Exception", e);
		}

	}

	protected String onServerValidateKey = "onServerValidate";

	public String getOnServerValidate()
	{
		return getAttributeValue(onServerValidateKey);
	}

	public void setOnServerValidate(String onServerValidate)
	{
		setAttributeValue(onServerValidateKey, onServerValidate);
	}

}
