package jr.debugger.tree.valuemanagers.settertypes.java;

import jr.debugger.tree.valuemanagers.ValueSetError;
import jr.debugger.tree.valuemanagers.settertypes.TypeValueSetter;

import java.lang.reflect.Field;

public class LongSetter extends TypeValueSetter<Long, String> {
	@Override
	public void set(Field field, Long instance, String value) {
		try {
			field.setLong(instance, Long.parseLong(value));
		} catch (IllegalAccessException e) {
			throw new ValueSetError("Error setting long value", e);
		}
	}
}
