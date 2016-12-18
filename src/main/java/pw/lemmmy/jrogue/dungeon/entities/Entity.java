package pw.lemmmy.jrogue.dungeon.entities;

import org.json.JSONArray;
import org.json.JSONObject;
import pw.lemmmy.jrogue.JRogue;
import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.entities.effects.StatusEffect;
import pw.lemmmy.jrogue.utils.Utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public abstract class Entity {
	private int lastX;
	private int lastY;

	private int x;
	private int y;

	private int visualID;

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

		this.visualID = Utils.random(1000);
	}

	public int getVisualID() {
		return visualID;
	}

	public abstract String getName(boolean requiresCapitalisation);

	public abstract EntityAppearance getAppearance();

	public void setPosition(int x, int y) {
		setLastX(getX());
		setLastY(getY());
		setX(x);
		setY(y);

		dungeon.entityMoved(this, getLastX(), getLastY(), x, y);
	}

	public int getX() {
		return x;
	}

	private void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	private void setY(int y) {
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

	public int getDepth() {
		return 1;
	}

	public Optional<Container> getContainer() {
		return Optional.empty();
	}

	public boolean canContainerBeOpened() {
		return false;
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

	public void update() {
		for (Iterator<StatusEffect> iterator = statusEffects.iterator(); iterator.hasNext(); ) {
			StatusEffect statusEffect = iterator.next();

			statusEffect.turn();

			if (statusEffect.getDuration() >= 0 && statusEffect.getTurnsPassed() >= statusEffect.getDuration()) {
				statusEffect.onEnd();
				iterator.remove();
			}
		}
	}

	public void serialise(JSONObject obj) {
		obj.put("class", getClass().getName());
		obj.put("x", getX());
		obj.put("y", getY());
		obj.put("lastX", getLastX());
		obj.put("lastY", getLastY());
		obj.put("visualID", getVisualID());

		statusEffects.forEach(e -> {
			JSONObject serialisedStatusEffect = new JSONObject();
			e.serialise(serialisedStatusEffect);
			obj.append("statusEffects", serialisedStatusEffect);
		});
	}

	public void unserialise(JSONObject obj) {
		lastX = obj.getInt("lastX");
		lastY = obj.getInt("lastY");
		visualID = obj.getInt("visualID");

		if (obj.has("statusEffects")) {
			JSONArray serialisedStatusEffects = obj.getJSONArray("statusEffects");
			serialisedStatusEffects.forEach(serialisedStatusEffect -> {
				unserialiseStatusEffect((JSONObject) serialisedStatusEffect);
			});
		}
	}

	@SuppressWarnings("unchecked")
	private void unserialiseStatusEffect(JSONObject serialisedStatusEffect) {
		String statusEffectClassName = serialisedStatusEffect.getString("class");

		try {
			Class statusEffectClass = Class.forName(statusEffectClassName);
			Constructor statusEffectConstructor = statusEffectClass.getConstructor(
				Dungeon.class,
				Entity.class,
				int.class
			);

			StatusEffect effect = (StatusEffect) statusEffectConstructor.newInstance(
				getDungeon(),
				this,
				serialisedStatusEffect.getInt("duration")
			);
			effect.unserialise(serialisedStatusEffect);
			statusEffects.add(effect);
		} catch (ClassNotFoundException e) {
			JRogue.getLogger().error("Unknown status effect class {}", statusEffectClassName);
		} catch (NoSuchMethodException e) {
			JRogue.getLogger().error(
				"Status effect class {} has no unserialisation constructor",
				statusEffectClassName
			);
		} catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
			JRogue.getLogger().error("Error loading status effect class {}", statusEffectClassName);
			JRogue.getLogger().error(e);
		}
	}

	public void addStatusEffect(StatusEffect effect) {
		statusEffects.add(effect);
	}

	public boolean hasStatusEffect(Class<? extends StatusEffect> statusEffect) {
		return statusEffects.stream().anyMatch(statusEffect::isInstance);
	}

	public List<StatusEffect> getStatusEffects() {
		return statusEffects;
	}

	public void kick(LivingEntity kicker, boolean isPlayer, int x, int y) {
		onKick(kicker, isPlayer, x, y);
	}

	protected abstract void onKick(LivingEntity kicker, boolean isPlayer, int x, int y);

	public void walk(LivingEntity walker, boolean isPlayer) {
		onWalk(walker, isPlayer);
	}

	protected abstract void onWalk(LivingEntity walker, boolean isPlayer);

	public abstract boolean canBeWalkedOn();
}
