package jr.debugger.tree.namehints;

import java.lang.reflect.Field;
import java.text.NumberFormat;

@TypeNameHintHandler({ Byte.class, Short.class, Integer.class, Double.class, Float.class })
public class NumberNameHint extends TypeNameHint<Number> {
	protected static final NumberFormat numberFormat = NumberFormat.getNumberInstance();
	
	@Override
	public String toNameHint(Field field, Number instance) {
		return numberFormat.format(instance);
	}
}
