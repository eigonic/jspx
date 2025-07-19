/**
 * 
 */
package eg.java.net.web.jspx.engine.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for WebControls used for Dependency Injection.
 * @author amr.eladawy
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface JspxWebControl
{
	String name() default "";
}
