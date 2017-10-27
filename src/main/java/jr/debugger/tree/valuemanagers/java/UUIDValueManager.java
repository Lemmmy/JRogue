package jr.debugger.tree.valuemanagers.java;

import jr.debugger.tree.valuemanagers.TypeValueManager;
import jr.debugger.tree.valuemanagers.TypeValueManagerHandler;

import java.lang.reflect.Field;
import java.util.UUID;

@TypeValueManagerHandler({ UUID.class })
public class UUIDValueManager extends TypeValueManager<UUID> {
	@Override
	public String valueToString(Field field, UUID instance) {
		return instance.toString();
	}
}
