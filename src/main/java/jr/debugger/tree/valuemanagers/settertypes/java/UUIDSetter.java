package jr.debugger.tree.valuemanagers.settertypes.java;

import jr.debugger.tree.valuemanagers.ValueSetError;
import jr.debugger.tree.valuemanagers.settertypes.TypeValueSetter;

import java.lang.reflect.Field;
import java.util.UUID;

public class UUIDSetter extends TypeValueSetter<UUID, String> {
	@Override
	public void set(Field field, UUID instance, String value) {
		try {
			field.set(instance, UUID.fromString(value));
		} catch (IllegalAccessException e) {
			throw new ValueSetError("Error setting uuid value", e);
		}
	}
}
