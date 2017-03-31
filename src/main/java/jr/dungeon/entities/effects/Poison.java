package jr.dungeon.entities.effects;

import jr.dungeon.entities.DamageSourceType;
import jr.dungeon.entities.EntityLiving;

public class Poison extends StatusEffect {
	public Poison() {
		this(-1);
	}
	
	public Poison(int duration) {
		super(duration);
	}
	
	public int getDamageLimit() {
		return 0;
	}
	
	public int getHealthLimit() {
		return 0;
	}
	
	@Override
	public void turn() {
		super.turn();
		
		if (getEntity() instanceof EntityLiving) {
			EntityLiving entityLiving = (EntityLiving) getEntity();
			
			if (
				(getDamageLimit() == 0 ||
				 entityLiving.getHealth() > entityLiving.getMaxHealth() - getDamageLimit()) &&
				getHealthLimit() < entityLiving.getHealth()
			) {
				entityLiving.damage(getDamageSource(), 1, null);
			}
			
			if (getHealthLimit() >= entityLiving.getMaxHealth()) {
				// The victim is far too weak to take normal damage and is killed instantaneously
				entityLiving.kill(getDamageSource(), 1, null);
			}
		}
	}
	
	public DamageSourceType getDamageSource() {
		return DamageSourceType.POISON;
	}
	
	@Override
	public String getName() {
		return "Poison";
	}
	
	@Override
	public Severity getSeverity() {
		return Severity.CRITICAL;
	}
	
	@Override
	public void onEnd() {
		getMessenger().greenYou("managed to absorb the deadly poison.");
	}
}
