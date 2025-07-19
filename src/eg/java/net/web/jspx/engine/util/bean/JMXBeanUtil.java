package eg.java.net.web.jspx.engine.util.bean;

import java.lang.management.ManagementFactory;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eg.java.net.web.jspx.engine.jmx.JspxAdmin;

/**
 * utility class for JMXBean
 * @author amr.eladawy
 *
 */
public class JMXBeanUtil
{
	private static final Logger logger = LoggerFactory.getLogger(JMXBeanUtil.class);

	/**
	 * register the MBean 
	 * @param sOptions
	 */
	public static void regesiterMXBean(String mBeanName)
	{

		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		ObjectName name = null;
		try
		{
			name = new ObjectName(mBeanName);
		}
		catch (MalformedObjectNameException e)
		{
			logger.error("Jspx Admin MBean name not formatted correctly [" + mBeanName + "]", e);
		}
		JspxAdmin jspxAdmin = new JspxAdmin();
		try
		{
			mbs.registerMBean(jspxAdmin, name);
		}
		catch (InstanceAlreadyExistsException e)
		{
			logger.error("Jspx Admin Instance is Already Exists, Replacing old version.");
			try
			{
				mbs.unregisterMBean(name);
				mbs.registerMBean(jspxAdmin, name);
			}
			catch (Exception e2)
			{
				logger.error("Failed to register MBean......");
			}

		}
		catch (MBeanRegistrationException e)
		{
			logger.error("Jspx Admin Registration Exception", e);
		}
		catch (NotCompliantMBeanException e)
		{
			logger.error("Jspx Admin is Not a Compliant MBean", e);
		}

		logger.debug("regesiterMXBean() - end");
	}
}
