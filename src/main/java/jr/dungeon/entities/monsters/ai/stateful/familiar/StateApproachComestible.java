package jr.dungeon.entities.monsters.ai.stateful.familiar;

import jr.dungeon.entities.containers.EntityItem;
import jr.dungeon.entities.monsters.ai.stateful.AIState;
import jr.utils.Utils;
import org.json.JSONObject;

public class StateApproachComestible extends AIState<FamiliarAI> {
	private EntityItem targetComestible;
	
	/**
	 * @param ai       The {@link FamiliarAI} that hosts this state.
	 * @param duration How many turns the state should run for. 0 for indefinite.
	 * @param targetComestible The comestible to approach.
	 */
	public StateApproachComestible(FamiliarAI ai, int duration, EntityItem targetComestible) {
		super(ai, duration);
		
		this.targetComestible = targetComestible;
	}
	
	@Override
	public void update() {
		super.update();
		
		if (
			targetComestible == null ||
			targetComestible.getLevel() != getAI().getMonster().getLevel() ||
			targetComestible.getLevel() == null ||
			!getAI().getMonster().getLevel().entityStore.hasEntity(targetComestible)
		) {
			getAI().setCurrentState(null);
			return;
		}
		
		if (Utils.chebyshevDistance(targetComestible.getPosition(), getAI().getMonster().getPosition()) <= 1) {
			getAI().setCurrentState(new StateConsumeComestible(getAI(), 3, targetComestible));
			return;
		}
		
		getAI().moveTowards(targetComestible);
		
		if (Utils.chebyshevDistance(targetComestible.getPosition(), getAI().getMonster().getPosition()) <= 1) {
			getAI().setCurrentState(new StateConsumeComestible(getAI(), 3, targetComestible));
			return;
		}
	}
	
	@Override
	public void serialise(JSONObject obj) {
		super.serialise(obj);
		
		obj.put("targetComestible", targetComestible.getUUID().toString());
	}
	
	@Override
	public void unserialise(JSONObject obj) {
		super.unserialise(obj);
		
		if (obj.has("targetComestible")) {
			targetComestible = (EntityItem) getAI().getMonster().getLevel()
				.entityStore.getEntityByUUID(obj.getString("targetComestible"));
		}
	}
}
