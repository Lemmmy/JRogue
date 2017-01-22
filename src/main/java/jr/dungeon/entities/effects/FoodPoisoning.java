package jr.dungeon.entities.effects;

import jr.dungeon.entities.DamageSource;
import jr.dungeon.entities.EntityLiving;
import jr.utils.RandomUtils;
import jr.dungeon.Messenger;
import jr.dungeon.entities.Entity;

public class FoodPoisoning extends StatusEffect {
	public FoodPoisoning() {
		super(RandomUtils.random(10, 20));
	}
	
	public FoodPoisoning(Messenger messenger, Entity entity) {
		super(messenger, entity, RandomUtils.random(10, 20));
	}
	
	@Override
	public String getName() {
		return "Food Poisoning";
	}
	
	@Override
	public Severity getSeverity() {
		return Severity.CRITICAL;
	}
	
	@Override
	public void onEnd() {
		if (getEntity() instanceof EntityLiving) {
			EntityLiving living /* well, not anymore */ = (EntityLiving) getEntity();
			
			living.kill(DamageSource.FOOD_POISONING, living.getHealth(), null, false);
		}
	}
}
