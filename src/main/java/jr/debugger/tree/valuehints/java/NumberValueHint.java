package jr.debugger.tree.valuehints.java;

import jr.debugger.tree.valuehints.TypeValueHint;
import jr.debugger.tree.valuehints.TypeValueHintHandler;

import java.lang.reflect.Field;
import java.text.NumberFormat;

@TypeValueHintHandler({ Byte.class, Short.class, Integer.class, Double.class, Float.class })
public class NumberValueHint extends TypeValueHint<Number> {
	protected static final NumberFormat numberFormat = NumberFormat.getNumberInstance();
	
	@Override
	public String toValueHint(Field field, Number instance) {
		return numberFormat.format(instance);
	}
}
