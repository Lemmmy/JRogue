package jr.dungeon.entities;

import jr.ErrorHandler;
import jr.dungeon.Dungeon;
import jr.dungeon.entities.containers.EntityItem;
import jr.dungeon.items.valuables.ItemGold;
import jr.dungeon.Level;
import jr.dungeon.items.ItemStack;

import java.lang.reflect.Constructor;

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
		
		level.getEntityStore().addEntity(entity);
	}
	
	public static void spawnClass(Class<? extends Entity> entityClass, Level level, int x, int y) {
		try {
			Constructor entityConstructor = entityClass.getConstructor(
				Dungeon.class, Level.class,
				int.class, int.class
			);
			
			Entity entity = (Entity) entityConstructor.newInstance(
				level.getDungeon(), level,
				x, y
			);
			
			level.getEntityStore().addEntity(entity);
		} catch (Exception e) {
			ErrorHandler.error("Error spawning entity", e);
		}
	}
}
