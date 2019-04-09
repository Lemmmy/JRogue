package jr.dungeon.entities;

import com.google.gson.annotations.Expose;
import jr.debugger.utils.Debuggable;
import jr.dungeon.Dungeon;
import jr.dungeon.EntityStore;
import jr.dungeon.Level;
import jr.dungeon.entities.effects.StatusEffect;
import jr.dungeon.entities.events.*;
import jr.dungeon.entities.player.Player;
import jr.dungeon.events.Event;
import jr.dungeon.events.EventHandler;
import jr.dungeon.events.EventListener;
import jr.dungeon.serialisation.HasRegistry;
import jr.dungeon.serialisation.Serialisable;
import jr.dungeon.tiles.Tile;
import jr.language.Noun;
import jr.language.transformers.Capitalise;
import jr.language.transformers.Possessive;
import jr.language.transformers.Transformer;
import jr.utils.DebugToStringStyle;
import jr.utils.Point;
import jr.utils.RandomUtils;
import jr.utils.VectorInt;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.*;

/**
 * Base Entity class. An entity is a unique game object that exists inside a {@link Level}. All entities have a position
 * and a UUID, as well as a few other intrinsic properties. Additionally, all entities are an
 * {@link jr.dungeon.events.EventListener}, and can listen to dungeon events with {@link EventHandler} methods.
 */
@Getter
@HasRegistry
public abstract class Entity implements Serialisable, EventListener, Debuggable {
	/**
	 * The unique identifier for this Entity instance, mainly used for referencing during serialisation.
	 */
	@Expose private UUID uuid;
	
	/**
	 * The position of this Entity in the {@link Level}.
	 */
	@Expose private Point position;
	
	/**
	 * The last position of this Entity in the {@link Level}. This is not necessarily the position last turn, but the
	 * position before it was last assigned.
	 */
	@Setter @Expose private Point lastPosition;
	
	/**
	 * The last position of this Entity in the {@link Level} that was seen by the
	 * {@link jr.dungeon.entities.player.Player}.
	 */
	@Setter @Expose private Point lastSeenPosition;
	
	/**
	 * A random non-unique number between 0 and 1000 used for randomisation inside the renderer. You can use this number
	 * for persistent random effects with no actual gameplay effect, e.g. the colour of a spider could be visualID % 2.
	 */
	@Expose private int visualID;
	
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
	 */
	private Level level;
	
	/**
	 * List of extrinsic {@link StatusEffect status effects} that the Entity has. These are typically temporary
	 * effects that last a certain duration of turns, for example being {@link jr.dungeon.entities.effects.Ablaze} or
	 * blind.
	 *
	 * @see StatusEffect
	 */
	@Expose private List<StatusEffect> statusEffects = new ArrayList<>();
	
	/**
	 * Base Entity class. An entity is a unique game object that exists inside a {@link Level}. All entities have a
	 * position and a UUID, as well as a few other intrinsic properties. Additionally, all entities are a
	 * {@link EventListener}, and can listen to dungeon events with {@link EventHandler} methods.
	 *  @param dungeon The {@link Dungeon} that this Entity is a part of.
	 * @param level The {@link Level} that this Entity is inside.
	 * @param position The starting position of the Entity inside the {@link Level}.
	 */
	public Entity(Dungeon dungeon, Level level, Point position) {
		this.uuid = UUID.randomUUID();
		
		this.dungeon = dungeon;
		this.level = level;
		
		this.position = position;
		lastPosition = position;
		lastSeenPosition = position;
		
		visualID = RandomUtils.random(1000);
	}
	
	protected Entity() {} // deserialisation constructor

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
	 * Sets the Entity's position in the {@link Level}, updates the Entity's lastX and lastY coordinates, and triggers
	 * an {@link EntityMovedEvent},
	 *
	 * @param point The entity's new position.
	 */
	public void setPosition(@NonNull Point point) {
		lastPosition = position;
		position = point;
		
		dungeon.eventSystem.triggerEvent(new EntityMovedEvent(this, lastPosition, position));
	}

	/**
	 * Sets the Entity's X and Y coordinates in the {@link Level}, updates the Entity's lastX and lastY coordinates,
	 * and triggers an {@link EntityMovedEvent},
	 *
	 * @param x The Entity's new X position.
	 * @param y The Entity's new Y position.
	 */
	public void setPosition(int x, int y) {
		setPosition(Point.get(x, y));
	}
	
	/**
	 * Sets the Entity's position in the {@link Level}, resets the last position, and triggers an
	 * {@link EntityMovedEvent}.
	 *
	 * @param point The entity's new position.
	 */
	public void setPositionFresh(@NonNull Point point) {
		lastPosition = point;
		position = point;
		
		dungeon.eventSystem.triggerEvent(new EntityMovedEvent(this, lastPosition, position));
	}
	
	/**
	 * Sets the Entity's X and Y coordinates in the {@link Level}, resets the last position, and triggers an
	 * {@link EntityMovedEvent},
	 *
	 * @param x The Entity's new X position.
	 * @param y The Entity's new Y position.
	 */
	public void setPositionFresh(int x, int y) {
		setPositionFresh(Point.get(x, y));
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

	public void update() {
		for (Iterator<StatusEffect> iterator = statusEffects.iterator(); iterator.hasNext(); ) {
			StatusEffect effect = iterator.next();
			
			effect.turn();
			
			if (effect.getDuration() >= 0 && effect.getTurnsPassed() >= effect.getDuration()) {
				effect.onEnd();
				iterator.remove();
				dungeon.eventSystem.triggerEvent(new EntityStatusEffectChangedEvent(this, effect, EntityStatusEffectChangedEvent.Change.REMOVED));
			}
		}
	}
	
	@Override
	public void afterDeserialise() {
		statusEffects.forEach(statusEffect -> statusEffect.init(dungeon, this));
	}
	
	/**
	 * Adds a {@link jr.dungeon.entities.effects.StatusEffect} to this entity and triggers related events.
	 *
	 * @param effect The effect to be applied.
	 */
	public void addStatusEffect(StatusEffect effect) {
		effect.setEntity(this);
		effect.setMessenger(dungeon);
		statusEffects.add(effect);
		dungeon.eventSystem.triggerEvent(new EntityStatusEffectChangedEvent(
			this, effect, EntityStatusEffectChangedEvent.Change.ADDED
		));
	}

	/**
	 * @param statusEffect The class of a {@link jr.dungeon.entities.effects.StatusEffect}.
	 * @return Whether this entity is affected by {@code statusEffect}.
	 */
	public boolean hasStatusEffect(Class<? extends StatusEffect> statusEffect) {
		return statusEffects.stream().anyMatch(statusEffect::isInstance);
	}

	/**
	 * Kicks this entity. Will trigger an {@link jr.dungeon.entities.events.EntityKickedEntityEvent}.
	 *
	 * @param kicker The entity that is kicking this entity.
	 * @param direction The direction to kick in.
	 */
	public void kick(EntityLiving kicker, VectorInt direction) {
		getDungeon().eventSystem.triggerEvent(new EntityKickedEntityEvent(this, kicker, direction));
	}

	/**
	 * Walk on top of this entity. Will trigger an {@link jr.dungeon.entities.events.EntityWalkedOnEvent}.
	 *
	 * @param walker The entity walking on top of this entity.
	 */
	public void walk(EntityLiving walker) {
		getDungeon().eventSystem.triggerEvent(new EntityWalkedOnEvent(this, walker));
	}

	/**
	 * Teleports this entity to the given entity. Will trigger an {@link jr.dungeon.entities.events.EntityTeleportedToEvent}.
	 *
	 * @param teleporter The entity to teleport to.
	 */
	public void teleport(EntityLiving teleporter) {
		getDungeon().eventSystem.triggerEvent(new EntityTeleportedToEvent(this, teleporter));
	}
	
	/**
	 * Queue this entity to be removed. The entity will be removed when the
	 * {@link EntityStore#processEntityQueues(boolean) entity queues} are next processed - this is typically at the
	 * start and end of each turn.
	 *
	 * @see EntityStore#removeEntity
	 */
	public void remove() {
		if (getLevel() == null || isBeingRemoved()) return;
		getLevel().entityStore.removeEntity(this);
	}

	/**
	 * @return Whether this entity is solid or can be walked on.
	 */
	public abstract boolean canBeWalkedOn();
	
	/**
	 * This is a set of objects related to the Entity which should receive {@link Event dungeon events}. When overriding
	 * this to add your own, you must always concatenate super's getSubListeners() to the list that you return.
	 *
	 * @return A set of {@link EventListener DunegonEventListeners} to receive events.
	 */
	public Set<EventListener> getSubListeners() {
		return new HashSet<>(statusEffects);
	}
	
	public void setLevel(Level level, Point newPosition) {
		Tile oldTile = newPosition != null ? this.level.tileStore.getTile(position) : null;
		
		this.level.entityStore.removeEntity(this); // TODO: is this safe to replace with remove()?
		this.level.entityStore.processEntityQueues(false);
		
		setLevelInternal(level);
		setPositionFresh(newPosition);
		
		level.entityStore.addEntity(this);
		level.entityStore.processEntityQueues(false);
		
		dungeon.eventSystem.triggerEvent(new EntityChangeLevelEvent(
			this,
			oldTile,
			level.tileStore.getTile(newPosition)
		));
	}
	
	public void setLevelInternal(Level level) {
		this.level = level;
		this.dungeon = level.getDungeon();
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
			.append("appearance", getAppearance().name().toLowerCase().replace("appearance_", ""));
		
		statusEffects.forEach(s -> tsb.append(s.toStringBuilder()));
		
		return tsb;
	}
	
	@Override
	public String getValueString() {
		String name = getAppearance().name().replaceFirst("^APPEARANCE_", "");
		
		if (getDungeon() != null) {
			Player player = getDungeon().getPlayer();
			name = getName(player).build(Capitalise.first);
		}
		
		return String.format("[P_GREY_3]%s[] %s", name, position);
	}
	
	@Override
	public int hashCode() {
		return uuid.hashCode();
	}
}
