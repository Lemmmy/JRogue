package jr.rendering.ui.skin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface UISkinStyleHandler {
	UISkinStylePriority priority() default UISkinStylePriority.NORMAL;
}
