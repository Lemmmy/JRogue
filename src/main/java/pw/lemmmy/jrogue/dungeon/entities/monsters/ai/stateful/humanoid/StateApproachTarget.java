package pw.lemmmy.jrogue.dungeon.entities.monsters.ai.stateful.humanoid;

import pw.lemmmy.jrogue.dungeon.entities.monsters.ai.stateful.AIState;
import pw.lemmmy.jrogue.dungeon.entities.monsters.ai.stateful.StatefulAI;
import pw.lemmmy.jrogue.utils.RandomUtils;

public class StateApproachTarget extends AIState {
	public StateApproachTarget(StatefulAI ai, int duration) {
		super(ai, duration);
	}
	
	@Override
	public void update() {
		super.update();
		
		if (getAI().getCurrentTarget() == null) {
			getAI().setCurrentState(null);
			return;
		}
		
		if (!getAI().canSeeTarget()) {
			getAI().setCurrentState(new StateSearch(getAI(), RandomUtils.random(4, 7)));
			return;
		}
				
		if (getAI().canMeleeAttack(getAI().getCurrentTarget())) {
			getAI().setCurrentState(new StateMeleeAttackTarget(getAI(), 3));
		} else {
			int destX = getAI().getCurrentTarget().getX();
			int destY = getAI().getCurrentTarget().getY();
			
			getAI().moveTowards(destX, destY);
		}
	}
}
