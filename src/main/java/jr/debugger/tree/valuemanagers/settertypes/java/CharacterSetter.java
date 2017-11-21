package jr.debugger.tree.valuemanagers.settertypes.java;

import jr.debugger.tree.valuemanagers.ValueSetError;
import jr.debugger.tree.valuemanagers.settertypes.TypeValueSetter;

import java.lang.reflect.Field;

public class CharacterSetter extends TypeValueSetter<Character, String> {
	@Override
	public void set(Field field, Object instance, String value) {
		try {
			field.setAccessible(true);
			field.setChar(instance, value.charAt(0));
		} catch (IllegalAccessException | IllegalArgumentException e) {
			throw new ValueSetError("Error setting char value", e);
		}
	}
}
