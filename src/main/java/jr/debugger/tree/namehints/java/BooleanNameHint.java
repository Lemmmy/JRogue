package jr.debugger.tree.namehints.java;

import jr.debugger.tree.namehints.TypeNameHint;
import jr.debugger.tree.namehints.TypeNameHintHandler;

import java.lang.reflect.Field;

@TypeNameHintHandler({ Boolean.class })
public class BooleanNameHint extends TypeNameHint<Boolean> {
	@Override
	public String toNameHint(Field field, Boolean instance) {
		return instance.toString();
	}
}
