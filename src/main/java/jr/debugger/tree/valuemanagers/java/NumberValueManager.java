package jr.debugger.tree.valuemanagers.java;

import jr.debugger.tree.valuemanagers.TypeValueManager;
import jr.debugger.tree.valuemanagers.TypeValueManagerHandler;
import jr.debugger.tree.valuemanagers.settertypes.NumberSetter;

import java.lang.reflect.Field;
import java.text.NumberFormat;

@TypeValueManagerHandler({ Byte.class, Short.class, Integer.class, Double.class, Float.class })
public class NumberValueManager extends TypeValueManager<Number, NumberSetter<Number>> {
	private static final NumberSetter setter = new NumberSetter();
	
	protected static final NumberFormat numberFormat = NumberFormat.getNumberInstance();
	
	@Override
	public String valueToString(Field field, Number instance) {
		return numberFormat.format(instance);
	}
	
	@Override
	public boolean canSet(Field field, Number instance) {
		return true;
	}
	
	@Override
	public NumberSetter getSetter(Field field, Number instance) {
		return setter;
	}
}