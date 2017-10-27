package jr.debugger.tree.valuemanagers.java;

import jr.debugger.tree.valuemanagers.TypeValueManager;
import jr.debugger.tree.valuemanagers.TypeValueManagerHandler;
import jr.debugger.tree.valuemanagers.settertypes.java.EnumSetter;

import java.lang.reflect.Field;

@TypeValueManagerHandler({ Enum.class })
public class EnumValueManager extends TypeValueManager<Enum, EnumSetter> {
	@Override
	public String valueToString(Field field, Enum instance) {
		return instance.name();
	}
	
	@Override
	public boolean canSet(Field field, Enum instance) {
		return true;
	}
	
	@Override
	public EnumSetter getSetter(Field field, Enum instance) {
		return new EnumSetter(instance);
	}
}
