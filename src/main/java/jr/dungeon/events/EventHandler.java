package jr.dungeon.events;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EventHandler {
	boolean selfOnly() default false;
	EventInvocationTime invocationTime() default EventInvocationTime.IMMEDIATELY;
	EventPriority priority() default EventPriority.NORMAL;
}
