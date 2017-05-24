package jr.dungeon.entities.monsters.ai.stateful.familiar;

import jr.dungeon.entities.containers.EntityItem;
import jr.dungeon.entities.monsters.ai.stateful.AIState;
import org.json.JSONObject;

public class StateConsumeComestible extends AIState<FamiliarAI> {
	private EntityItem targetComestible;
	
	/**
	 * @param ai       The {@link FamiliarAI} that hosts this state.
	 * @param duration How many turns the state should run for. 0 for indefinite.
	 * @param targetComestible The comestible to approach.
	 */
	public StateConsumeComestible(FamiliarAI ai, int duration, EntityItem targetComestible) {
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
