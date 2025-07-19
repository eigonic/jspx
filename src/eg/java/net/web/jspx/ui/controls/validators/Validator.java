/**
 * 
 */
package eg.java.net.web.jspx.ui.controls.validators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eg.java.net.web.jspx.engine.ResourceHandler;
import eg.java.net.web.jspx.engine.error.JspxException;
import eg.java.net.web.jspx.engine.parser.TagFactory;
import eg.java.net.web.jspx.engine.util.ResourceUtility;
import eg.java.net.web.jspx.engine.util.StringUtility;
import eg.java.net.web.jspx.engine.util.ui.Render;
import eg.java.net.web.jspx.engine.util.ui.RenderPrinter;
import eg.java.net.web.jspx.ui.controls.WebControl;
import eg.java.net.web.jspx.ui.controls.attrbs.Attribute;
import eg.java.net.web.jspx.ui.controls.attrbs.JspxAttribute;
import eg.java.net.web.jspx.ui.controls.html.GenericWebControl;
import eg.java.net.web.jspx.ui.controls.html.elements.Image;
import eg.java.net.web.jspx.ui.controls.html.elements.Label;
import eg.java.net.web.jspx.ui.controls.html.elements.markers.IAjaxSubmitter;
import eg.java.net.web.jspx.ui.controls.html.elements.markers.ISubmitter;
import eg.java.net.web.jspx.ui.controls.html.elements.markers.IValidator;
import eg.java.net.web.jspx.ui.pages.Page;

/**
 * Super class for all validators.
 * 
 * @author amr.eladawy
 * 
 */
public abstract class Validator extends GenericWebControl implements IValidator
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2177266790210277841L;

	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(Validator.class);

	public static String Required = "required";

	public static String MSISDN = "msisdn";

	public static String Email = "email";

	public static String Length = "length";

	public static String Custom = "custom";

	public static String Range = "range";

	public static String Numeric = "numeric";

	@JspxAttribute
	protected static String ControlToValidateKey = "control_to_validate";

	protected GenericWebControl controlToValidate;

	@JspxAttribute
	protected static String MessageKey = "message";

	@JspxAttribute
	protected static String IndicatorKey = "indicator";

	@JspxAttribute
	protected static String MessageStyleKey = "message_style";

	@JspxAttribute
	protected static String IndicatorStyleKey = "indicator_style";

	@JspxAttribute
	protected static String GroupKey = "group";

	@JspxAttribute
	protected static String type = "type";

	@JspxAttribute
	protected static String showIndicator = "showindicator";

	protected String validationType = "";

	protected String expersion;

	/**
	 * true of the validation failed and the validator will be rendered visible.
	 */
	protected boolean invalid = false;

	/**
	 */
	public Validator(String validationType)
	{
		super(TagFactory.Validator);
		this.validationType = validationType;
		initStyles();
	}

	/**
	 * @param page
	 */
	public Validator(Page page)
	{
		super(TagFactory.Validator, page);
		initStyles();
	}

	/**
	 * overriden to render this control with span .
	 */
	@Override
	public void render(RenderPrinter outputStream) throws Exception
	{
		if (!isRendered() || !isAccessible() || !isIncluded())
			return;
		// [Sep 16, 2013 12:45:47 PM] [Amr.ElAdawy] [add the control to validate to the page script ]

		page.addControlToValidation(getAttributeValue(ControlToValidateKey), getGroup(), validationType, this);
		/**
		 * the new Validation Screen 1. Render Indicator 1.1. if the Indicator is empty then check if this is a required field
		 * validator ==> set the message to * . else render indicator image. render the indicator message as a separate box
		 * 
		 * 2. Render Error the same for error
		 */

		if (!StringUtility.isNullOrEmpty(getIndicator()))
		{
			Image indImg = new Image(page);
			indImg.setSrc(ResourceHandler.ResourcePrefix + ResourceUtility.Info);
			indImg.setId(getControlToValidate().getId() + getValidatorString() + "Info");

			// indImg.addAttribute(new Attribute("onmouseover", "$jspx('#'+this.id+'Msg').fadeIn('slow');"));
			// indImg.addAttribute(new Attribute("onmouseout", "$jspx('#'+this.id+'Msg').fadeOut('slow');"));
			indImg.addAttribute(new Attribute(Render.OnMouseOut, "hideJspxMessage(this)"));
			indImg.addAttribute(new Attribute(Render.OnMouseOver, "showJspxMessage(this)"));
			indImg.setStyle("cursor:pointer;");

			indImg.render(outputStream);

			GenericWebControl indMessage = new GenericWebControl(TagFactory.Span, page);
			indMessage.setId(indImg.getId() + "Msg");
			Label label = new Label(page);
			label.setText(getIndicator());
			indMessage.addControl(label);
			indMessage.setCssClass("jspxInfo");
			indMessage.setStyle("display:none;position:abolute;");
			indMessage.render(outputStream);
		}
		// show the error
		Image errImg = new Image(page);
		errImg.setSrc(ResourceHandler.ResourcePrefix + ResourceUtility.Error);
		errImg.setId(getControlToValidate().getId() + "Error" + getValidatorString());
		// errImg.addAttribute(new Attribute("onmouseover", "$jspx('#'+this.id+'Msg').fadeIn('slow');"));
		// errImg.addAttribute(new Attribute("onmouseout", "$jspx('#'+this.id+'Msg').fadeOut('slow');"));
		errImg.addAttribute(new Attribute(Render.OnMouseOut, "hideJspxMessage(this)"));
		errImg.addAttribute(new Attribute(Render.OnMouseOver, "showJspxMessage(this)"));
		if (invalid)
			errImg.setStyle("display:inline;cursor:pointer;");
		else
			errImg.setStyle("display:none;cursor:pointer;");

		errImg.render(outputStream);

		GenericWebControl errMessage = new GenericWebControl(TagFactory.Span, page);
		errMessage.setId(errImg.getId() + "Msg");
		Label label = new Label(page);
		label.setText(getMessage());
		errMessage.setStyle("display:none;position:abolute;");
		errMessage.addControl(label);
		errMessage.setCssClass("jspxError");
		errMessage.render(outputStream);

		// ////////////////////////////////////////////
		// ////////////////////////////////////////////

	}

	public GenericWebControl getControlToValidate()
	{
		if (controlToValidate != null)
			return controlToValidate;
		String controlID = getAttributeValue(ControlToValidateKey);
		try
		{
			this.controlToValidate = (GenericWebControl) page.getControl(controlID);
			if (controlToValidate == null)
				throw new JspxException("No Control with id [" + controlID + "] was found to validate");
			page.addControlToValidation(controlID, getGroup(), validationType, this);
		}
		catch (JspxException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new JspxException("No Control with id [" + controlID + "] was found to validate", e);
		}
		return controlToValidate;
	}

	/**
	 * initiate the styles of the message and the indicator.
	 */
	protected void initStyles()
	{
		setMessageStyle("color:red; font-style:italic; font-weight:bold; display:none;");
		setIndicatorStyle("color:grey; font-style:italic;");
	}

	/**
	 * sets the control that will be validated with this validator.
	 * 
	 * @param controlToValidate
	 *            the id of the control to validate.
	 */
	public void setControlToValidate(String controlToValidate)
	{
		if (page != null)
		{
			try
			{
				// page.addControlToValidation(controlToValidate, getGroup(),
				// validationType);
				setAttributeValue(ControlToValidateKey, controlToValidate);
				this.controlToValidate = (GenericWebControl) page.getControl(controlToValidate);
			}
			catch (Exception e)
			{
				logger.error("setControlToValidate(controlToValidate=" + controlToValidate + ")", e);
			}
		}
	}

	/**
	 * Overridden to clone the none attributes elements.
	 */
	@Override
	public WebControl clone(WebControl parent, Page page, ISubmitter submitter, IAjaxSubmitter ajaxSubmitter)
	{
		Validator validator = (Validator) super.clone(parent, page, submitter, ajaxSubmitter);
		validator.setControlToValidate(getAttributeValue(ControlToValidateKey));
		// Commented as it is moved to the Page class.
		// // page.addValidator(validator);
		// page.addControlToValidation(getAttributeValue(ControlToValidateKey), getGroup(), validationType, this);
		return validator;
	}

	/**
	 * validates the associated control .
	 */
	public abstract void validate() throws ValidationException;

	/**
	 * incase the validation failed , the message will be displayed.
	 */
	protected void showError()
	{
		if (invalid)
			setMessageStyle(getMessageStyle().replace("none", "inline"));
	}

	/**
	 * the error message to be displayed when validation failed.
	 * 
	 * @param message
	 */
	public void setMessage(String message)
	{
		setAttributeValue(MessageKey, message);
	}

	public String getMessage()
	{
		return getAttributeValue(MessageKey);
	}

	public void setMessageStyle(String messageStyle)
	{
		setAttributeValue(MessageStyleKey, messageStyle);
	}

	public String getMessageStyle()
	{
		return getAttributeValue(MessageStyleKey);
	}

	/**
	 * directive to the user to help him to insert the valid format.
	 * 
	 * @param indicator
	 */
	public void setIndicator(String indicator)
	{
		setAttributeValue(IndicatorKey, indicator);
	}

	public String getIndicator()
	{
		return getAttributeValue(IndicatorKey);
	}

	public void setIndicatorStyle(String indicatorStyle)
	{
		setAttributeValue(IndicatorStyleKey, indicatorStyle);
	}

	public String getIndicatorStyle()
	{
		return getAttributeValue(IndicatorStyleKey);
	}

	public String getType()
	{
		return getAttributeValue(type);
	}

	public void setType(String typeString)
	{
		setAttributeValue(type, typeString);
	}

	/**
	 * the validation group Name.
	 * 
	 * @param group
	 */
	public void setGroup(String group)
	{
		setAttributeValue(GroupKey, group);
	}

	public String getGroup()
	{
		return getAttributeValue(GroupKey);
	}

	public String getValidatorString()
	{
		return validationType;
	}

	public boolean getShowIndicator()
	{
		Attribute a = getAttribute(showIndicator);
		return StringUtility.isNullOrEmpty(a.getValue(page)) || a.getValue().equalsIgnoreCase("true");
	}

	public void setShowIndicator(String showMarker)
	{
		setAttributeValue(showIndicator, showMarker);
	}
}
