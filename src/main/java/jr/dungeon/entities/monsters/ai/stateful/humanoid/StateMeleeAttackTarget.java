package jr.dungeon.entities.monsters.ai.stateful.humanoid;

import jr.dungeon.entities.monsters.ai.stateful.StatefulAI;
import jr.dungeon.entities.monsters.ai.stateful.AIState;
import jr.utils.RandomUtils;

public class StateMeleeAttackTarget extends AIState {
	public StateMeleeAttackTarget(StatefulAI ai, int duration) {
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
			getAI().meleeAttack(getAI().getCurrentTarget());
		} else if (RandomUtils.rollD2()) {
			// use this turn to move
			
			int destX = getAI().getCurrentTarget().getX();
			int destY = getAI().getCurrentTarget().getY();
			
			getAI().moveTowards(destX, destY);
		} else {
			// move next turn
			
			getAI().setCurrentState(new StateApproachTarget(getAI(), 0));
		}
	}
}
