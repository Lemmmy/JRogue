package jr.dungeon.entities.monsters.ai.stateful.generic;

import com.google.gson.annotations.Expose;
import jr.dungeon.entities.monsters.ai.stateful.AIState;
import jr.dungeon.entities.monsters.ai.stateful.StatefulAI;
import jr.utils.RandomUtils;
import lombok.Getter;
import lombok.Setter;

public class StateApproachTarget extends AIState<StatefulAI> {
	// TODO: these are remnants from pre-gson; they used to be stored in persistence, however they are never actually set.
	//       they are probably not needed, or could be moved to constructors or something
	@Expose @Getter @Setter private int minSearchDuration = 4;
	@Expose @Getter @Setter private int maxSearchDuration = 7;
	@Expose @Getter @Setter private int meleeAttackDuration = 3;
	
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
			getAI().setCurrentState(new StateSearch(getAI(), RandomUtils.random(minSearchDuration, maxSearchDuration)));
			return;
		}
				
		if (getAI().canMeleeAttack(getAI().getCurrentTarget())) {
			getAI().setCurrentState(new StateMeleeAttackTarget(getAI(), meleeAttackDuration));
		} else {
			int destX = getAI().getCurrentTarget().getX();
			int destY = getAI().getCurrentTarget().getY();
			
			getAI().moveTowards(destX, destY);
		}
	}
}
