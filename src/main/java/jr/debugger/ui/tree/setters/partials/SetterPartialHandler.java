package jr.debugger.ui.tree.setters.partials;

import jr.debugger.tree.valuemanagers.settertypes.TypeValueSetter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SetterPartialHandler {
    Class<? extends TypeValueSetter>[] value();
}