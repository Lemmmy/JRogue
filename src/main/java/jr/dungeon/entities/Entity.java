package jr.dungeon.entities;

import jr.JRogue;
import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.containers.Container;
import jr.dungeon.entities.effects.StatusEffect;
import jr.dungeon.entities.events.EntityKickedEvent;
import jr.dungeon.entities.events.EntityMovedEvent;
import jr.dungeon.entities.events.EntityTeleportedToEvent;
import jr.dungeon.entities.events.EntityWalkedOnEvent;
import jr.dungeon.events.DungeonEventListener;
import jr.utils.Persisting;
import jr.utils.Point;
import jr.utils.RandomUtils;
import jr.utils.Serialisable;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

@Getter
public abstract class Entity implements Serialisable, Persisting, DungeonEventListener {
	private UUID uuid;
	
	@Setter private int x;
	@Setter private int y;
	
	@Setter private int lastX;
	@Setter private int lastY;
	
	@Setter private int lastSeenX;
	@Setter private int lastSeenY;
	
	private int visualID;
	
	@Setter private boolean beingRemoved = false;
	
	private Dungeon dungeon;
	@Setter private Level level;
	
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
	
	public abstract String getName(EntityLiving observer, boolean requiresCapitalisation);
	
	public abstract EntityAppearance getAppearance();
	
	public Point getPosition() {
		return Point.getPoint(x, y);
	}
	
	public void setPosition(int x, int y) {
		setLastX(getX());
		setLastY(getY());
		setX(x);
		setY(y);
		
		dungeon.triggerEvent(new EntityMovedEvent(this, getLastX(), getLastY(), x, y));
	}
	
	public Point getLastPosition() {
		return Point.getPoint(lastX, lastY);
	}
	
	public Point getLastSeenPosition() {
		return Point.getPoint(lastSeenX, lastSeenY);
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
	
	public void kick(EntityLiving kicker, int dx, int dy) {
		getDungeon().triggerEvent(new EntityKickedEvent(this, kicker, dx, dy));
	}
	
	public void walk(EntityLiving walker) {
		getDungeon().triggerEvent(new EntityWalkedOnEvent(this, walker));
	}
	
	public void teleport(EntityLiving teleporter) {
		getDungeon().triggerEvent(new EntityTeleportedToEvent(this, teleporter));
	}
	
	public abstract boolean canBeWalkedOn();

	@Override
	public JSONObject getPersistence() {
		return persistence;
	}
	
	public List<DungeonEventListener> getSubListeners() {
		return new ArrayList<>(statusEffects);
	}
}
