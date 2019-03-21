package jr.debugger.tree.valuemanagers.java;

import jr.debugger.tree.valuemanagers.TypeValueManager;
import jr.debugger.tree.valuemanagers.TypeValueManagerHandler;
import jr.debugger.tree.valuemanagers.settertypes.StringSetter;

import java.lang.reflect.Field;

@TypeValueManagerHandler({ String.class })
public class StringValueManager extends TypeValueManager<String, StringSetter> {
	private static final StringSetter setter = new StringSetter();
	
	@Override
	public String valueToString(Field field, String instance) {
		return String.format(
			"[P_GREEN_2]\"[][P_GREEN_4]%s[][P_GREEN_2]\"[]",
			instance
		);
	}
	
	@Override
	public boolean canSet(Field field, String instance) {
		return true;
	}
	
	@Override
	public StringSetter getSetter(Field field, String instance) {
		return setter;
	}
}