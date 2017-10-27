package jr.debugger.tree.valuemanagers.java;

import jr.debugger.tree.valuemanagers.TypeValueManager;
import jr.debugger.tree.valuemanagers.TypeValueManagerHandler;
import jr.debugger.tree.valuemanagers.settertypes.java.LongSetter;

import java.lang.reflect.Field;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

@TypeValueManagerHandler({ Long.class })
public class LongValueManager extends TypeValueManager<Long, LongSetter> {
	private static final LongSetter setter = new LongSetter();
	
	private static final NumberFormat numberFormat = NumberFormat.getNumberInstance();
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	
	@Override
	public String valueToString(Field field, Long instance) {
		if (field != null && field.getName().toLowerCase().contains("time")) {
			return dateFormat.format(instance);
		}
		
		return numberFormat.format(instance);
	}
	
	@Override
	public boolean canSet(Field field, Long instance) {
		return true;
	}
	
	@Override
	public LongSetter getSetter(Field field, Long instance) {
		return setter;
	}
}
