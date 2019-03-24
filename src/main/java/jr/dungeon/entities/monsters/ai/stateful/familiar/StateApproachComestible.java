package jr.dungeon.entities.monsters.ai.stateful.familiar;

import com.google.gson.annotations.Expose;
import jr.dungeon.entities.EntityReference;
import jr.dungeon.entities.containers.EntityItem;
import jr.dungeon.entities.monsters.ai.stateful.AIState;
import jr.dungeon.serialisation.Registered;
import jr.utils.Utils;
import lombok.AccessLevel;
import lombok.Setter;

@Registered(id="aiStateFamiliarApproachComestible")
public class StateApproachComestible extends AIState<FamiliarAI> {
	@Expose @Setter(AccessLevel.NONE)
	private EntityReference<EntityItem> targetComestible = new EntityReference<>();
	
	/**
	 * @param ai       The {@link FamiliarAI} that hosts this state.
	 * @param duration How many turns the state should run for. 0 for indefinite.
	 * @param targetComestible The comestible to approach.
	 */
	public StateApproachComestible(FamiliarAI ai, int duration, EntityItem targetComestible) {
		super(ai, duration);
		
		this.targetComestible.set(targetComestible);
	}
	
	@Override
	public void afterDeserialise() {
		super.afterDeserialise();
		if (targetComestible == null) targetComestible = new EntityReference<>();
	}
	
	@Override
	public void update() {
		super.update();
		
		EntityItem target = targetComestible.get(getLevel());
		
		if (
			target == null ||
			target.getLevel() != getLevel() ||
			target.getLevel() == null ||
			!getLevel().entityStore.hasEntity(target)
		) {
			ai.setCurrentState(null);
			return;
		}
		
		if (Utils.chebyshevDistance(target.getPosition(), ai.getMonster().getPosition()) <= 1) {
			ai.setCurrentState(new StateConsumeComestible(ai, 3, target));
			return;
		}
		
		ai.moveTowards(target);
		
		if (Utils.chebyshevDistance(target.getPosition(), ai.getMonster().getPosition()) <= 1) {
			ai.setCurrentState(new StateConsumeComestible(ai, 3, target));
		}
	}
}
