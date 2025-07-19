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
public class MSISDN_Validator extends Validator
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7854275166095581650L;
	protected Prefix[] allowedPrefix =
	{ new Prefix("11", 9), new Prefix("14", 9), new Prefix("15", 9), new Prefix("10", 9), new Prefix("12", 9), new Prefix("16", 9),
			new Prefix("17", 9), new Prefix("18", 9), new Prefix("19", 9),

			new Prefix("011", 10), new Prefix("014", 10), new Prefix("015", 10), new Prefix("010", 10), new Prefix("012", 10), new Prefix("016", 10),
			new Prefix("017", 10), new Prefix("018", 10), new Prefix("019", 10),

			new Prefix("2011", 11), new Prefix("2014", 11), new Prefix("2015", 11), new Prefix("2010", 11), new Prefix("2012", 11),
			new Prefix("2016", 11), new Prefix("2017", 11), new Prefix("2018", 11), new Prefix("2019", 11),

			new Prefix("+2011", 12), new Prefix("+2014", 12), new Prefix("+2015", 12), new Prefix("+2010", 12), new Prefix("+2012", 12),
			new Prefix("+2016", 12), new Prefix("+2017", 12), new Prefix("+2018", 12), new Prefix("+2019", 12),

			new Prefix("002011", 13), new Prefix("002014", 13), new Prefix("002015", 13), new Prefix("002010", 13), new Prefix("002012", 13),
			new Prefix("002016", 13), new Prefix("002017", 13), new Prefix("002018", 13), new Prefix("002019", 13),

			new Prefix("11", 10), new Prefix("14", 10), new Prefix("15", 10), new Prefix("10", 10), new Prefix("12", 10), new Prefix("16", 10),
			new Prefix("17", 10), new Prefix("18", 10), new Prefix("19", 10),

			new Prefix("011", 11), new Prefix("014", 11), new Prefix("015", 11), new Prefix("010", 11), new Prefix("012", 11), new Prefix("016", 11),
			new Prefix("017", 11), new Prefix("018", 11), new Prefix("019", 11),

			new Prefix("2011", 12), new Prefix("2014", 12), new Prefix("2015", 12), new Prefix("2010", 12), new Prefix("2012", 12),
			new Prefix("2016", 12), new Prefix("2017", 12), new Prefix("2018", 12), new Prefix("2019", 12),

			new Prefix("+2011", 13), new Prefix("+2014", 13), new Prefix("+2015", 13), new Prefix("+2010", 13), new Prefix("+2012", 13),
			new Prefix("+2016", 13), new Prefix("+2017", 13), new Prefix("+2018", 13), new Prefix("+2019", 13),

			new Prefix("002011", 14), new Prefix("002014", 14), new Prefix("002015", 14), new Prefix("002010", 14), new Prefix("002012", 14),
			new Prefix("002016", 14), new Prefix("002017", 14), new Prefix("002018", 14), new Prefix("002019", 14),

	};
	private int internationLength = 12;

	/**
	 * @param validationType
	 */
	public MSISDN_Validator()
	{
		super("5");
		expersion = "\\d{9}";
	}

	protected static String AllowInternational = "allowinternational";

	public void setAllowInternational(boolean allow)
	{
		setAttributeBooleanValue(AllowInternational, allow);
	}

	public boolean isAllowInternational()
	{
		return getAttributeBooleanValue(AllowInternational);
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
		String data = s;
		boolean valid = false;

		//validate local prefixes
		for (Prefix prefix : allowedPrefix)
		{
			if (data.startsWith(prefix.startWith) && data.length() == prefix.length)
			{
				valid = true;
				break;
			}
		}

		//validate international
		if (!valid && isAllowInternational() && data.length() == internationLength)
			valid = true;

		if (!valid)
		{
			invalid = true;
			throw new ValidationException("Invalid MSISDN [" + data + "]");
		}
	}

	protected class Prefix
	{
		String startWith;
		int length;

		public Prefix(String startWith, int length)
		{
			this.startWith = startWith;
			this.length = length;
		}
	}
}
