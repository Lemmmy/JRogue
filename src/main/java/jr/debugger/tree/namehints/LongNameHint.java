package jr.debugger.tree.namehints;

import java.lang.reflect.Field;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

@TypeNameHintHandler({ Long.class })
public class LongNameHint extends TypeNameHint<Long> {
	private static final NumberFormat numberFormat = NumberFormat.getNumberInstance();
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	
	@Override
	public String toNameHint(Field field, Long instance) {
		if (field != null && field.getName().toLowerCase().contains("time")) {
			return dateFormat.format(instance);
		}
		
		return numberFormat.format(instance);
	}
}
