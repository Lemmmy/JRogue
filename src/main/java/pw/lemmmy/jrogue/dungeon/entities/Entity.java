package pw.lemmmy.jrogue.dungeon.entities;

import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.entities.actions.EntityAction;
import pw.lemmmy.jrogue.dungeon.entities.effects.StatusEffect;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class Entity {
	private int x;
	private int y;

	private Dungeon dungeon;
	private Level level;

	private int movementPoints = 0;
	private EntityAction nextAction;

	private List<StatusEffect> statusEffects = new ArrayList<>();

	public Entity(Dungeon dungeon, Level level, int x, int y) {
		this.dungeon = dungeon;
		this.level = level;
		this.x = x;
		this.y = y;
	}

	public abstract String getName();

	public abstract Appearance getAppearance();

	public void setAction(EntityAction action) {
		nextAction = action;
	}

	public boolean hasAction() {
		return nextAction != null;
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

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void setPosition(int x, int y) {
		setX(x);
		setY(y);
	}

	public Dungeon getDungeon() {
		return dungeon;
	}

	public Level getLevel() {
		return level;
	}

	public void setLevel(Level level) {
		this.level = level;
	}

	protected abstract void onKick(Entity kicker);

	public void update() {
		for (Iterator<StatusEffect> iterator = statusEffects.iterator(); iterator.hasNext(); ) {
			StatusEffect statusEffect = iterator.next();

			statusEffect.turn();

			if (statusEffect.getTurnsPassed() >= statusEffect.getDuration()) {
				statusEffect.onEnd();
				iterator.remove();
			}
		}
	}

	public void addStatusEffect(StatusEffect effect) {
		statusEffects.add(effect);
	}

	public boolean hasStatusEffect(Class<? extends StatusEffect> statusEffect) {
		return statusEffects.stream().filter(statusEffect::isInstance).findFirst().isPresent();
	}

	public List<StatusEffect> getStatusEffects() {
		return statusEffects;
	}

	public abstract int getMovementSpeed();
}
