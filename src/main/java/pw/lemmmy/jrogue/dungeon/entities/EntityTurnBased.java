package pw.lemmmy.jrogue.dungeon.entities;

import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.entities.actions.EntityAction;

public abstract class EntityTurnBased extends Entity {
	private int movementPoints = 0;
	private EntityAction nextAction;

	public EntityTurnBased(Dungeon dungeon, Level level, int x, int y) {
		super(dungeon, level, x, y);
	}

	public void setAction(EntityAction action) {
		nextAction = action;
	}

	public boolean hasAction() {
		return nextAction != null;
	}

	public void removeAction() {
		nextAction = null;
	}

	public void move() {
		if (hasAction()) {
			nextAction.execute();
			nextAction = null;
		}
	}

	public int getMovementPoints() {
		return movementPoints;
	}

	public void setMovementPoints(int movementPoints) {
		this.movementPoints = movementPoints;
	}

	public abstract int getMovementSpeed();
}
