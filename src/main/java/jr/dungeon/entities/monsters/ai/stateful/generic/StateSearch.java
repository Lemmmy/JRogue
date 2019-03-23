package jr.dungeon.entities.monsters.ai.stateful.generic;

import jr.dungeon.entities.monsters.ai.stateful.AIState;
import jr.dungeon.entities.monsters.ai.stateful.StatefulAI;
import jr.dungeon.serialisation.Registered;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Registered(id="aiStateSearch")
public class StateSearch extends AIState<StatefulAI> {
	public StateSearch(StatefulAI ai, int duration) {
		super(ai, duration);
	}
	
	@Override
	public void update() {
		super.update();
		
		if (ai.canSeeTarget()) {
			ai.setCurrentState(new StateApproachTarget(ai, 0));
			return;
		}
		
		if (ai.getCurrentTarget() == null || ai.getTargetLastPos() == null) {
			ai.setCurrentState(null);
			return;
		}
		
		ai.moveTowards(ai.getTargetLastPos());
	}
	
	@Override
	public ToStringBuilder toStringBuilder() {
		return super.toStringBuilder()
			.append("dest", ai.getTargetLastPos());
	}
}
