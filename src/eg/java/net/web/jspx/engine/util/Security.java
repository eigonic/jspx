package eg.java.net.web.jspx.engine.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.servlet.ServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eg.java.net.web.jspx.engine.RequestHandler;
import eg.java.net.web.jspx.ui.pages.Page;

/**
 * Utility class for security controls, like Captcha
 * 
 * @author amr.eladawy
 * 
 */
public class Security
{
	private static final Logger logger = LoggerFactory.getLogger(Security.class);

	public static int Numeric = 0;
	public static int Alpha = 1;
	public static int AlphaNumeric = 2;

	private static String num = "0123456789";
	private static String alpha = "ASDFGHJKLQWERTYUIOPZXCVBNMasdfghjklqwertyuiopzxcvbnm";
	private static String num_alpha = num + alpha;

	private static Random random = new Random(System.currentTimeMillis());

	private static String[] pool =
	{ num, alpha, num_alpha };

	/**
	 * creates a random secure code for the Capatcha.
	 * 
	 * @param length
	 * @param type
	 * @return
	 */
	public static String createPasscode(int length, int type)
	{
		StringBuilder builder = new StringBuilder();
		String data = pool[type];
		int dataLenght = data.length();
		for (int i = 0; i < length; i++)
			builder.append(data.charAt((int) (random.nextDouble() * dataLenght)));
		return builder.toString();
	}

	/**
	 * checks whether a resource is accessible by a user or not.
	 * 
	 * @param allowedRoles
	 * @param deniedRoles
	 * @param request
	 * @return
	 */
	public static boolean isAccessible(String allowedRoles, String deniedRoles, Page page)
	{
		// logger.info("0=====&&&&&&&&&&&&&&&&&&&&&&&&&&&&& Checking allowed roles {}, denied  {}, on page {}", new Object[]
		// { allowedRoles, deniedRoles, page });
		if (page == null)
			return true;
		boolean isJaas = getSecurityRoles(page.request) == null;
		// logger.info("1=====&&&&&&&&&&&&&&&&&&&&&&&&&&&&& Checking allowed roles {}, denied  {}, on page {},  checik JAAS = {}",
		// new Object[]
		// { allowedRoles, deniedRoles, page, isJaas });
		List<String> jspxRoles = new ArrayList<String>();
		if (!isJaas)
			jspxRoles = getSecurityRoles(page.request);

		// logger.info("1.5=====&&&&&&&&&&&&&&&&&&&&&&&&&&&&& isAccessible(String, String, Page) - List<String> jspxRoles={}",
		// jspxRoles);

		/**
		 * if there is denied roles then, if all roles denied,then stop them. if the given role inside it, then return false
		 */
		if (!StringUtility.isNullOrEmpty(deniedRoles))
		{
			if (deniedRoles.equals("*"))
				return false;
			String[] roles = deniedRoles.split(",");
			for (int i = 0; i < roles.length; i++)
			{
				if (isJaas)
				{
					if (page.request.isUserInRole(roles[i]))
						return false;
				}
				else if (jspxRoles.contains(roles[i]))
					return false;
			}
		}
		if (!StringUtility.isNullOrEmpty(allowedRoles))
		{
			if (allowedRoles.equals("*"))
				return true;
			String[] roles = allowedRoles.split(",");
			for (int i = 0; i < roles.length; i++)
			{
				// logger.info("2============&&&&&&&&&&&&&& checking the role {} ", roles[i]);
				if (isJaas)
				{
					if (page.request.isUserInRole(roles[i]))
						return true;
				}
				else if (jspxRoles.contains(roles[i]))
				{
					// logger.info("3=============&&&&&&&&&&&&&& The role {} was found, return true", roles[i]);
					return true;
				}
			}
			// logger.info("4=============&&&&&&&&&&&&&& return false");
			return false;
		}
		return true;
	}

	/**
	 * Safe method to set the thread name, in order to prevent errors thrown in some hosting areas like GoogleEngine.
	 * 
	 * @param name
	 */
	public static void setThreadName(String name)
	{
		try
		{
			Thread.currentThread().setName(name);
		}
		catch (Throwable e)
		{

		}
	}

	/**
	 * Gets the security roles assigned to this HTTP request
	 * 
	 * @param request
	 * 
	 * @return the roles
	 */
	public static List<String> getSecurityRoles(ServletRequest request)
	{
		return (List<String>) request.getAttribute(RequestHandler.JSPX_USER_ROLES);
	}

	/**
	 * sets the user roles in the request
	 * 
	 * @param roles
	 *            the roles to set
	 * @param request
	 */
	public static void setSecurityRoles(List<String> roles, ServletRequest request)
	{
		// logger.info("################## setting   the roles with the value {} ", roles);
		request.setAttribute(RequestHandler.JSPX_USER_ROLES, roles);
	}
}
