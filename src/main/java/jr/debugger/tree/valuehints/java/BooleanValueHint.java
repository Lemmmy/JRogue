package jr.debugger.tree.valuehints.java;

import jr.debugger.tree.valuehints.TypeValueHint;
import jr.debugger.tree.valuehints.TypeValueHintHandler;

import java.lang.reflect.Field;

@TypeValueHintHandler({ Boolean.class })
public class BooleanValueHint extends TypeValueHint<Boolean> {
	@Override
	public String toValueHint(Field field, Boolean instance) {
		return instance.toString();
	}
}
