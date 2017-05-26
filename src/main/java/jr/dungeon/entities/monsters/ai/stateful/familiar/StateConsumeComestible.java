package jr.dungeon.entities.monsters.ai.stateful.familiar;

import jr.dungeon.entities.Entity;
import jr.dungeon.entities.actions.Action;
import jr.dungeon.entities.actions.ActionEat;
import jr.dungeon.entities.containers.EntityItem;
import jr.dungeon.entities.monsters.Monster;
import jr.dungeon.entities.monsters.ai.stateful.AIState;
import jr.dungeon.entities.monsters.familiars.Familiar;
import jr.dungeon.items.ItemStack;
import jr.dungeon.items.comestibles.ItemComestible;
import org.json.JSONObject;

public class StateConsumeComestible extends AIState<FamiliarAI> {
	private EntityItem targetComestible;
	
	/**
	 * @param ai       The {@link FamiliarAI} that hosts this state.
	 * @param duration How many turns the state should run for. 0 for indefinite.
	 * @param targetComestible The comestible to approach.
	 */
	public StateConsumeComestible(FamiliarAI ai, int duration, EntityItem targetComestible) {
		super(ai, duration);
		
		this.targetComestible = targetComestible;
	}
	
	@Override
	public void update() {
		super.update();
		
		assert getAI().getMonster() instanceof Familiar;
		Familiar f = (Familiar) getAI().getMonster();
		
		if (
			targetComestible == null ||
			targetComestible.getLevel() != f.getLevel() ||
			targetComestible.getLevel() == null ||
			!f.getLevel().entityStore.hasEntity(targetComestible)
		) {
			setTurnsTaken(getDuration());
			getAI().setCurrentState(null);
			return;
		}
		
		ItemStack stack = targetComestible.getItemStack();
		ItemComestible item = (ItemComestible) targetComestible.getItem();
		
		if (stack.getCount() == 1) {
			targetComestible.getLevel().entityStore.removeEntity(targetComestible);
		} else {
			stack.subtractCount(1);
		}
		
		float nutrition = item.getNutrition() * 3;
		
		getAI().getMonster().setAction(new ActionEat(item, (Action.CompleteCallback) entity -> {
			if (item.getTurnsRequiredToEat() == 1) {
				f.setNutrition(f.getNutrition() + nutrition);
			} else if (item.getEatenState() != ItemComestible.EatenState.EATEN) {
				if (item.getTurnsEaten() == item.getTurnsRequiredToEat() - 1) {
					f.setNutrition((int) (f.getNutrition() + Math.ceil(nutrition / item.getTurnsRequiredToEat())));
				} else {
					f.setNutrition((int) (f.getNutrition() + Math.floor(nutrition / item.getTurnsRequiredToEat())));
				}
			}
			
			item.eatPart();
			
			if (item.getEatenState() != ItemComestible.EatenState.EATEN) {
				EntityItem newStack = new EntityItem(
					f.getDungeon(),
					f.getLevel(),
					f.getX(),
					f.getY(),
					new ItemStack(item, 1)
				);
				
				f.getLevel().entityStore.addEntity(newStack);
			}
		}));
	}
	
	@Override
	public void serialise(JSONObject obj) {
		super.serialise(obj);
		
		obj.put("targetComestible", targetComestible.getUUID().toString());
	}
	
	@Override
	public void unserialise(JSONObject obj) {
		super.unserialise(obj);
		
		if (obj.has("targetComestible")) {
			targetComestible = (EntityItem) getAI().getMonster().getLevel()
				.entityStore.getEntityByUUID(obj.getString("targetComestible"));
		}
	}
}
