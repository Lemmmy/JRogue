package jr.debugger.tree.namehints.jr;

import jr.debugger.tree.namehints.TypeNameHint;
import jr.debugger.tree.namehints.TypeNameHintHandler;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.player.Player;
import jr.dungeon.tiles.Tile;
import jr.language.transformers.Capitalise;

import java.lang.reflect.Field;

@TypeNameHintHandler(Entity.class)
public class EntityNameHint extends TypeNameHint<Entity> {
	@Override
	public String toNameHint(Field field, Entity instance) {
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