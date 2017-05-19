package jr.dungeon.entities.monsters.ai.stateful.generic;

import jr.dungeon.entities.monsters.ai.stateful.AIState;
import jr.dungeon.entities.monsters.ai.stateful.StatefulAI;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class StateSearch extends AIState<StatefulAI> {
	public StateSearch(StatefulAI ai, int duration) {
		super(ai, duration);
	}
	
	@Override
	public void update() {
		super.update();
		
		if (getAI().canSeeTarget()) {
			getAI().setCurrentState(new StateApproachTarget(getAI(), 0));
			return;
		}
		
		if (getAI().getCurrentTarget() == null || getAI().getTargetLastPos() == null) {
			getAI().setCurrentState(null);
			return;
		}
		
		getAI().moveTowards(getAI().getTargetLastPos());
	}
	
	@Override
	public ToStringBuilder toStringBuilder() {
		return super.toStringBuilder()
			.append("dest", getAI().getTargetLastPos());
	}
}
