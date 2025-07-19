package eg.java.net.web.jspx.engine.annotation;

import java.io.Serializable;

public class JspxBeanValue implements Serializable
{

	private static final long serialVersionUID = 3041568304819234391L;
	JspxBean jspxBean;
	Object value;
	long jspxBeanLastAccess;

	public JspxBeanValue()
	{

	}

	public JspxBeanValue(JspxBean jspxBean, Object value)
	{
		this.jspxBean = jspxBean;
		this.value = value;
	}

	public JspxBean getJspxBean()
	{
		return jspxBean;
	}

	public void setJspxBean(JspxBean jspxBean)
	{
		this.jspxBean = jspxBean;
	}

	public Object getValue()
	{
		jspxBeanLastAccess = System.currentTimeMillis();
		return value;
	}

	public void setValue(Object value)
	{
		jspxBeanLastAccess = System.currentTimeMillis();
		this.value = value;
	}

	public void setJspxBeanLastAccess(long jspxBeanLastAccess)
	{
		this.jspxBeanLastAccess = jspxBeanLastAccess;
	}

	public long getJspxBeanLastAccess()
	{
		return jspxBeanLastAccess;
	}

}
