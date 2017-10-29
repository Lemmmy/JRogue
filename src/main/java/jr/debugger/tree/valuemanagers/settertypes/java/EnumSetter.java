package jr.debugger.tree.valuemanagers.settertypes.java;

import jr.debugger.tree.valuemanagers.ValueSetError;
import jr.debugger.tree.valuemanagers.settertypes.TypeValueSetter;
import lombok.Getter;

import java.lang.reflect.Field;
import java.util.Arrays;

@Getter
public class EnumSetter extends TypeValueSetter<Enum, Enum> {
	private Enum[] values;
	
	public EnumSetter(Enum instance) {
		values = Arrays.stream(instance.getClass().getEnumConstants())
			.toArray(Enum[]::new);
	}
	
	@Override
	public void set(Field field, Object instance, Enum value) {
		try {
			field.setAccessible(true);
			field.set(instance, value);
		} catch (IllegalAccessException | IllegalArgumentException e) {
			throw new ValueSetError("Error setting enum value", e);
		}
	}
}
