package pw.lemmmy.jrogue.dungeon.items.weapons;

import pw.lemmmy.jrogue.JRogue;
import pw.lemmmy.jrogue.dungeon.entities.LivingEntity;
import pw.lemmmy.jrogue.dungeon.entities.player.Attribute;
import pw.lemmmy.jrogue.dungeon.entities.player.Player;
import pw.lemmmy.jrogue.dungeon.entities.projectiles.EntityProjectile;
import pw.lemmmy.jrogue.dungeon.entities.skills.Skill;
import pw.lemmmy.jrogue.dungeon.items.ItemAppearance;
import pw.lemmmy.jrogue.dungeon.items.projectiles.ItemProjectile;

import java.util.List;
import java.util.Optional;

public abstract class ItemProjectileLauncher extends ItemWeapon {
	@Override
	public void hit(LivingEntity attacker, LivingEntity victim) {
		
	}
	
	@Override
	public void zap(LivingEntity attacker, LivingEntity victim, int dx, int dy) {
		
	}
	
	@Override
	public boolean fire(LivingEntity attacker, ItemProjectile projectileItem, int dx, int dy) {
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
		attacker.getLevel().addEntity(projectile);
		
		return true;
	}
	
	private int getTravelRange(LivingEntity attacker, ItemProjectile projectileItem) {
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
	
	@Override
	public abstract String getName(LivingEntity observer, boolean requiresCapitalisation, boolean plural);
	
	@Override
	public abstract float getWeight();
	
	@Override
	public abstract ItemAppearance getAppearance();
	
	public abstract List<Class<? extends ItemProjectile>> getValidProjectiles();
}
