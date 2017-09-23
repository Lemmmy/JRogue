package jr.debugger.tree.valuehints.java;

import jr.debugger.tree.valuehints.TypeValueHint;
import jr.debugger.tree.valuehints.TypeValueHintHandler;

import java.lang.reflect.Field;

@TypeValueHintHandler({ String.class })
public class StringValueHint extends TypeValueHint<String> {
	@Override
	public String toValueHint(Field field, String instance) {
		return String.format(
			"[P_GREEN_2]\"[][P_GREEN_4]%s[][P_GREEN_2]\"[]",
			instance
		);
	}
}
