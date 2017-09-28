package jr.debugger.tree.valuehints;

import java.lang.reflect.Field;

public abstract class TypeValueHint<T> {
	public abstract String toValueHint(Field field, T instance);
}
