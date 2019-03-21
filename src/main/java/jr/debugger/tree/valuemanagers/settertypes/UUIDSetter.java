package jr.debugger.tree.valuemanagers.settertypes;

import jr.debugger.tree.valuemanagers.ValueSetError;

import java.lang.reflect.Field;
import java.util.UUID;

public class UUIDSetter extends TypeValueSetter<UUID, String> {
	@Override
	public void set(Field field, Object instance, String value) {
		try {
			field.set(instance, UUID.fromString(value));
		} catch (IllegalAccessException | IllegalArgumentException e) {
			throw new ValueSetError("Error setting uuid value", e);
		}
	}
}