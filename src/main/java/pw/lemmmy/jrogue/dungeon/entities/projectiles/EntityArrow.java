package pw.lemmmy.jrogue.dungeon.entities.projectiles;

import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.entities.DamageSource;
import pw.lemmmy.jrogue.dungeon.entities.Entity;
import pw.lemmmy.jrogue.dungeon.entities.EntityAppearance;
import pw.lemmmy.jrogue.dungeon.entities.LivingEntity;
import pw.lemmmy.jrogue.dungeon.entities.player.Player;
import pw.lemmmy.jrogue.utils.RandomUtils;

public class EntityArrow extends EntityProjectile {
	private boolean canPenetrate = false;
	
	public EntityArrow(Dungeon dungeon, Level level, int x, int y) {
		super(dungeon, level, x, y);
	}
	
	@Override
	public String getName(LivingEntity observer, boolean requiresCapitalisation) {
		return requiresCapitalisation ? "Arrow" : "arrow";
	}
	
	@Override
	public EntityAppearance getAppearance() {
		return EntityAppearance.APPEARANCE_ARROW;
	}
	
	@Override
	public int getMovementSpeed() {
		return Dungeon.NORMAL_SPEED * 3;
	}
	
	public void setCanPenetrate(boolean penetrate) {
		canPenetrate = penetrate;
	}
	
	@Override
	public void onHitEntity(Entity victim) {
		if (victim instanceof LivingEntity) {
			Entity source = getSource();
			
			if (source != null && source instanceof LivingEntity) {
				LivingEntity living = (LivingEntity) victim;
				
				if (source instanceof Player) {
					source.getDungeon().Your("arrow hits the %s!", living.getName((LivingEntity) source, false));
				}
				
				if (living instanceof Player) {
					living.getDungeon().orangeYou("get hit by an arrow from %s!" + source.getName(living, false));
				}
				
				living.damage(DamageSource.ARROW, getArrowDamage(), (LivingEntity) source, source instanceof Player);
				
				if (!canPenetrate) {
					killProjectile();
				}
			}
		}
	}
	
	private int getArrowDamage() {
		return RandomUtils.roll(6);
	}
	
	@Override
	public void killProjectile() {
		if (!isBeingRemoved() && RandomUtils.roll(3) != 3) {
			dropItems();
		}
		
		super.killProjectile();
	}
	
	@Override
	protected void onKick(LivingEntity kicker, boolean isPlayer, int dx, int dy) {
		
	}
	
	@Override
	protected void onWalk(LivingEntity walker, boolean isPlayer) {
		
	}
}
