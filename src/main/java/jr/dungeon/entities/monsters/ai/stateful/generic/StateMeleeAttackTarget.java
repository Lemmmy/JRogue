package jr.dungeon.entities.monsters.ai.stateful.generic;

import jr.dungeon.entities.monsters.Monster;
import jr.dungeon.entities.monsters.ai.stateful.AIState;
import jr.dungeon.entities.monsters.ai.stateful.StatefulAI;
import jr.dungeon.serialisation.Registered;
import jr.utils.RandomUtils;

@Registered(id="aiStateMeleeAttackTarget")
public class StateMeleeAttackTarget extends AIState<StatefulAI> {
	public StateMeleeAttackTarget(StatefulAI ai, int duration) {
		super(ai, duration);
	}
	
	@Override
	public void update() {
		super.update();
		
		Monster m = ai.getMonster();
		
		if (ai.getCurrentTarget() == null || !ai.getCurrentTarget().get(getLevel()).isAlive()) {
			ai.setCurrentState(null);
			return;
		}
		
		if (!ai.canSeeTarget()) {
			ai.setCurrentState(new StateSearch(ai, RandomUtils.random(4, 7)));
			return;
		}
		
		if (ai.canMeleeAttack(ai.getCurrentTarget().get(getLevel()))) {
			ai.meleeAttack(ai.getCurrentTarget().get(getLevel()));
		} else if (RandomUtils.rollD2()) {
			// use this turn to move
			ai.moveTowards(ai.getCurrentTarget());
		} else {
			// move next turn
			ai.setCurrentState(new StateApproachTarget(ai, 0));
		}
		
		/*float intrinsicFear = ((TraitIntrinsicFear) getAI().getTrait(TraitIntrinsicFear.class)).getFear();
		float extrinsicFear = ((TraitExtrinsicFear) getAI().getTrait(TraitExtrinsicFear.class)).getFear();
		
		float fear = intrinsicFear + extrinsicFear;
		
		if (
			fear >= 1.0f &&
			RandomUtils.randomFloat() < 0.05f
		) {
			getAI().setCurrentState(new StateFlee(getAI(), RandomUtils.roll(3, 5)));
			
			if (m.getDungeon().getPlayer().getLevel() == getLevel()) {
				m.getDungeon().The("%s turns to flee!", m.getName(m.getDungeon().getPlayer(), false));
			}
		}*/
	}
}
