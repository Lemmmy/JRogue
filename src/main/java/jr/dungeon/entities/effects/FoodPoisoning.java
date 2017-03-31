package jr.dungeon.entities.effects;

import jr.dungeon.Messenger;
import jr.dungeon.entities.DamageSource;
import jr.dungeon.entities.DamageSourceType;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.items.Item;
import jr.dungeon.items.comestibles.ItemComestible;
import jr.utils.RandomUtils;
import org.json.JSONObject;

public class FoodPoisoning extends StatusEffect {
	private ItemComestible sourceFood;
	
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
			
			living.kill(new DamageSource(null, null, DamageSourceType.FOOD_POISONING), living.getHealth());
		}
	}
	
	@Override
	public void serialise(JSONObject obj) {
		super.serialise(obj);
		
		obj.put("sourceFood", sourceFood);
	}
	
	@Override
	public void unserialise(JSONObject obj) {
		super.unserialise(obj);
		
		if (obj.has("sourceFood")) {
			Item.createFromJSON(obj.getJSONObject("sourceFood")).ifPresent(i -> sourceFood = (ItemComestible) i);
		}
	}
}
