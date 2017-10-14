package jr.debugger.tree.valuehints.java;

import jr.debugger.tree.valuehints.TypeValueHint;
import jr.debugger.tree.valuehints.TypeValueHintHandler;

import java.lang.reflect.Field;

@TypeValueHintHandler({ Enum.class })
public class EnumValueHint extends TypeValueHint<Enum> {
	@Override
	public String toValueHint(Field field, Enum instance) {
		return instance.name();
	}
}
