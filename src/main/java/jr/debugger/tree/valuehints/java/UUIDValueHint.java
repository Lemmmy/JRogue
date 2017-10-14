package jr.debugger.tree.valuehints.java;

import jr.debugger.tree.valuehints.TypeValueHint;
import jr.debugger.tree.valuehints.TypeValueHintHandler;

import java.lang.reflect.Field;
import java.util.UUID;

@TypeValueHintHandler({ UUID.class })
public class UUIDValueHint extends TypeValueHint<UUID> {
	@Override
	public String toValueHint(Field field, UUID instance) {
		return instance.toString();
	}
}
