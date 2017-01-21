package pw.lemmmy.jrogue.dungeon.entities.monsters.ai.stateful.humanoid;

import pw.lemmmy.jrogue.dungeon.entities.monsters.ai.stateful.AIState;
import pw.lemmmy.jrogue.dungeon.entities.monsters.ai.stateful.StatefulAI;

public class StateLurk extends AIState {
	public StateLurk(StatefulAI ai, int duration) {
		super(ai, duration);
	}
	
	@Override
	public void update() {
		super.update();
		
		if (getAI().canSeeTarget()) {
			getAI().setCurrentState(new StateApproachTarget(getAI(), 0));
			return;
		}
	}
}
