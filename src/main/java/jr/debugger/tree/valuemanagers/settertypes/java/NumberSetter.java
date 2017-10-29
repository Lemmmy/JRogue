package jr.debugger.tree.valuemanagers.settertypes.java;

import jr.debugger.tree.valuemanagers.ValueSetError;
import jr.debugger.tree.valuemanagers.settertypes.TypeValueSetter;

import java.lang.reflect.Field;
import java.util.Arrays;

public class NumberSetter<T> extends TypeValueSetter<T, String> {
	@Override
	public void set(Field field, Object instance, String value) {
		try {
			field.setAccessible(true);
			
			int radix = 10;
			
			value = value.trim();
			
			if (is(field, Double.class, double.class)) field.setDouble(instance, Double.parseDouble(value));
			if (is(field, Float.class, float.class)) field.setFloat(instance, Float.parseFloat(value));
			
			if (value.startsWith("0b")) { radix = 2; value = value.replaceFirst("0b", ""); }
			if (value.startsWith("0x")) { radix = 16; value = value.replaceFirst("0x", ""); }
			if (value.startsWith("0")) { radix = 8; value = value.replaceFirst("0", ""); }
			
			if (is(field, Byte.class, byte.class)) field.setByte(instance, Byte.parseByte(value, radix));
			if (is(field, Short.class, short.class)) field.setShort(instance, Short.parseShort(value, radix));
			if (is(field, Integer.class, int.class)) field.setInt(instance, Integer.parseInt(value, radix));
			if (is(field, Long.class, long.class)) field.setLong(instance, Long.parseLong(value, radix));
		} catch (IllegalAccessException | IllegalArgumentException e) {
			throw new ValueSetError("Error setting number value", e);
		}
	}
	
	public boolean is(Field field, Class... classes) {
		return Arrays.stream(classes).anyMatch(c -> field.getType().equals(c));
	}
}
