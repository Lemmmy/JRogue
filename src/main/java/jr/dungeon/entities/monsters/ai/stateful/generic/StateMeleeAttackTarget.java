package jr.dungeon.entities.monsters.ai.stateful.generic;

import jr.dungeon.entities.monsters.Monster;
import jr.dungeon.entities.monsters.ai.stateful.AIState;
import jr.dungeon.entities.monsters.ai.stateful.StatefulAI;
import jr.utils.RandomUtils;

public class StateMeleeAttackTarget extends AIState<StatefulAI> {
	public StateMeleeAttackTarget(StatefulAI ai, int duration) {
		super(ai, duration);
	}
	
	@Override
	public void update() {
		super.update();
		
		Monster m = getAI().getMonster();
		
		if (getAI().getCurrentTarget() == null || !getAI().getCurrentTarget().isAlive()) {
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
			getAI().moveTowards(getAI().getCurrentTarget().getPosition());
		} else {
			// move next turn
			getAI().setCurrentState(new StateApproachTarget(getAI(), 0));
		}
		
		/*float intrinsicFear = ((TraitIntrinsicFear) getAI().getTrait(TraitIntrinsicFear.class)).getFear();
		float extrinsicFear = ((TraitExtrinsicFear) getAI().getTrait(TraitExtrinsicFear.class)).getFear();
		
		float fear = intrinsicFear + extrinsicFear;
		
		if (
			fear >= 1.0f &&
			RandomUtils.randomFloat() < 0.05f
		) {
			getAI().setCurrentState(new StateFlee(getAI(), RandomUtils.roll(3, 5)));
			
			if (m.getDungeon().getPlayer().getLevel() == m.getLevel()) {
				m.getDungeon().The("%s turns to flee!", m.getName(m.getDungeon().getPlayer(), false));
			}
		}*/
	}
}
