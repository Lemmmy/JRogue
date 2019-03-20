package jr.dungeon.entities;

import jr.ErrorHandler;
import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.containers.EntityItem;
import jr.dungeon.items.ItemStack;
import jr.dungeon.items.valuables.ItemGold;

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
		
		level.entityStore.addEntity(entity);
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends Entity> T spawnClass(Class<? extends T> entityClass, Level level, int x, int y) {
		try {
			Constructor entityConstructor = entityClass.getConstructor(
				Dungeon.class, Level.class,
				int.class, int.class
			);
			
			T entity = (T) entityConstructor.newInstance(
				level.getDungeon(), level,
				x, y
			);
			
			level.entityStore.addEntity(entity);
			
			return entity;
		} catch (Exception e) {
			ErrorHandler.error("Error spawning entity", e);
		}
		
		return null;
	}
}
