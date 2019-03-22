package jr.dungeon.serialisation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Register this class within a {@link DungeonRegistries registry}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Registered {
	/**
	 * @return A unique ID referring to this object class.
	 */
	String id();
}
