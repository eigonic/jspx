package eg.java.net.web.jspx.engine.message;

import java.io.Serializable;

import eg.java.net.web.jspx.engine.util.StringUtility;
import eg.java.net.web.jspx.ui.pages.Page;

/**
 * Model for Messages
 * 
 * @author amr.eladawy May 24, 2012 2:59:04 AM
 */
public class JspxMessage implements Serializable
{
	private static final long serialVersionUID = -8589104092838045370L;
	public static int INFO = 1;
	public static int WARNING = 2;
	public static int ERROR = 3;
	public static int SUCCESS = 4;

	public static int FOR_EVER = -1;

	String text;
	String header;
	int type;
	long time;
	private Page page;

	/**
	 * constructs a Jspx message as @
	 * 
	 * @param text
	 * @param header
	 * @param type
	 * @param time
	 */
	public JspxMessage(String text, String header, int type, long time)
	{
		this.text = text;
		this.header = header;
		this.type = type;
		this.time = time;
	}

	public JspxMessage()
	{
	}

	public JspxMessage(String text)
	{
		this.text = text;
	}

	public JspxMessage(String text, String header)
	{
		this.text = text;
		this.header = header;
	}

	public JspxMessage(String text, String header, int type)
	{
		this.text = text;
		this.header = header;
		this.type = type;
	}

	public JspxMessage(String text, int type)
	{
		this.text = text;
		this.type = type;
		if (type == JspxMessage.SUCCESS)
			this.header = "Success";
		else if (type == JspxMessage.ERROR)
			this.header = "Error";
		else if (type == JspxMessage.INFO)
			this.header = "Info";
		else if (type == JspxMessage.WARNING)
			this.header = "Warning";
	}

	public String getText()
	{
		return text;
	}

	public void setText(String text)
	{
		this.text = text;
	}

	public String getHeader()
	{
		return header;
	}

	public void setHeader(String header)
	{
		this.header = header;
	}

	public int getType()
	{
		return type;
	}

	public void setType(int type)
	{
		this.type = type;
	}

	public long getTime()
	{
		return time;
	}

	public void setTime(long time)
	{
		this.time = time;
	}

	private String getFinalText()
	{
		String newValue = StringUtility.evaluateComplexVlaue(getText(), page, null);
		if (newValue == null)
			newValue = getText();
		return newValue;
	}

	/**
	 * gets the JS string for this message
	 */
	@Override
	public String toString()
	{
		if (!StringUtility.isNullOrEmpty(getText()))
		{

			String document = "";
			if (page != null && page.isAjaxPostBack && page.isMultiPartForm)
				document = "window.parent.";
			StringBuilder sb = new StringBuilder(document).append("$jspx.jGrowl('").append(getFinalText().replace("\n", "<br/>").replace("'", "\""))
					.append("',{");
			if (time < 0)
				sb.append("sticky: true,");
			if (!StringUtility.isNullOrEmpty(header))
				sb.append("header: '").append(header).append("',");

			sb.append("theme: '");
			if (type == INFO)
				sb.append("jspxInfo");
			else if (type == ERROR)
				sb.append("jspxError");
			else if (type == WARNING)
				sb.append("jspxWarning");
			else if (type == SUCCESS)
				sb.append("jspxSuccess");
			else
				sb.append("jspxDefault");
			sb.append("'");
			sb.append("});");
			return sb.toString();
		}
		return "";
	}

	public void setPage(Page page)
	{
		this.page = page;

	}
}
