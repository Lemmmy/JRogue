package pw.lemmmy.jrogue.dungeon.entities.monsters.ai.stateful.humanoid;

import pw.lemmmy.jrogue.dungeon.entities.monsters.ai.stateful.AIState;
import pw.lemmmy.jrogue.dungeon.entities.monsters.ai.stateful.StatefulAI;

public class StateApproachTarget extends AIState {
	public StateApproachTarget(StatefulAI ai, int duration) {
		super(ai, duration);
	}
	
	@Override
	public void update() {
		super.update();
		
		if (getAI().getCurrentTarget() == null || !getAI().canSeeTarget()) {
			getAI().setCurrentState(null); // TODO: search state
			return;
		}
		
		int destX = getAI().getCurrentTarget().getX();
		int destY = getAI().getCurrentTarget().getY();
		
		getAI().moveTowards(destX, destY);
	}
}
