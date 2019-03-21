package jr.debugger.tree.valuemanagers.settertypes;

import java.lang.reflect.Field;

public abstract class TypeValueSetter<T, ValueT> {
	public abstract void set(Field field, Object instance, ValueT value);
}