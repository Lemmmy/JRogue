package jr.debugger.tree.valuehints.jr;

import jr.debugger.tree.valuehints.TypeValueHint;
import jr.debugger.tree.valuehints.TypeValueHintHandler;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.player.Player;
import jr.language.transformers.Capitalise;

import java.lang.reflect.Field;

@TypeValueHintHandler(Entity.class)
public class EntityValueHint extends TypeValueHint<Entity> {
	@Override
	public String toValueHint(Field field, Entity instance) {
		if (instance == null) return "null entity";
		
		String name = instance.getAppearance().name().replaceFirst("^APPEARANCE_", "");
		
		if (instance.getDungeon() != null) {
			Player player = instance.getDungeon().getPlayer();
			name = instance.getName(player).build(Capitalise.first);
		}
		
		return String.format(
			"[P_GREY_3]%s[] %,d, %,d",
			name,
			instance.getX(), instance.getY()
		);
	}
}