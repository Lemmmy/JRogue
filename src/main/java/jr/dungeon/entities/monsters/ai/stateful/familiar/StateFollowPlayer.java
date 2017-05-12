package jr.dungeon.entities.monsters.ai.stateful.familiar;

import jr.dungeon.entities.monsters.ai.stateful.AIState;
import jr.dungeon.entities.monsters.ai.stateful.StatefulAI;

public class StateFollowPlayer extends AIState {
	public StateFollowPlayer(StatefulAI ai, int duration) {
		super(ai, duration);
	}
	
	@Override
	public void update() {
		super.update();
		
		if (getAI().distanceFromPlayer() < 4) {
			getAI().setCurrentState(new StateLurk(getAI(), 0));
			return;
		}
		
		getAI().moveTowardsPlayer();
	}
}
