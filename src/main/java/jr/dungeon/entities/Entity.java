package jr.dungeon.entities;

import com.badlogic.gdx.Gdx;
import jr.JRogue;
import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.containers.Container;
import jr.dungeon.entities.effects.StatusEffect;
import jr.dungeon.entities.events.*;
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

	/**
	 * @return An identifier unique to this entity.
	 */
	public UUID getUUID() {
		return uuid;
	}

	/**
	 * @param observer The entity "reading" the name.
	 * @param requiresCapitalisation Whether the name should have its first letter capitalised.
	 * @return The name of this entity.
	 */
	public abstract String getName(EntityLiving observer, boolean requiresCapitalisation);

	/**
	 * @return The appearance of this entity. Determines which sprite is rendered.
	 */
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

	/**
	 * @return true if this entity is immobile and should be shown outside of the player's view.
	 */
	public boolean isStatic() {
		return false;
	}

	/**
	 * @return A Container associated with this entity. E.g. {@link jr.dungeon.entities.player.Player} will return its inventory.
	 */
	public Optional<Container> getContainer() {
		return Optional.empty();
	}

	/**
	 * @return true if this entity can be looted like a chest.
	 */
	public boolean isLootable() {
		return false;
	}
	
	public Optional<String> lootSuccessString() {
		return Optional.empty();
	}
	
	public Optional<String> lootFailedString() {
		return Optional.empty();
	}

	/**
	 * This method is called every frame.
	 * It's possible to get the time since the last frame using <code>Gdx.graphics.getDeltaTime()</code>.
	 */
	public void update() {
		for (Iterator<StatusEffect> iterator = statusEffects.iterator(); iterator.hasNext(); ) {
			StatusEffect effect = iterator.next();
			
			effect.turn();
			
			if (effect.getDuration() >= 0 && effect.getTurnsPassed() >= effect.getDuration()) {
				effect.onEnd();
				iterator.remove();
				dungeon.triggerEvent(new EntityStatusEffectChangedEvent(this, effect, EntityStatusEffectChangedEvent.Change.REMOVED));
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

	/**
	 * Adds a {@link jr.dungeon.entities.effects.StatusEffect} to this entity and triggers related events.
	 * @param effect The effect to be applied.
	 */
	public void addStatusEffect(StatusEffect effect) {
		effect.setEntity(this);
		effect.setMessenger(dungeon);
		statusEffects.add(effect);
		dungeon.triggerEvent(new EntityStatusEffectChangedEvent(this, effect, EntityStatusEffectChangedEvent.Change.ADDED));
	}

	/**
	 * @param statusEffect The class of a {@link jr.dungeon.entities.effects.StatusEffect}.
	 * @return Whether this entity is affected by <code>statusEffect</code>.
	 */
	public boolean hasStatusEffect(Class<? extends StatusEffect> statusEffect) {
		return statusEffects.stream().anyMatch(statusEffect::isInstance);
	}

	/**
	 * Kicks this entity. Will trigger an {@link jr.dungeon.entities.events.EntityKickedEvent}.
	 * @param kicker The entity that is kicking this entity.
	 * @param dx The x direction to kick in.
	 * @param dy The y direction to kick in.
	 */
	public void kick(EntityLiving kicker, int dx, int dy) {
		getDungeon().triggerEvent(new EntityKickedEvent(this, kicker, dx, dy));
	}

	/**
	 * Walk on top of this entity. Will trigger an {@link jr.dungeon.entities.events.EntityWalkedOnEvent}.
	 * @param walker The entity walking on top of this entity.
	 */
	public void walk(EntityLiving walker) {
		getDungeon().triggerEvent(new EntityWalkedOnEvent(this, walker));
	}

	/**
	 * Teleports this entity to the given entity. Will trigger an {@link jr.dungeon.entities.events.EntityTeleportedToEvent}.
	 * @param teleporter The entity to teleport to.
	 */
	public void teleport(EntityLiving teleporter) {
		getDungeon().triggerEvent(new EntityTeleportedToEvent(this, teleporter));
	}

	/**
	 * @return Whether this entity is solid or can be walked on.
	 */
	public abstract boolean canBeWalkedOn();

	/**
	 * @return Persistence data. Anything stored in this JSONObject will persist across saves.
	 */
	@Override
	public JSONObject getPersistence() {
		return persistence;
	}
	
	public List<DungeonEventListener> getSubListeners() {
		return new ArrayList<>(statusEffects);
	}
}
