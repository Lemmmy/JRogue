package jr.debugger.tree.valuehints.java;

import jr.debugger.tree.valuehints.TypeValueHint;
import jr.debugger.tree.valuehints.TypeValueHintHandler;

import java.lang.reflect.Field;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

@TypeValueHintHandler({ Long.class })
public class LongValueHint extends TypeValueHint<Long> {
	private static final NumberFormat numberFormat = NumberFormat.getNumberInstance();
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	
	@Override
	public String toValueHint(Field field, Long instance) {
		if (field != null && field.getName().toLowerCase().contains("time")) {
			return dateFormat.format(instance);
		}
		
		return numberFormat.format(instance);
	}
}
