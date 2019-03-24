package jr.dungeon.entities.effects;

import com.google.gson.annotations.Expose;
import jr.dungeon.entities.DamageSource;
import jr.dungeon.entities.DamageType;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.io.Messenger;
import jr.dungeon.items.comestibles.ItemComestible;
import jr.dungeon.serialisation.Registered;
import jr.utils.RandomUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Registered(id="statusEffectFoodPoisoning")
public class FoodPoisoning extends StatusEffect {
	@Expose private ItemComestible sourceFood;
	
	public FoodPoisoning() {
		super(RandomUtils.random(10, 20));
	}
	
	public FoodPoisoning(int duration, ItemComestible sourceFood) {
		super(duration);
		this.sourceFood = sourceFood;
	}
	
	public FoodPoisoning(Messenger messenger, Entity entity) {
		super(messenger, entity, RandomUtils.random(10, 20));
	}
	
	public FoodPoisoning(Messenger messenger, Entity entity, ItemComestible sourceFood) {
		super(messenger, entity, RandomUtils.random(10, 20));
		this.sourceFood = sourceFood;
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
			
			living.kill(new DamageSource(null, null, DamageType.FOOD_POISONING), living.getHealth());
		}
	}
	
	@Override
	public ToStringBuilder toStringBuilder() {
		return super.toStringBuilder()
			.append("source", sourceFood.toStringBuilder());
	}
}
