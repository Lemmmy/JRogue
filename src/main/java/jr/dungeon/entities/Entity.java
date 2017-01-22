package jr.dungeon.entities;

import jr.JRogue;
import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.containers.Container;
import jr.dungeon.entities.containers.EntityItem;
import jr.dungeon.entities.effects.StatusEffect;
import jr.utils.Persisting;
import jr.utils.RandomUtils;
import jr.utils.Serialisable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public abstract class Entity implements Serialisable, Persisting {
	private UUID uuid;
	
	private int x;
	private int y;
	
	private int lastX;
	private int lastY;
	
	private int lastSeenX;
	private int lastSeenY;
	
	private int visualID;
	
	private boolean beingRemoved = false;
	
	private Dungeon dungeon;
	private Level level;
	
	private List<StatusEffect> statusEffects = new ArrayList<>();

	private final JSONObject persistence = new JSONObject();
	
	public Entity(Dungeon dungeon, Level level, int x, int y) {
		this.uuid = UUID.randomUUID();
		
		this.dungeon = dungeon;
		this.level = level;
		
		this.x = x;
		this.y = y;
		this.lastX = x;
		this.lastY = y;
		this.lastSeenX = x;
		this.lastSeenY = y;
		
		this.visualID = RandomUtils.random(1000);
	}
	
	public UUID getUUID() {
		return uuid;
	}
	
	public int getVisualID() {
		return visualID;
	}
	
	public abstract String getName(EntityLiving observer, boolean requiresCapitalisation);
	
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
	
	public int getLastSeenX() {
		return lastSeenX;
	}
	
	public void setLastSeenX(int lastSeenX) {
		this.lastSeenX = lastSeenX;
	}
	
	public int getLastSeenY() {
		return lastSeenY;
	}
	
	public void setLastSeenY(int lastSeenY) {
		this.lastSeenY = lastSeenY;
	}
	
	public int getDepth() {
		return 1;
	}
	
	public boolean isStatic() {
		return false;
	}
	
	public Optional<Container> getContainer() {
		return Optional.empty();
	}
	
	public boolean lootable() {
		return false;
	}
	
	public Optional<String> lootSuccessString() {
		return Optional.empty();
	}
	
	public Optional<String> lootFailedString() {
		return Optional.empty();
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
	
	public boolean isBeingRemoved() {
		return beingRemoved;
	}
	
	public void setBeingRemoved(boolean beingRemoved) {
		this.beingRemoved = beingRemoved;
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
	
	public void onSpawn() {}
	
	public void onItemDropped(EntityItem entityItem) {}
	
	@Override
	public void serialise(JSONObject obj) {
		obj.put("class", getClass().getName());
		obj.put("uuid", getUUID().toString());
		obj.put("x", getX());
		obj.put("y", getY());
		obj.put("lastX", getLastX());
		obj.put("lastY", getLastY());
		obj.put("lastSeenX", getLastSeenX());
		obj.put("lastSeenY", getLastSeenY());
		obj.put("visualID", getVisualID());
		
		statusEffects.forEach(e -> {
			JSONObject serialisedStatusEffect = new JSONObject();
			e.serialise(serialisedStatusEffect);
			obj.append("statusEffects", serialisedStatusEffect);
		});

		serialisePersistence(obj);
	}
	
	@Override
	public void unserialise(JSONObject obj) {
		uuid = UUID.fromString(obj.getString("uuid"));
		x = obj.getInt("x");
		y = obj.getInt("y");
		lastX = obj.getInt("lastX");
		lastY = obj.getInt("lastY");
		lastSeenX = obj.getInt("lastSeenX");
		lastSeenY = obj.getInt("lastSeenY");
		visualID = obj.getInt("visualID");
		
		if (obj.has("statusEffects")) {
			JSONArray serialisedStatusEffects = obj.getJSONArray("statusEffects");
			serialisedStatusEffects.forEach(serialisedStatusEffect ->
				unserialiseStatusEffect((JSONObject) serialisedStatusEffect));
		}

		unserialisePersistence(obj);
	}
	
	@SuppressWarnings("unchecked")
	private void unserialiseStatusEffect(JSONObject serialisedStatusEffect) {
		String statusEffectClassName = serialisedStatusEffect.getString("class");
		
		try {
			Class<? extends StatusEffect> statusEffectClass = (Class<? extends StatusEffect>) Class
				.forName(statusEffectClassName);
			Constructor<? extends StatusEffect> statusEffectConstructor = statusEffectClass.getConstructor(
				Dungeon.class,
				Entity.class,
				int.class
			);
			
			StatusEffect effect = statusEffectConstructor.newInstance(
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
		effect.setEntity(this);
		effect.setMessenger(getDungeon());
		statusEffects.add(effect);
	}
	
	public boolean hasStatusEffect(Class<? extends StatusEffect> statusEffect) {
		return statusEffects.stream().anyMatch(statusEffect::isInstance);
	}
	
	public List<StatusEffect> getStatusEffects() {
		return statusEffects;
	}
	
	public void kick(EntityLiving kicker, boolean isPlayer, int dx, int dy) {
		onKick(kicker, isPlayer, dx, dy);
	}
	
	protected abstract void onKick(EntityLiving kicker, boolean isPlayer, int dx, int dy);
	
	public void walk(EntityLiving walker, boolean isPlayer) {
		onWalk(walker, isPlayer);
	}
	
	protected abstract void onWalk(EntityLiving walker, boolean isPlayer);
	
	public void teleport(EntityLiving walker, boolean isPlayer) {
		onTeleport(walker, isPlayer);
	}
	
	protected void onTeleport(EntityLiving walker, boolean isPlayer) {}
	
	public abstract boolean canBeWalkedOn();

	@Override
	public JSONObject getPersistence() {
		return persistence;
	}
}
