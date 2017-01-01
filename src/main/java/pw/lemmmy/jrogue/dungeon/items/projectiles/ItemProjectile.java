package pw.lemmmy.jrogue.dungeon.items.projectiles;

import pw.lemmmy.jrogue.JRogue;
import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.entities.Entity;
import pw.lemmmy.jrogue.dungeon.entities.projectiles.EntityProjectile;
import pw.lemmmy.jrogue.dungeon.items.Item;

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
