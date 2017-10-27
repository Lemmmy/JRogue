package jr.debugger.tree.valuemanagers.settertypes.java;

import jr.debugger.tree.valuemanagers.ValueSetError;
import jr.debugger.tree.valuemanagers.settertypes.TypeValueSetter;

import java.lang.reflect.Field;

public class BooleanSetter extends TypeValueSetter<Boolean, Boolean> {
	@Override
	public void set(Field field, Boolean instance, Boolean value) {
		try {
			field.setBoolean(instance, value);
		} catch (IllegalAccessException e) {
			throw new ValueSetError("Error setting boolean value", e);
		}
	}
}
