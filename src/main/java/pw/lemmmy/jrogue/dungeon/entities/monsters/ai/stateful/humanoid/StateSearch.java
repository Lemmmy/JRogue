package pw.lemmmy.jrogue.dungeon.entities.monsters.ai.stateful.humanoid;

import pw.lemmmy.jrogue.dungeon.entities.monsters.ai.stateful.AIState;
import pw.lemmmy.jrogue.dungeon.entities.monsters.ai.stateful.StatefulAI;

public class StateSearch extends AIState {
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
		
		int destX = getAI().getTargetLastX();
		int destY = getAI().getTargetLastY();
		
		getAI().moveTowards(destX, destY);
	}
}
