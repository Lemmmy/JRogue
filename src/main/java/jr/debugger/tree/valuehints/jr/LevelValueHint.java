package jr.debugger.tree.valuehints.jr;

import jr.debugger.tree.valuehints.TypeValueHint;
import jr.debugger.tree.valuehints.TypeValueHintHandler;
import jr.dungeon.Level;

import java.lang.reflect.Field;

@TypeValueHintHandler(Level.class)
public class LevelValueHint extends TypeValueHint<Level> {
	@Override
	public String toValueHint(Field field, Level instance) {
		if (instance == null) return "null level";
		
		return String.format(
			"[P_GREY_3]%s[] %,d",
			instance.getName(),
			instance.getDepth()
		);
	}
}