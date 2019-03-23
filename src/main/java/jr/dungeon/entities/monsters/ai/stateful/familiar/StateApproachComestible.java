package jr.dungeon.entities.monsters.ai.stateful.familiar;

import com.google.gson.annotations.Expose;
import jr.dungeon.entities.EntityReference;
import jr.dungeon.entities.containers.EntityItem;
import jr.dungeon.entities.monsters.ai.stateful.AIState;
import jr.utils.Utils;

public class StateApproachComestible extends AIState<FamiliarAI> {
	@Expose private EntityReference<EntityItem> targetComestible = new EntityReference<>();
	
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
	public void update() {
		super.update();
		
		EntityItem target = targetComestible.get(getAI().getLevel());
		
		if (
			target == null ||
			target.getLevel() != getAI().getMonster().getLevel() ||
			target.getLevel() == null ||
			!getAI().getMonster().getLevel().entityStore.hasEntity(target)
		) {
			getAI().setCurrentState(null);
			return;
		}
		
		if (Utils.chebyshevDistance(target.getPosition(), getAI().getMonster().getPosition()) <= 1) {
			getAI().setCurrentState(new StateConsumeComestible(getAI(), 3, target));
			return;
		}
		
		getAI().moveTowards(target);
		
		if (Utils.chebyshevDistance(target.getPosition(), getAI().getMonster().getPosition()) <= 1) {
			getAI().setCurrentState(new StateConsumeComestible(getAI(), 3, target));
		}
	}
}
