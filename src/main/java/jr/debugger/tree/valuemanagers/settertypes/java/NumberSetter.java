package jr.debugger.tree.valuemanagers.settertypes.java;

import jr.debugger.tree.valuemanagers.ValueSetError;
import jr.debugger.tree.valuemanagers.settertypes.TypeValueSetter;

import java.lang.reflect.Field;

public class NumberSetter extends TypeValueSetter<Number, String> {
	@Override
	public void set(Field field, Number instance, String value) {
		try {
			int radix = 10;
			
			value = value.trim();
			
			if (instance instanceof Double) field.setDouble(instance, Double.parseDouble(value));
			if (instance instanceof Float) field.setFloat(instance, Float.parseFloat(value));
			
			if (value.startsWith("0b")) { radix = 2; value = value.replaceFirst("0b", ""); }
			if (value.startsWith("0x")) { radix = 16; value = value.replaceFirst("0x", ""); }
			if (value.startsWith("0")) { radix = 8; value = value.replaceFirst("0", ""); }
			
			if (instance instanceof Byte) field.setByte(instance, Byte.parseByte(value, radix));
			if (instance instanceof Short) field.setShort(instance, Short.parseShort(value, radix));
			if (instance instanceof Integer) field.setInt(instance, Integer.parseInt(value, radix));
		} catch (IllegalAccessException e) {
			throw new ValueSetError("Error setting number value", e);
		}
	}
}
