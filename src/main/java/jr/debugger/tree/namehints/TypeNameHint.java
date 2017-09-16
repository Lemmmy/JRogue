package jr.debugger.tree.namehints;

import java.lang.reflect.Field;

public abstract class TypeNameHint<T> {
	public abstract String toNameHint(Field field, T instance);
}
