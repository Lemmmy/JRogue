package pw.lemmmy.jrogue.dungeon.items.weapons;

import pw.lemmmy.jrogue.JRogue;
import pw.lemmmy.jrogue.dungeon.entities.player.Attribute;
import pw.lemmmy.jrogue.dungeon.entities.player.Player;
import pw.lemmmy.jrogue.dungeon.entities.projectiles.EntityProjectile;
import pw.lemmmy.jrogue.dungeon.entities.LivingEntity;
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
    public void fire(LivingEntity attacker, LivingEntity victim, ItemProjectile projectileItem, int dx, int dy) {
        Optional<? extends EntityProjectile> projectileOpt = projectileItem.createProjectile(attacker, 0, 0);

        if (!projectileOpt.isPresent()) {
            JRogue.getLogger().error("Failed to fire projectile!");
            return;
        }

        EntityProjectile projectile = projectileOpt.get();
        projectile.setTravelRange(getTravelRange(attacker));
        projectile.update();
        attacker.getLevel().addEntity(projectile);
    }
	
	private int getTravelRange(LivingEntity attacker) {
		int strength = 8;
		
		if (attacker instanceof Player) {
			strength = ((Player) attacker).getAttributes().getAttribute(Attribute.STRENGTH);
		}
		
		// TODO: check if this is appropriate launcher
		
		return (int) Math.floor(strength / 2 + 2);
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
    public abstract String getName(boolean requiresCapitalisation, boolean plural);

    @Override
    public abstract float getWeight();

    @Override
    public abstract ItemAppearance getAppearance();
    
    public abstract List<Class<? extends ItemProjectile>> getValidProjectiles();
}
