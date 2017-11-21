package jr.debugger.tree.valuemanagers.java;

import jr.debugger.tree.valuemanagers.TypeValueManager;
import jr.debugger.tree.valuemanagers.TypeValueManagerHandler;
import jr.debugger.tree.valuemanagers.settertypes.java.CharacterSetter;

import java.lang.reflect.Field;

@TypeValueManagerHandler({ Character.class })
public class CharacterValueManager extends TypeValueManager<Character, CharacterSetter> {
	private static final CharacterSetter setter = new CharacterSetter();
	
	@Override
	public String valueToString(Field field, Character instance) {
		return String.format(
			"[P_GREEN_2]'[][P_GREEN_4]%s[][P_GREEN_2]'[]",
			instance
		);
	}
	
	@Override
	public boolean canSet(Field field, Character instance) {
		return true;
	}
	
	@Override
	public CharacterSetter getSetter(Field field, Character instance) {
		return setter;
	}
}
