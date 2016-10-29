package pw.lemmmy.jrogue.dungeon.entities;

import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.entities.effects.StatusEffect;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class Entity {
	private int lastX;
	private int lastY;

	private int x;
	private int y;

	private Dungeon dungeon;
	private Level level;

	private List<StatusEffect> statusEffects = new ArrayList<>();

	public Entity(Dungeon dungeon, Level level, int x, int y) {
		this.dungeon = dungeon;
		this.level = level;
		this.x = x;
		this.y = y;
		this.lastX = x;
		this.lastY = y;
	}

	public abstract String getName(boolean requiresCapitalisation);

	public abstract EntityAppearance getAppearance();

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

	public int getLastX() {
		return lastX;
	}

	public void setLastX(int lastX) {
		this.lastX = lastX;
	}

	public int getLastY() {
		return lastY;
	}

	public void setLastY(int lastY) {
		this.lastY = lastY;
	}

	public void setPosition(int x, int y) {
		setLastX(getX());
		setLastY(getY());
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

	protected abstract void onKick(LivingEntity kicker, boolean isPlayer, int x, int y);

	protected abstract void onWalk(LivingEntity walker, boolean isPlayer);

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

	public void kick(LivingEntity kicker, boolean isPlayer, int x, int y) {
		onKick(kicker, isPlayer, x, y);
	}

	public void walk(LivingEntity walker, boolean isPlayer) {
		onWalk(walker, isPlayer);
	}

	public abstract boolean canBeWalkedOn();
}
