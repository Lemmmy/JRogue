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

	private int actionPoints = 0;
	private List<EntityAction> actionQueue = new ArrayList<>();
	private List<EntityAction> actionExecuteQueue = new ArrayList<>();

	private List<StatusEffect> statusEffects = new ArrayList<>();

	public Entity(Dungeon dungeon, Level level, int x, int y) {
		this.dungeon = dungeon;
		this.level = level;
		this.x = x;
		this.y = y;
	}

	public abstract String getName();

	public abstract Appearance getAppearance();

	public int getActionPoints() {
		return actionPoints;
	}

	protected void addActionPoints(int points) {
		actionPoints += points;

		if (actionPoints >= 12) {
			for (int i = 0; i < Math.floor(actionPoints / 12); i++) {
				actionExecuteQueue.add(actionQueue.remove(0));
			}

			actionPoints = actionPoints % 12;
		}
	}

	public void move() {
		if (actionExecuteQueue.size() <= 0) {
			return;
		}

		actionExecuteQueue.get(0).execute();
		actionExecuteQueue.clear();

		actionPoints = 0;
	}

	public void addAction(EntityAction action) {
		actionQueue.add(action);
		addActionPoints(action.getTurnsRequired());
	}

	public boolean hasQueuedAction() {
		return actionQueue.size() > 0;
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
}
