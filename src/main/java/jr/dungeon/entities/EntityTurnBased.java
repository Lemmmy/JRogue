package jr.dungeon.entities;

import com.google.gson.annotations.Expose;
import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.actions.Action;
import jr.utils.RandomUtils;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * An {@link Entity} that gets a chance to run an {@link Action} based on the turn timer. A turn consists of 12
 * ticks, and every Action takes exactly 12 ticks to perform. If the Entity has a speed of 12, they will perform one
 * action every turn. If their speed is lower than 12, their movement points will count down until they have enough
 * to perform the action. For example, an Entity with a speed of 6 will perform 1 action every 2 turns. An Entity
 * with a speed of 24 will perform 2 actions every turn.
 *
 * @see Action
 */
public abstract class EntityTurnBased extends Entity {
	/**
	 * The amount of movement points the Entity currently has free.
	 */
	@Expose @Getter @Setter private int movementPoints = 0;
	/**
	 * The next {@link Action} this Entity is going to perform when it gets a chance to move.
	 */
	private Action nextAction;
	
	/**
	 * @param dungeon The {@link Dungeon} this Entity is part of.
	 * @param level The {@link Level} this Entity is on.
	 * @param x The starting X position of this Entity in the {@link Level}.
	 * @param y The starting Y position of this Entity in the {@link Level}.
	 */
	public EntityTurnBased(Dungeon dungeon, Level level, int x, int y) {
		super(dungeon, level, x, y);
	}
	
	/**
	 * @param action Sets the next action to perform when this Entity gets a chance to move.
	 */
	public void setAction(Action action) {
		nextAction = action;
	}
	
	/**
	 * Unsets the next action, meaning the Entity will do nothing when it next gets a chance to move.
	 */
	public void removeAction() {
		nextAction = null;
	}
	
	/**
	 * Called during the {@link jr.dungeon.TurnSystem#moveEntities() Dungeon's turn loop - performs the next
	 * {@link Action}} if the Entity has one to perform.
	 */
	public void move() {
		if (hasAction()) {
			nextAction.execute(this, getDungeon());
			nextAction = null;
		}
	}
	
	/**
	 * @return true if the Entity's nextAction is null.
	 */
	public boolean hasAction() {
		return nextAction != null;
	}
	
	/**
	 * @return The amount of movement 'ticks' this entity performs per turn.
	 */
	public abstract int getMovementSpeed();
	
	/**
	 * Adds this Entity's movement speed to its stored movementSpeed, with a small random 'bonus'. You can override
	 * this to change the bonus, but make sure at least some bonus is applied otherwise the turn loop will hang in an
	 * infinite loop if the Entity is slowed down.
	 */
	public void applyMovementPoints() {
		movementPoints += getMovementSpeed() + Math.max(1, RandomUtils.random(2, 4));
	}
	
	@Override
	public ToStringBuilder toStringBuilder() {
		return super.toStringBuilder()
			.append("movementPoints", movementPoints)
			.append("nextAction", nextAction);
	}
}
