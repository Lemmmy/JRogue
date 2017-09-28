package jr.dungeon.items.weapons;

import jr.JRogue;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.player.Attribute;
import jr.dungeon.entities.player.Player;
import jr.dungeon.entities.projectiles.EntityProjectile;
import jr.dungeon.entities.skills.Skill;
import jr.dungeon.items.projectiles.ItemProjectile;

import java.util.List;
import java.util.Optional;

public abstract class ItemProjectileLauncher extends ItemWeapon {
	@Override
	public void hit(EntityLiving attacker, EntityLiving victim) {
		
	}
	
	@Override
	public void zap(EntityLiving attacker, EntityLiving victim, int dx, int dy) {
		
	}
	
	@Override
	public boolean fire(EntityLiving attacker, ItemProjectile projectileItem, int dx, int dy) {
		Optional<? extends EntityProjectile> projectileOpt = projectileItem.createProjectile(attacker, 0, 0);
		
		if (!projectileOpt.isPresent()) {
			JRogue.getLogger().error("Failed to fire projectile!");
			return false;
		}
		
		EntityProjectile projectile = projectileOpt.get();
		projectile.setSource(attacker);
		projectile.setTravelRange(getTravelRange(attacker, projectileItem));
		projectile.setTravelDirection(dx, dy);
		projectile.setOriginalItem(projectileItem);
		projectile.update();
		attacker.getLevel().entityStore.addEntity(projectile);
		
		return true;
	}
	
	private int getTravelRange(EntityLiving attacker, ItemProjectile projectileItem) {
		int strength = 8;
		
		if (attacker instanceof Player) {
			strength = ((Player) attacker).getAttributes().getAttribute(Attribute.STRENGTH);
		}
		
		if (getValidProjectiles().contains(projectileItem.getClass())) {
			return (int) Math.floor(strength / 2 + 2);
		} else {
			return (int) Math.floor(strength / 5.5);
		}
	}
	
	@Override
	public boolean isMelee() {
		return false;
	}
	
	@Override
	public boolean isRanged() {
		return true;
	}
	
	@Override
	public abstract boolean isTwoHanded();
	
	@Override
	public abstract boolean isMagic();
	
	@Override
	public abstract int getToHitBonus();
	
	@Override
	public abstract Skill getSkill();
	
	public abstract List<Class<? extends ItemProjectile>> getValidProjectiles();
}
