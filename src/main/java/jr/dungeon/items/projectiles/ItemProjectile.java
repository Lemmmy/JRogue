package jr.dungeon.items.projectiles;

import jr.JRogue;
import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.projectiles.EntityProjectile;
import jr.dungeon.items.Item;

import java.lang.reflect.Constructor;
import java.util.Optional;

public abstract class ItemProjectile extends Item {
	public abstract Class<? extends EntityProjectile> getProjectileEntity();
	
	public Optional<? extends EntityProjectile> createProjectile(Entity emitter, int x, int y) {
		try {
			Constructor<? extends EntityProjectile> c = getProjectileEntity().getConstructor(
				Dungeon.class,
				Level.class,
				int.class,
				int.class
			);
			
			return Optional.of(c.newInstance(
				emitter.getDungeon(),
				emitter.getLevel(),
				emitter.getX() + x,
				emitter.getY() + y
			));
		} catch (Exception e) {
			JRogue.getLogger().error("Couldn't create projectile entity", e);
		}
		
		return Optional.empty();
	}
}
