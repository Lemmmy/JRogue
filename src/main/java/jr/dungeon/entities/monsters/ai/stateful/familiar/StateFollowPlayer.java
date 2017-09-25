package jr.dungeon.entities.monsters.ai.stateful.familiar;

import jr.dungeon.entities.monsters.ai.stateful.AIState;

public class StateFollowPlayer extends AIState<FamiliarAI> {
	public StateFollowPlayer(FamiliarAI ai, int duration) {
		super(ai, duration);
	}
	
	@Override
	public void update() {
		super.update();
		
		getAI().moveTowardsPlayer();
	}
}
