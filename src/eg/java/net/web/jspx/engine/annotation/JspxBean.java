package eg.java.net.web.jspx.engine.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface JspxBean
{

	public static final int REQUEST = 1;
	public static final int SESSION = 2;
	public static final int CONVERSATION = 3;

	String name();

	int scope() default REQUEST;

	String dateformat() default "dd-MM-yyyy";
}