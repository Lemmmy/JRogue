package jr.dungeon.entities.monsters.ai.stateful.humanoid;

import jr.dungeon.entities.monsters.ai.stateful.AIState;
import jr.dungeon.entities.monsters.ai.stateful.StatefulAI;
import jr.dungeon.entities.monsters.ai.stateful.generic.StateApproachTarget;
import jr.utils.MultiLineNoPrefixToStringStyle;
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
		
		if (getAI().getCurrentTarget() == null) {
			getAI().setCurrentState(null);
			return;
		}
		
		getAI().moveTowards(getAI().getTargetLastPos());
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this, MultiLineNoPrefixToStringStyle.STYLE)
			.append("duration", getDuration())
			.append("turnsTaken", getTurnsTaken())
			.append("dest", getAI().getTargetLastPos())
			.toString();
	}
}
