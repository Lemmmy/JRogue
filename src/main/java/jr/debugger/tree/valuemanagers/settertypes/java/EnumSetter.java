package jr.debugger.tree.valuemanagers.settertypes.java;

import jr.debugger.tree.valuemanagers.ValueSetError;
import jr.debugger.tree.valuemanagers.settertypes.TypeValueSetter;
import lombok.Getter;

import java.lang.reflect.Field;
import java.util.Arrays;

@Getter
public class EnumSetter extends TypeValueSetter<Enum, String> {
	private String[] values;
	
	public EnumSetter(Enum instance) {
		values = Arrays.stream(instance.getClass().getEnumConstants())
			.map(Enum::name)
			.toArray(String[]::new);
	}
	
	@Override
	public void set(Field field, Object instance, String value) {
		try {
			field.setAccessible(true);
			field.set(instance, Enum.valueOf((Class<Enum>) field.getType(), value));
		} catch (IllegalAccessException | IllegalArgumentException e) {
			throw new ValueSetError("Error setting char value", e);
		}
	}
}
