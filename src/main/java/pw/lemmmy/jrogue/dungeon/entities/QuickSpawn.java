package pw.lemmmy.jrogue.dungeon.entities;

import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.entities.containers.EntityItem;
import pw.lemmmy.jrogue.dungeon.items.ItemStack;
import pw.lemmmy.jrogue.dungeon.items.valuables.ItemGold;

/**
 * Utils to quickly spawn common entities
 */
public class QuickSpawn {
	public static void spawnGold(Level level, int x, int y, int amount) {
		EntityItem entity = new EntityItem(
			level.getDungeon(),
			level,
			x, y, new ItemStack(new ItemGold(), amount)
		);
		
		level.addEntity(entity);
	}
}
