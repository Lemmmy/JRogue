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
			int minSearchDuration = getAI().getMonster().getPersistence().optInt("minSearchDuration", 4);
			int maxSearchDuration = getAI().getMonster().getPersistence().optInt("maxSearchDuration", 7);
			
			getAI().setCurrentState(new StateSearch(getAI(), RandomUtils.random(minSearchDuration, maxSearchDuration)));
			return;
		}
				
		if (getAI().canMeleeAttack(getAI().getCurrentTarget())) {
			int meleeAttackDuration = getAI().getMonster().getPersistence().optInt("meleeAttackDuration", 3);
			
			getAI().setCurrentState(new StateMeleeAttackTarget(getAI(), meleeAttackDuration));
		} else {
			int destX = getAI().getCurrentTarget().getX();
			int destY = getAI().getCurrentTarget().getY();
			
			getAI().moveTowards(destX, destY);
		}
	}
}
