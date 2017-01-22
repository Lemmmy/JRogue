package pw.lemmmy.jrogue.dungeon.entities.monsters.ai.stateful;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.json.JSONObject;
import pw.lemmmy.jrogue.dungeon.entities.EntityLiving;
import pw.lemmmy.jrogue.dungeon.entities.monsters.Monster;
import pw.lemmmy.jrogue.dungeon.entities.monsters.ai.AI;
import pw.lemmmy.jrogue.dungeon.tiles.TileType;
import pw.lemmmy.jrogue.utils.MultiLineNoPrefixToStringStyle;
import pw.lemmmy.jrogue.utils.Utils;

public class StatefulAI extends AI {
	private AIState defaultState;
	private AIState currentState;
	
	private EntityLiving currentTarget;
	private int targetLastX, targetLastY;
	
	private boolean shouldTargetPlayer = true;
	private int visibilityRange = 15;
	
	public StatefulAI(Monster monster) {
		super(monster);
	}
	
	@Override
	public void update() {
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
	
	public AIState getDefaultState() {
		return defaultState;
	}
	
	public void setDefaultState(AIState defaultState) {
		this.defaultState = defaultState;
	}
	
	public AIState getCurrentState() {
		return currentState;
	}
	
	public void setCurrentState(AIState currentState) {
		this.currentState = currentState;
	}
	
	public EntityLiving getCurrentTarget() {
		return currentTarget;
	}
	
	public void setCurrentTarget(EntityLiving currentTarget) {
		this.currentTarget = currentTarget;
	}
	
	public int getTargetLastX() {
		return targetLastX;
	}
	
	public int getTargetLastY() {
		return targetLastY;
	}
	
	public int getVisibilityRange() {
		return visibilityRange;
	}
	
	public void setVisibilityRange(int visibilityRange) {
		this.visibilityRange = visibilityRange;
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
			
			if (getMonster().getLevel().getTileType(x, y).getSolidity() == TileType.Solidity.SOLID) {
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
		
		JSONObject serialisedDefaultState = new JSONObject();
		defaultState.serialise(serialisedDefaultState);
		obj.put("defaultState", serialisedDefaultState);
		
		JSONObject serialisedCurrentState = new JSONObject();
		currentState.serialise(serialisedCurrentState);
		obj.put("currentState", serialisedCurrentState);
		
		obj.put("currentTarget", currentTarget.getUUID().toString());
		obj.put("targetLastX", targetLastX);
		obj.put("targetLastY", targetLastY);
		
		obj.put("shouldTargetPlayer", shouldTargetPlayer);
		obj.put("visibilityRange", visibilityRange);
	}
	
	@Override
	public void unserialise(JSONObject obj) {
		super.unserialise(obj);
		
		defaultState = AIState.createFromJSON(obj.getJSONObject("defaultState"), this);
		currentState = AIState.createFromJSON(obj.getJSONObject("currentState"), this);
		
		currentTarget = (EntityLiving) getMonster().getLevel().getEntityByUUID(obj.optString("currentTarget"));
		targetLastX = obj.optInt("targetLastX");
		targetLastY = obj.optInt("targetLastY");
		
		shouldTargetPlayer = obj.optBoolean("shouldTargetPlayer", true);
		visibilityRange = obj.optInt("visibilityRange");
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this, MultiLineNoPrefixToStringStyle.STYLE)
			.append(currentState.toString())
			.append("currentTarget", currentTarget.getClass().getSimpleName())
			.toString();
	}
}
