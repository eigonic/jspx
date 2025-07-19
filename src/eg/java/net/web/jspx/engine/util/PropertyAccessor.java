package eg.java.net.web.jspx.engine.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eg.java.net.web.jspx.engine.data.DataField;
import eg.java.net.web.jspx.engine.error.JspxException;
import eg.java.net.web.jspx.ui.controls.WebControl;
import eg.java.net.web.jspx.ui.controls.html.GenericWebControl;
import eg.java.net.web.jspx.ui.pages.Page;

//import org.apache.commons.beanutils.BeanUtils;

public class PropertyAccessor
{

	private static final Logger logger = LoggerFactory.getLogger(PropertyAccessor.class);

	private PropertyAccessor()
	{
	}

	/**
	 * gets the Getter method name from an ID
	 * 
	 * @param id
	 * @return
	 */
	protected static String getGetter(String id)
	{
		return new StringBuilder("get").append(getTerName(id)).toString();

	}

	/**
	 * gets the Setter method name from an ID
	 * 
	 * @param id
	 * @return
	 */
	protected static String getSetter(String id)
	{
		return new StringBuilder("set").append(getTerName(id)).toString();
	}

	/**
	 * gets the Is method name from an ID
	 * 
	 * @param id
	 * @return
	 */
	protected static String getIser(String id)
	{
		return new StringBuilder("is").append(getTerName(id)).toString();
	}

	/**
	 * gets the property name to be added to set or get or is
	 * 
	 * @param id
	 * @return
	 */
	protected static StringBuilder getTerName(String id)
	{
		id = id.trim();
		if (id.indexOf("_") > 0)
		{
			return getMethodName(id);
		}
		return new StringBuilder().append(id.substring(0, 1).toUpperCase()).append(id.subSequence(1, id.length()));

	}

	protected static StringBuilder getMethodName(String fieldName)
	{
		StringBuilder s = new StringBuilder();
		fieldName = fieldName.toLowerCase();
		StringTokenizer st = new StringTokenizer(fieldName, "_");
		while (st.hasMoreTokens())
		{
			String token = st.nextToken();
			s.append(token.toUpperCase().charAt(0)).append(token.substring(1));
		}
		return s;
	}

	/**
	 * to convert
	 * 
	 * @param obj
	 * @param clazz
	 * @return
	 */
	public static Object convert(Object obj, Class clazz, Object... objects)
	{
		if (obj == null)
			return null;
		try
		{
			if (obj instanceof Timestamp)
			{
				if (clazz.equals(Date.class))
					return new Date(((Timestamp) obj).getTime());
				if (clazz.equals(Timestamp.class))
					return (Timestamp) obj;
				if (clazz.equals(java.sql.Date.class))
					return new java.sql.Date(((Timestamp) obj).getTime());
			}
			if (obj instanceof java.sql.Date)
			{
				if (clazz.equals(Date.class))
					return new Date(((java.sql.Date) obj).getTime());
				if (clazz.equals(Timestamp.class))
					return new Timestamp(((java.sql.Date) obj).getTime());
				if (clazz.equals(java.sql.Date.class))
					return (java.sql.Date) obj;
			}
			if (obj instanceof Date)
			{
				if (clazz.equals(Date.class))
					return (Date) obj;
				if (clazz.equals(Timestamp.class))
					return new Timestamp(((Date) obj).getTime());
				if (clazz.equals(java.sql.Date.class))
					return new java.sql.Date(((Date) obj).getTime());
			}
			String s = obj.toString();
			if (clazz.equals(String.class))
				return s;
			if (!StringUtility.isNullOrEmpty(s))
			{
				if (clazz.equals(Integer.class))
					return Integer.parseInt(s);
				else if (clazz.equals(int.class))
					return Integer.parseInt(s);
				else if (clazz.equals(Long.class))
					return new Long(s);
				else if (clazz.equals(long.class))
					return Long.parseLong(s);
				else if (clazz.equals(Double.class))
					return new Double(s);
				else if (clazz.equals(double.class))
					return Double.parseDouble(s);
				else if (clazz.equals(Float.class))
					return new Float(s);
				else if (clazz.equals(float.class))
					return Float.parseFloat(s);
				else if (clazz.equals(Date.class))
				{
					SimpleDateFormat sdf = new SimpleDateFormat(objects[0].toString());
					return sdf.parse(s);
				}
				else if (clazz.equals(BigDecimal.class))
					return new BigDecimal(s);

				else if (clazz.equals(boolean.class) || clazz.equals(Boolean.class))
					return Boolean.parseBoolean(s);
			}
			else
				return null;
			// TODO please support other data types conversion.
		}
		catch (Exception e)
		{
			logger.debug("Exception while converting object value... " + e);
		}
		return obj;
	}

	/**
	 * checks if a given object has a setter method for the given name
	 * 
	 * @param object
	 *            the object that is checked for the setter method.
	 * @param name
	 * @return true if the object has a method setName()
	 */
	public static boolean hasSetter(Object object, String name)
	{
		try
		{
			String setter = getSetter(name);
			Method[] methods = object.getClass().getDeclaredMethods();
			for (int i = 0; i < methods.length; i++)
			{
				if (methods[i].getName().equals(setter))
					return true;
			}
		}
		catch (Exception e)
		{
			logger.error("hasSetter(object=" + object + ", name=" + name + ")", e);
		}
		return false;
	}

	/**
	 * checks if a given object has a getter method for the given name
	 * 
	 * @param object
	 *            the object that is checked for the getter method.
	 * @param name
	 * @return true if the object has a method getName()
	 */
	private static boolean hasGetter1(Object object, String name)
	{
		try
		{
			object.getClass().getMethod(getGetter(name));
			return true;
		}
		catch (Exception e)
		{
		}
		return false;
	}

	/**
	 * checks if a given object has a getter method for the given name and the return of the method is the same as the given class
	 * 
	 * @param object
	 *            the object that is checked for the getter method.
	 * @param name
	 * @return true if the object has a method getName()
	 */
	public static boolean hasGetter(Object object, String name)
	{
		try
		{
			Method method = object.getClass().getMethod(getGetter(name));
			if (method.getParameterTypes() != null && method.getParameterTypes().length == 0)
			{
				if (method.getReturnType().getCanonicalName().equals(GenericWebControl.class.getCanonicalName()))
					return true;
				Object obj = method.getReturnType().newInstance();
				return obj instanceof WebControl;
			}
			return false;
		}
		catch (Exception e)
		{
		}
		return false;
	}

	/**
	 * gets a Control declared on a page. if the control is not found (no getter, a null is returned);
	 * 
	 * @param page
	 * @param name
	 * @return
	 */
	public static Object getPageCotnrol(Page page, String name)
	{
		String getter = null;
		try
		{
			getter = getGetter(name);
			return (page.getClass().getMethod(getter)).invoke(page);
		}
		catch (NoSuchMethodException e)
		{
			logger.warn("The control  [" + name + "] has no getter in page[" + page.getClass().getSimpleName() + "]");
			throw new JspxException(e);
		}
		catch (Exception e)
		{
			logger.debug("couldn't get property >> " + name + " for page>> " + page + "... error: " + e);
		}
		return null;
	}

	/**
	 * gets property value using its name from certain object.
	 * 
	 * @param object
	 * @param name
	 * @return
	 */
	public static Object getProperty(Object object, String name)
	{
		String getter = null;
		// return BeanUtils.getProperty(object, name);
		int dotIndex = name.indexOf('.');
		if (dotIndex < 0)
		{
			try
			{
				getter = getGetter(name);
				return (object.getClass().getMethod(getter)).invoke(object);
			}
			catch (NoSuchMethodException e)
			{
				// [Sep 29, 2012 9:05:48 AM] [Amr.ElAdawy] [check if the getter is in form of Boolean]
				getter = getIser(name);
				try
				{
					return (object.getClass().getMethod(getter)).invoke(object);
				}
				catch (Exception e1)
				{
				}
				logger.trace(
						"Property [" + name + "] was not found in class [" + object.getClass().getSimpleName() + "] no [get/is] method is found");
				// [Jul 10, 2009 6:48:39 PM] [amr.eladawy] [I think no need to do that, as object.getClass().getMethod(getter)
				// already search the super ]
				// return getPropertyFromSuperClass(object, object.getClass(), getter);
			}
			catch (Exception e)
			{
				logger.debug("couldn't get property >> " + name + " for object>> " + object + "... error: " + e);
			}
		}
		else
		{
			String restName = name.substring(dotIndex + 1);
			name = name.substring(0, dotIndex);
			getter = getGetter(name);
			Object o = null;
			try
			{
				o = object.getClass().getMethod(getter).invoke(object);
			}
			catch (NoSuchMethodException e)
			{
				// logger.warn("No Method [" + getter + "] found in class [" + object.getClass().getSimpleName() + "]");
				// [Jul 10, 2009 6:48:39 PM] [amr.eladawy] [I think no need to do that, as object.getClass().getMethod(getter)
				// already search the super ]
				// o = getPropertyFromSuperClass(object, object.getClass(), getter);
			}
			catch (Exception e)
			{
				logger.debug("couldn't get property >> " + name + " for object>> " + object + "... error: " + e);
			}
			if (o != null)
				o = getProperty(o, restName);
			return o;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static Object getPropertyFromSuperClass(Object object, Class objClass, String getter)
	{
		Class newClass = objClass;
		Object value = null;
		for (int i = 0; i < 3; i++)
		{
			newClass = newClass.getSuperclass();
			try
			{
				value = (newClass.getMethod(getter)).invoke(object);
				break;
			}
			catch (NoSuchMethodException e)
			{
				continue;
			}
			catch (Throwable e)
			{
				return null;
			}
		}
		return value;
	}

	// FIXME implement SetPropertyFromSuperClass
	public static void setProperty(Object object, String name, Object value, Object... objects)
			throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
	{
		try
		{
			// [20 Aug 2015 13:04:39] [aeladawy] [if the object is a Hashtable<String, DataField> then put the key and value]
			if (object instanceof Hashtable<?, ?>)
			{
				try
				{
					((Hashtable<String, DataField>) object).put(name, (DataField) value);
				}
				catch (Exception e)
				{
					logger.warn("Failed to set value in hashmap", e);
				}
				return;
			}
			Method[] methods = object.getClass().getMethods();
			// return BeanUtils.getProperty(object, name);
			int dotIndex = name.indexOf('.');
			if (dotIndex < 0)
			{
				setProperty(object, methods, name, value, objects);
			}
			else
			{
				String restName = name.substring(dotIndex + 1);
				name = name.substring(0, dotIndex);
				Object obj = object.getClass().getMethod(getGetter(name)).invoke(object);
				if (obj == null)
				{
					obj = object.getClass().getMethod(getGetter(name)).getReturnType().newInstance();
					object.getClass().getMethod(getSetter(name), obj.getClass()).invoke(object, obj);
				}
				setProperty(obj, restName, value, objects);

			}
		}
		catch (Exception ex)
		{
			logger.warn("Couldn't set property: " + name + " with value: " + value + ", in the object: " + object);
		}
	}

	public static void setProperty(Object object, Method[] methods, String name, Object value, Object... objects)
			throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
	{
		try
		{
			String setter = getSetter(name);
			for (int i = 0; i < methods.length; i++)
			{
				if (methods[i].getName().equals(setter))
				{
					invokeSetter(object, methods[i], value, objects);
					return;
				}
			}
		}
		catch (Exception e)
		{
			logger.warn("Couldn't set property: " + name + " with value: " + value + ", in the object: " + object);
		}
	}

	/**
	 * invokes setter method of Object
	 * 
	 * @param object
	 * @param method
	 *            that is to be invoked
	 * @param value
	 *            the value to be set in the object
	 * @param objects
	 *            any extra information
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static void invokeSetter(Object object, Method method, Object value, Object... objects)
			throws IllegalAccessException, InvocationTargetException
	{
		Class[] types = method.getParameterTypes();
		if (types.length == 1)
		{
			if (value != null)
				value = convert(value, types[0], objects);
			try
			{
				method.invoke(object, value);
			}
			catch (Throwable e)
			{
				logger.debug("Couldn't invoke the setter: " + method.getName() + " in the object: " + object.getClass().getCanonicalName()
						+ ", error: " + e);
			}
		}
	}

	/**
	 * gets the name of the property given the getter method name i.e. getId --> id
	 * 
	 * @param getter
	 * @return
	 */
	public static String getPropertName(String getter)
	{
		return new StringBuilder(getter.substring(3, 4).toLowerCase()).append(getter.substring(4)).toString();
	}

	/**
	 * invokes a method in a page controller.
	 * 
	 * @Author Amr.ElAdawy Aug 27, 2013 3:38:22 AM
	 * @param page
	 * @param pageClass
	 * @param methodName
	 * @param invoker
	 * @param args
	 * @throws InvocationTargetException
	 */
	public static void invokePageMethod(Page page, Class<? extends Page> pageClass, String methodName, WebControl invoker, String args)
			throws InvocationTargetException
	{
		try
		{
			pageClass.getDeclaredMethod(methodName, WebControl.class, String.class).invoke((pageClass.cast(page)), invoker, args);
		}
		catch (NoSuchMethodException e)
		{
			logger.error("Event Not found, please make sure that method name is  [" + methodName + "] ");
			logger.warn(e.getMessage());
		}
		catch (IllegalArgumentException e)
		{
			logger.error("Event Not found, please make sure that method [" + methodName + "] has the signature [public " + methodName
					+ "(WebControl, String )]");
			logger.warn(e.getMessage());
		}
		catch (SecurityException e)
		{
			logger.error("Invalid  method modifier , please make sure that the controller has a method with the name [" + methodName
					+ "] with public modifier");
			logger.warn(e.getMessage());
		}
		catch (IllegalAccessException e)
		{
			logger.warn(e.getMessage());
		}
	}

	/**
	 * invokes a method in a WebControl
	 * 
	 * @Author Amr.ElAdawy Aug 27, 2013 3:38:37 AM
	 * @param targetControl
	 * @param webControlClass
	 * @param methodName
	 * @param invoker
	 * @param args
	 * @throws InvocationTargetException
	 */
	public static void invokeWebContorlMethod(WebControl targetControl, Class<? extends WebControl> webControlClass, String methodName, String args)
			throws InvocationTargetException
	{
		Class current = webControlClass;
		Method method = null;
		do
		{
			try
			{
				method = current.getDeclaredMethod(methodName, String.class);
				break;
			}
			catch (NoSuchMethodException ex)
			{
				current = current.getSuperclass();
			}

		}
		while (current != WebControl.class);

		try
		{
			if (method == null)
				throw new NoSuchMethodException();
			method.invoke((webControlClass.cast(targetControl)), args);
		}
		catch (NoSuchMethodException e)
		{
			logger.error("Event Not found, please make sure that method name is  [" + methodName + "] ");
			logger.warn(e.getMessage());
		}
		catch (IllegalArgumentException e)
		{
			logger.error("Event Not found, please make sure that method [" + methodName + "] has the signature [public " + methodName
					+ "(WebControl, String )]");
			logger.warn(e.getMessage());
		}
		catch (SecurityException e)
		{
			logger.error("Invalid  method modifier , please make sure that the controller has a method with the name [" + methodName
					+ "] with public modifier");
			logger.warn(e.getMessage());
		}
		catch (IllegalAccessException e)
		{
			logger.warn(e.getMessage());
		}
	}
}
