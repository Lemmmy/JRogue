package jr.debugger.tree.namehints.java;

import jr.debugger.tree.namehints.TypeNameHint;
import jr.debugger.tree.namehints.TypeNameHintHandler;

import java.lang.reflect.Field;

@TypeNameHintHandler({ String.class })
public class StringNameHint extends TypeNameHint<String> {
	@Override
	public String toNameHint(Field field, String instance) {
		return String.format(
			"[P_GREEN_2]\"[][P_GREEN_4]%s[][P_GREEN_2]\"[]",
			instance
		);
	}
}
