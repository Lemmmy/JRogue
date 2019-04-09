package jr.dungeon.entities;

import jr.ErrorHandler;
import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.containers.EntityItem;
import jr.dungeon.items.ItemStack;
import jr.dungeon.items.valuables.ItemGold;
import jr.utils.Point;

import java.lang.reflect.Constructor;

/**
 * Utils to quickly spawn common entities
 */
public class QuickSpawn {
	public static void spawnGold(Level level, Point position, int amount) {
		EntityItem entity = new EntityItem(
			level.getDungeon(),
			level,
			position, new ItemStack(new ItemGold(), amount)
		);
		
		level.entityStore.addEntity(entity);
	}
	
	public static <T extends Entity> T spawnClass(Class<? extends T> entityClass, Level level, Point position) {
		try {
			Constructor entityConstructor = entityClass.getConstructor(Dungeon.class, Level.class, Point.class);
			
			T entity = (T) entityConstructor.newInstance(level.getDungeon(), level, position);
			level.entityStore.addEntity(entity);
			
			return entity;
		} catch (Exception e) {
			ErrorHandler.error("Error spawning entity", e);
		}
		
		return null;
	}
}
