package jr.dungeon.entities.monsters.ai.stateful.generic;

import jr.dungeon.entities.monsters.ai.stateful.AITrait;
import jr.dungeon.entities.monsters.ai.stateful.StatefulAI;
import org.json.JSONObject;

public class TraitBewareTarget extends AITrait<StatefulAI> {
	/**
	 * Intrinsic or extrinsic pieces of information that can affect the way a {@link StatefulAI} behaves.
	 *
	 * @param ai The {@link StatefulAI} that hosts this trait.
	 */
	public TraitBewareTarget(StatefulAI ai) {
		super(ai);
	}
	
	@Override
	public void update() {
		if (
			(getAI().getCurrentState() == null || getAI().getCurrentState().getDuration() == 0) &&
			getAI().canSeeTarget()
		) {
			getAI().setCurrentState(new StateApproachTarget(getAI(), 0));
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
		return 20;
	}
}
