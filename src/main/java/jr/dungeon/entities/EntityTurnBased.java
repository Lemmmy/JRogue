package jr.dungeon.entities;

import org.json.JSONObject;
import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.actions.EntityAction;
import jr.utils.RandomUtils;

public abstract class EntityTurnBased extends Entity {
	private int movementPoints = 0;
	private EntityAction nextAction;
	
	public EntityTurnBased(Dungeon dungeon, Level level, int x, int y) {
		super(dungeon, level, x, y);
	}
	
	@Override
	public void serialise(JSONObject obj) {
		super.serialise(obj);
		
		obj.put("movementPoints", movementPoints);
	}
	
	@Override
	public void unserialise(JSONObject obj) {
		super.unserialise(obj);
		
		movementPoints = obj.getInt("movementPoints");
	}
	
	public void setAction(EntityAction action) {
		nextAction = action;
	}
	
	public void removeAction() {
		nextAction = null;
	}
	
	public void move() {
		if (hasAction()) {
			nextAction.execute(this, getDungeon());
			nextAction = null;
		}
	}
	
	public boolean hasAction() {
		return nextAction != null;
	}
	
	public int getMovementPoints() {
		return movementPoints;
	}
	
	public void setMovementPoints(int movementPoints) {
		this.movementPoints = movementPoints;
	}
	
	public abstract int getMovementSpeed();
	
	public void applyMovementPoints() {
		movementPoints += getMovementSpeed() + Math.max(1, RandomUtils.random(2, 4));
	}
}
