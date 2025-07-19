/**
 * 
 */
package eg.java.net.web.jspx.ui.controls.html.elements.inputs;

import eg.java.net.web.jspx.engine.util.ui.RenderPrinter;
import eg.java.net.web.jspx.ui.controls.attrbs.Attribute;
import eg.java.net.web.jspx.ui.controls.html.elements.Input;
import eg.java.net.web.jspx.ui.controls.html.elements.Label;

/**
 * @author amr.eladawy
 * 
 */
public class CheckBox extends Input
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8675824499049205335L;

	protected static String textKey = "text";

	protected static String checkedKey = "checked";

	@Override
	protected void loadInternalAttributes()
	{
		super.loadInternalAttributes();
		internalAttribtes.put(textKey, 0);
	}

	public CheckBox()
	{
		setType(CheckBox);
	}

	public void setText(String text)
	{
		setAttributeValue(textKey, text);
	}

	public String getText()
	{
		return getAttributeValue(textKey);
	}

	public void setChecked(boolean checked)
	{
		if (checked)
			setAttributeValue(checkedKey, checkedKey);
		else
			attributes.remove(checkedKey);
	}

	public boolean isChecked()
	{
		return attributes.get(checkedKey) != null;
	}

	public void setValue(String value)
	{
		// if the passed value is null or false this is not checked
		setChecked(!(value == null || value.equals(FALSE)));
	}

	public String getValue()
	{
		return isChecked() ? checkedKey : "";
	}

	public void render(RenderPrinter outputStream) throws Exception
	{
		if (!isChecked() || attributes.get(checkedKey).getValue(page).equals(FALSE))
			attributes.remove(checkedKey);
		super.render(outputStream);
		renderLabel(outputStream);
	}

	protected void renderLabel(RenderPrinter outputStream) throws Exception
	{
		Label label = new Label();
		label.setFor(getId());
		label.setText(getText());
		label.getStyle().put("display", new Attribute("display", "inline"));
		label.render(outputStream);
	}

}
