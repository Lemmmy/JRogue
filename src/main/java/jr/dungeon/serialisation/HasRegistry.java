package jr.dungeon.serialisation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that this class should have a registry in the {@link DungeonRegistries} system. Subclasses of it can define
 * {@link Registered @Registered}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface HasRegistry {
}
