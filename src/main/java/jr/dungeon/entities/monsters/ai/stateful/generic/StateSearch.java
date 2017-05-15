package jr.dungeon.entities.monsters.ai.stateful.generic;

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
		
		System.out.println("State search - update 1");
		
		if (getAI().canSeeTarget()) {
			getAI().setCurrentState(new StateApproachTarget(getAI(), 0));
			return;
		}
		
		System.out.println("State search - update 2");
		
		if (getAI().getCurrentTarget() == null || getAI().getTargetLastPos() == null) {
			getAI().setCurrentState(null);
			return;
		}
		
		System.out.println("State search - update 3");
		
		getAI().moveTowards(getAI().getTargetLastPos());
		
		System.out.println("State search - update 4");
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
