package jr.debugger.tree.namehints.jr;

import jr.debugger.tree.namehints.TypeNameHint;
import jr.debugger.tree.namehints.TypeNameHintHandler;
import jr.dungeon.Level;

import java.lang.reflect.Field;

@TypeNameHintHandler(Level.class)
public class LevelNameHint extends TypeNameHint<Level> {
	@Override
	public String toNameHint(Field field, Level instance) {
		if (instance == null) return "null level";
		
		return String.format(
			"[P_GREY_3]%s[] %,d",
			instance.getName(),
			instance.getDepth()
		);
	}
}