package jr.dungeon.entities.monsters.ai.stateful.familiar;

import com.google.gson.annotations.Expose;
import jr.dungeon.entities.EntityReference;
import jr.dungeon.entities.actions.Action;
import jr.dungeon.entities.actions.ActionEat;
import jr.dungeon.entities.containers.EntityItem;
import jr.dungeon.entities.monsters.ai.stateful.AIState;
import jr.dungeon.entities.monsters.familiars.Familiar;
import jr.dungeon.items.ItemStack;
import jr.dungeon.items.comestibles.ItemComestible;
import jr.dungeon.serialisation.Registered;
import lombok.AccessLevel;
import lombok.Setter;

@Registered(id="aiStateFamiliarConsumeComestible")
public class StateConsumeComestible extends AIState<FamiliarAI> {
	@Expose @Setter(AccessLevel.NONE)
	private EntityReference<EntityItem> targetComestible = new EntityReference<>();
	
	/**
	 * @param ai The {@link FamiliarAI} that hosts this state.
	 * @param duration How many turns the state should run for. 0 for indefinite.
	 * @param targetComestible The comestible to approach.
	 */
	public StateConsumeComestible(FamiliarAI ai, int duration, EntityItem targetComestible) {
		super(ai, duration);
		this.targetComestible.set(targetComestible);
	}
	
	protected StateConsumeComestible() { super(); }
	
	@Override
	public void update() {
		super.update();
		
		assert ai.getMonster() instanceof Familiar;
		Familiar f = (Familiar) ai.getMonster();
		
		EntityItem target = targetComestible.get(getLevel());
		
		if (
			target == null ||
			target.getLevel() != f.getLevel() ||
			target.getLevel() == null ||
			!f.getLevel().entityStore.hasEntity(target)
		) {
			setTurnsTaken(getDuration());
			ai.setCurrentState(null);
			return;
		}
		
		ItemStack stack = target.getItemStack();
		ItemComestible item = (ItemComestible) target.getItem();
		
		if (stack.getCount() == 1) {
			target.remove();
			targetComestible.unset();
		} else {
			stack.subtractCount(1);
		}
		
		float nutrition = item.getNutrition() * 3;
		
		ai.getMonster().setAction(new ActionEat(item, (Action.CompleteCallback) entity -> {
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
					f.getPosition(),
					new ItemStack(item, 1)
				);
				
				f.getLevel().entityStore.addEntity(newStack);
			}
		}));
	}
}
