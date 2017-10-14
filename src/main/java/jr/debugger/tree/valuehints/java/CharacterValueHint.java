package jr.debugger.tree.valuehints.java;

import jr.debugger.tree.valuehints.TypeValueHint;
import jr.debugger.tree.valuehints.TypeValueHintHandler;

import java.lang.reflect.Field;

@TypeValueHintHandler({ Character.class })
public class CharacterValueHint extends TypeValueHint<Character> {
	@Override
	public String toValueHint(Field field, Character instance) {
		return String.format(
			"[P_GREEN_2]'[][P_GREEN_4]%s[][P_GREEN_2]'[]",
			instance
		);
	}
}
