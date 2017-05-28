package jr.dungeon.entities;

import jr.JRogue;
import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.effects.StatusEffect;
import jr.dungeon.entities.events.*;
import jr.dungeon.events.Event;
import jr.dungeon.events.EventHandler;
import jr.dungeon.events.EventListener;
import jr.language.Noun;
import jr.language.transformations.Possessive;
import jr.language.transformations.Transformer;
import jr.utils.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Base Entity class. An entity is a unique game object that exists inside a {@link Level}. All entities have a
 * position and a UUID, as well as a few other intrinsic properties. Additionally, all entities are a
 * {@link jr.dungeon.events.EventListener}, and can listen to dungeon events with {@link EventHandler}
 * methods.
 */
@Getter
public abstract class Entity implements Serialisable, Persisting, EventListener {
	/**
	 * The unique identifier for this Entity instance, mainly used for referencing during serialisation.
	 */
	private UUID uuid;
	
	/**
	 * The X position of this Entity in the {@link Level}.
	 */
	@Setter private int x;
	/**
	 * The Y position of this Entity in the {@link Level}.
	 */
	@Setter private int y;
	
	/**
	 * The last X position of this Entity in the {@link Level}. This is not necessarily the position last turn, but
	 * the position before it was last assigned.
	 */
	@Setter private int lastX;
	/**
	 * The last Y position of this Entity in the {@link Level}. This is not necessarily the position last turn, but
	 * the position before it was last assigned.
	 */
	@Setter private int lastY;
	
	/**
	 * The last X position of this Entity in the {@link Level} that was seen by the
	 * {@link jr.dungeon.entities.player.Player}.
	 */
	@Setter private int lastSeenX;
	/**
	 * The last Y position of this Entity in the {@link Level} that was seen by the
	 * {@link jr.dungeon.entities.player.Player}.
	 */
	@Setter private int lastSeenY;
	
	/**
	 * A random non-unique number between 0 and 1000 used for randomisation inside the renderer. You can use this
	 * number for persistent random effects with no actual gameplay effect, e.g. the colour of a spider could be
	 * visualID % 2.
	 */
	private int visualID;
	
	/**
	 * Assigned by the {@link jr.dungeon.EntityStore} when the Entity is in the removal queue. Do not set this yourself,
	 * instead, use {@link jr.dungeon.EntityStore#removeEntity(Entity)}.
	 *
	 * @see jr.dungeon.EntityStore
	 */
	@Setter private boolean beingRemoved = false;
	
	/**
	 * The {@link Dungeon} this Entity is part of.
	 */
	private Dungeon dungeon;
	/**
	 * The {@link Level} this Entity is part of.
	 * -- SETTER --
	 * Sets the  {@link Level} this Entity is part of. Do not set this without removing it from the old Level's
	 * {@link jr.dungeon.EntityStore} and placing it in the new Level's one. This value is not automatically synced
	 * with {@link jr.dungeon.EntityStore EntityStores}.
	 */
	private Level level;
	
	/**
	 * List of extrinsic {@link StatusEffect status effects} that the Entity has. These are typically temporary
	 * effects that last a certain duration of turns, for example being {@link jr.dungeon.entities.effects.Ablaze} or
	 * blind.
	 *
	 * @see StatusEffect
	 */
	private List<StatusEffect> statusEffects = new ArrayList<>();
	
	/**
	 * An object of persistent properties that will be serialised with the Entity. Can contain absolutely any data
	 * for any purpose - typically for use by mods or the renderer.
	 */
	private final JSONObject persistence = new JSONObject();
	
	/**
	 * Base Entity class. An entity is a unique game object that exists inside a {@link Level}. All entities have a
	 * position and a UUID, as well as a few other intrinsic properties. Additionally, all entities are a
	 * {@link EventListener}, and can listen to dungeon events with {@link EventHandler}
	 * methods.
	 *
	 * @param dungeon The {@link Dungeon} that this Entity is a part of.
	 * @param level The {@link Level} that this Entity is inside.
	 * @param x The starting X position of the Entity inside the {@link Level}.
	 * @param y The starting Y position of the Entity inside the {@link Level}.
	 */
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
	 * @param observer The entity "reading" the name. Used, for example, to hide aspects the entity does not know about.
	 * @return The name of this entity.
	 */
	public abstract Noun getName(EntityLiving observer);
	
	public Transformer getPossessiveTransformer(EntityLiving observer, Object... transformers) {
		if (observer == this) {
			return Possessive.your;
		} else {
			return Possessive.build(getName(observer).build(transformers));
		}
	}

	/**
	 * @return The appearance of this entity. Determines which sprite is rendered.
	 */
	public abstract EntityAppearance getAppearance();
	
	/**
	 * @return The Entity's X and Y coordinates in the {@link Level} as a {@link Point}.
	 */
	public Point getPosition() {
		return Point.getPoint(x, y);
	}

	/**
	 * @return The Entity's X and Y coordinates in the {@link Level} as a {@link VectorInt}.
	 */
	public VectorInt getPositionVector() {
		return new VectorInt(x, y);
	}

	/**
	 * Sets the Entity's X and Y coordinates in the {@link Level}, updates the Entity's lastX and lastY coordinates,
	 * and triggers an {@link EntityMovedEvent},
	 *
	 * @param x The Entity's new X position.
	 * @param y The Entity's new Y position.
	 */
	public void setPosition(int x, int y) {
		setLastX(getX());
		setLastY(getY());
		setX(x);
		setY(y);
		
		dungeon.eventSystem.triggerEvent(new EntityMovedEvent(this, getLastX(), getLastY(), x, y));
	}
	
	/**
	 * Sets the Entity's X and Y coordinates in the {@link Level}, resets the last position,
	 * and triggers an {@link EntityMovedEvent},
	 *
	 * @param x The Entity's new X position.
	 * @param y The Entity's new Y position.
	 */
	public void setPositionFresh(int x, int y) {
		setLastX(x);
		setLastY(y);
		setX(x);
		setY(y);
		
		dungeon.eventSystem.triggerEvent(new EntityMovedEvent(this, x, y, x, y));
	}
	
	/**
	 * Sets the Entity's position in the {@link Level}, updates the Entity's lastX and lastY coordinates,
	 * and triggers an {@link EntityMovedEvent},
	 *
	 * @param point The entity's new position.
	 */
	public void setPosition(Point point) {
		setPosition(point.getX(), point.getY());
	}
	
	/**
	 * Sets the Entity's position in the {@link Level}, resets the last position, and triggers an
	 * {@link EntityMovedEvent},
	 *
	 * @param point The entity's new position.
	 */
	public void setPositionFresh(Point point) {
		setPositionFresh(point.getX(), point.getY());
	}
	
	/**
	 * @return The Entity's last X and Y coordinates in the {@link Level} as a {@link Point}.
	 */
	public Point getLastPosition() {
		return Point.getPoint(lastX, lastY);
	}
	
	/**
	 * @return The position the {@link jr.dungeon.entities.player.Player} last saw this Entity in the {@link Level} as
	 * a {@link Point}.
	 */
	public Point getLastSeenPosition() {
		return Point.getPoint(lastSeenX, lastSeenY);
	}
	
	/**
	 * @return The rendering depth of this Entity. Entities with lower depths are drawn first, i.e. on the bottom.
	 */
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
				dungeon.eventSystem
					.triggerEvent(new EntityStatusEffectChangedEvent(this, effect, EntityStatusEffectChangedEvent.Change.REMOVED)
					);
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
			JRogue.getLogger().error("Error loading status effect class {}", statusEffectClassName, e);
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
		dungeon.eventSystem
			.triggerEvent(new EntityStatusEffectChangedEvent(this, effect, EntityStatusEffectChangedEvent.Change.ADDED)
			);
	}

	/**
	 * @param statusEffect The class of a {@link jr.dungeon.entities.effects.StatusEffect}.
	 * @return Whether this entity is affected by <code>statusEffect</code>.
	 */
	public boolean hasStatusEffect(Class<? extends StatusEffect> statusEffect) {
		return statusEffects.stream().anyMatch(statusEffect::isInstance);
	}

	/**
	 * Kicks this entity. Will trigger an {@link jr.dungeon.entities.events.EntityKickedEntityEvent}.
	 * @param kicker The entity that is kicking this entity.
	 * @param dx The x direction to kick in.
	 * @param dy The y direction to kick in.
	 */
	public void kick(EntityLiving kicker, int dx, int dy) {
		getDungeon().eventSystem.triggerEvent(new EntityKickedEntityEvent(this, kicker, dx, dy));
	}

	/**
	 * Walk on top of this entity. Will trigger an {@link jr.dungeon.entities.events.EntityWalkedOnEvent}.
	 * @param walker The entity walking on top of this entity.
	 */
	public void walk(EntityLiving walker) {
		getDungeon().eventSystem.triggerEvent(new EntityWalkedOnEvent(this, walker));
	}

	/**
	 * Teleports this entity to the given entity. Will trigger an {@link jr.dungeon.entities.events.EntityTeleportedToEvent}.
	 * @param teleporter The entity to teleport to.
	 */
	public void teleport(EntityLiving teleporter) {
		getDungeon().eventSystem.triggerEvent(new EntityTeleportedToEvent(this, teleporter));
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
	
	/**
	 * This is a set of objects related to the Entity which should receive {@link Event
	 * dungeon events}. When overriding this to add your own, you must always concatenate super's getSubListeners()
	 * to the list that you return.
	 *
	 * @return A set of {@link EventListener DunegonEventListeners} to receive events.
	 */
	public Set<EventListener> getSubListeners() {
		return new HashSet<>(statusEffects);
	}
	
	public void setLevel(Level level) {
		this.level.entityStore.removeEntity(this);
		this.level.entityStore.processEntityQueues(false);
		
		this.level = level;
		
		level.entityStore.addEntity(this);
		level.entityStore.processEntityQueues(false);
	}
	
	@Override
	public String toString() {
		return toStringBuilder().build();
	}
	
	public ToStringBuilder toStringBuilder() {
		ToStringBuilder tsb = new ToStringBuilder(this, DebugToStringStyle.STYLE)
			.append("uuid", uuid)
			.append("level", level.toString())
			.append("position", getPosition().toString())
			.append("lastPosition", getLastPosition().toString())
			.append("lastSeenPosition", getLastSeenPosition().toString())
			.append("isBeingRemoved", beingRemoved)
			.append("visualID", visualID)
			.append("appearance", getAppearance().name().toLowerCase());
		
		statusEffects.forEach(s -> tsb.append(s.toStringBuilder()));
		
		return tsb;
	}
}
