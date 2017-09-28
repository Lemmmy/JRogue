package jr.dungeon.entities.monsters.ai.stateful.familiar;

import jr.dungeon.entities.monsters.ai.stateful.AITrait;
import jr.dungeon.entities.monsters.ai.stateful.generic.StateLurk;
import org.json.JSONObject;

public class TraitFollowPartner extends AITrait<FamiliarAI> {
	/**
	 * Intrinsic or extrinsic pieces of information that can affect the way a {@link FamiliarAI} behaves.
	 *
	 * @param ai The {@link FamiliarAI} that hosts this trait.
	 */
	public TraitFollowPartner(FamiliarAI ai) {
		super(ai);
	}
	
	@Override
	public void update() {
		if (getAI().getCurrentState() == null || getAI().getCurrentState().getDuration() == 0) {
			float distance = getAI().distanceFromPlayer();
			
			if (distance > 4) {
				getAI().setCurrentState(new StateFollowPlayer(getAI(), 0));
			} else {
				getAI().setCurrentState(new StateLurk(getAI(), 0));
			}
		}
	}
	
	@Override
	public void serialise(JSONObject obj) {
	
	}
	
	@Override
	public void unserialise(JSONObject obj) {
	
	}
	
	@Override
	public int getPriority() {
		return 0;
	}
}
