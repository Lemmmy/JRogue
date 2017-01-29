package jr.dungeon.entities.monsters.ai.stateful;

import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.monsters.Monster;
import jr.dungeon.entities.monsters.ai.AI;
import jr.dungeon.events.DungeonEventListener;
import jr.dungeon.tiles.TileType;
import jr.utils.MultiLineNoPrefixToStringStyle;
import jr.utils.Utils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class StatefulAI extends AI {
	private AIState defaultState;
	private AIState currentState;
	
	private EntityLiving currentTarget;
	private int targetLastX, targetLastY;
	
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private boolean shouldTargetPlayer = true;
	private int visibilityRange = 15;
	
	private Map<Class<? extends AITrait>, AITrait> traits = new HashMap<>();
	
	public StatefulAI(Monster monster) {
		super(monster);
	}
	
	@Override
	public void update() {
		if (getMonster() == null) {
			return;
		}
		
		if (shouldTargetPlayer && currentTarget == null) {
			currentTarget = getMonster().getDungeon().getPlayer();
		}
		
		if (currentState == null) {
			currentState = defaultState;
		}
		
		if (currentState != null) {
			currentState.update();
		}
				
		if (
			currentState != null &&
			currentState.getDuration() > 0 &&
			currentState.getTurnsTaken() >= currentState.getDuration()
		) {
			currentState = null;
		}
		
		updateTargetVisibility();
	}
	
	public boolean shouldTargetPlayer() {
		return shouldTargetPlayer;
	}
	
	public void setShouldTargetPlayer(boolean shouldTargetPlayer) {
		this.shouldTargetPlayer = shouldTargetPlayer;
	}
	
	public boolean canSeeTarget() {
		if (getCurrentTarget() == null) {
			return false;
		}
		
		int startX = getMonster().getX();
		int startY = getMonster().getY();
		int endX = getCurrentTarget().getX();
		int endY = getCurrentTarget().getY();
		
		int preDistance = Utils.distance(startX, startY, endX, endY);
		
		if (preDistance > getVisibilityRange()) {
			return false;
		}
		
		float diffX = endX - startX;
		float diffY = endY - startY;
		
		float dist = Math.abs(diffX + diffY);
		
		float dx = diffX / dist;
		float dy = diffY / dist;
		
		for (int i = 0; i <= Math.ceil(dist); i++) {
			int x = Math.round(startX + dx * i);
			int y = Math.round(startY + dy * i);
			
			if (getMonster().getLevel().getTileStore().getTileType(x, y).getSolidity() == TileType.Solidity.SOLID) {
				return false;
			}
		}
		
		return true;
	}
	
	public void updateTargetVisibility() {
		if (getCurrentTarget() == null) {
			return;
		}
		
		if (canSeeTarget()) {
			targetLastX = getCurrentTarget().getX();
			targetLastY = getCurrentTarget().getY();
		}
	}
	
	@Override
	public void serialise(JSONObject obj) {
		super.serialise(obj);
		
		if (getMonster() == null) {
			return;
		}
		
		if (defaultState != null) {
			JSONObject serialisedDefaultState = new JSONObject();
			defaultState.serialise(serialisedDefaultState);
			obj.put("defaultState", serialisedDefaultState);
		}
		
		if (currentState != null) {
			JSONObject serialisedCurrentState = new JSONObject();
			currentState.serialise(serialisedCurrentState);
			obj.put("currentState", serialisedCurrentState);
		}
		
		if (currentTarget != null) {
			obj.put("currentTarget", currentTarget.getUUID().toString());
			obj.put("targetLastX", targetLastX);
			obj.put("targetLastY", targetLastY);
		}
		
		obj.put("shouldTargetPlayer", shouldTargetPlayer);
		obj.put("visibilityRange", visibilityRange);
		
		JSONObject serialisedTraits = new JSONObject();
		
		traits.forEach((c, t) -> {
			JSONObject serialisedTrait = new JSONObject();
			t.serialise(serialisedTrait);
			serialisedTraits.put(c.getName(), serialisedTrait);
		});
		
		obj.put("traits", serialisedTraits);
	}
	
	@Override
	public void unserialise(JSONObject obj) {
		super.unserialise(obj);
		
		if (getMonster() == null || getMonster().getLevel() == null) {
			return;
		}
		
		defaultState = AIState.createFromJSON(obj.getJSONObject("defaultState"), this);
		currentState = AIState.createFromJSON(obj.getJSONObject("currentState"), this);
		
		if (obj.has("currentTarget")) {
			currentTarget = (EntityLiving) getMonster().getLevel().getEntityStore().getEntityByUUID(obj.optString("currentTarget"));
			targetLastX = obj.optInt("targetLastX");
			targetLastY = obj.optInt("targetLastY");
		}
		
		shouldTargetPlayer = obj.optBoolean("shouldTargetPlayer", true);
		visibilityRange = obj.optInt("visibilityRange");
		
		if (obj.has("traits")) {
			JSONObject serialisedTraits = obj.getJSONObject("traits");
			
			serialisedTraits.keySet().forEach(traitClassName -> {
				JSONObject serialisedTrait = serialisedTraits.getJSONObject(traitClassName);
				AITrait unserialisedTrait = AITrait.createFromJSON(traitClassName, serialisedTrait, this);
				assert unserialisedTrait != null;
				traits.put(unserialisedTrait.getClass(), unserialisedTrait);
			});
		}
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this, MultiLineNoPrefixToStringStyle.STYLE)
			.append(currentState == null ? "no state" : currentState.toString())
			.append("currentTarget", currentTarget == null ? "no target" : currentTarget.getClass().getSimpleName())
			.toString();
	}
	
	@Override
	public List<DungeonEventListener> getSubListeners() {
		val subListeners = super.getSubListeners();
		
		subListeners.add(currentState);
		subListeners.add(defaultState);
		
		traits.values().forEach(subListeners::add);
		
		return subListeners;
	}
}
