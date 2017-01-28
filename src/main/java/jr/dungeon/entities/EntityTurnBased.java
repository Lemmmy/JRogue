package jr.dungeon.entities;

import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.actions.EntityAction;
import jr.utils.RandomUtils;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

public abstract class EntityTurnBased extends Entity {
	@Getter @Setter private int movementPoints = 0;
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
	
	public abstract int getMovementSpeed();
	
	public void applyMovementPoints() {
		movementPoints += getMovementSpeed() + Math.max(1, RandomUtils.random(2, 4));
	}
}
