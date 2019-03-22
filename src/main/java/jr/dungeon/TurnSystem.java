package jr.dungeon;

import com.github.alexeyr.pcg.Pcg32;
import com.google.gson.annotations.Expose;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.EntityTurnBased;
import jr.dungeon.entities.events.EntityAddedEvent;
import jr.dungeon.entities.interfaces.PassiveSoundEmitter;
import jr.dungeon.entities.player.Player;
import jr.dungeon.events.BeforeTurnEvent;
import jr.dungeon.events.EventSystem;
import jr.dungeon.events.TurnEvent;
import jr.utils.RandomUtils;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.Range;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class TurnSystem {
	/**
	 * The random range of turns in which a random {@link jr.dungeon.entities.monsters.Monster} will spawn somewhere on
	 * the {@link Level}.
	 *
	 * @see MonsterSpawner#spawnNewMonsters()
	 */
	private static final Range<Integer> PROBABILITY_MONSTER_SPAWN_COUNTER = Range.between(40, 100);
	
	
	private Dungeon dungeon;
	
	/**
	 * The number of turns that have passed.
	 */
	@Expose @Getter private long turn = 0;
	
	/**
	 * A bulk action is an action in which multiple turns will pass, and the action is repeated. For example, when
	 * you walk to a locked door, and confirm you want to automatically kick it down, the bulk action of kicking the
	 * door will occur. Bulk actions can be interrupted by marking the turn as a turn in which 'something happened'.
	 *
	 * @see #markSomethingHappened()
	 *
	 * @param doingBulkAction Sets whether or not a bulk action is currently happening.
	 * @return Whether or not a bulk action is currently happening.
	 */
	@Getter @Setter private boolean doingBulkAction;
	
	/**
	 * A turn in which something happened is usually a turn where something that should interrupt a
	 * {@link #isDoingBulkAction() bulk action}, for example a {@link jr.dungeon.entities.monsters.Monster} attacking
	 * the {@link Player}.
	 *
	 * @return Whether or not something critical happened in this turn.
	 */
	@Getter private boolean somethingHappened;
	
	/**
	 * Random counter for ambient dungeon 'sounds'.
	 *
	 * @see PassiveSoundEmitter
	 */
	@Expose @Getter private long passiveSoundCounter = 0;
	
	/**
	 * Random counter for new monster spawns.
	 *
	 * @see MonsterSpawner
	 */
	@Expose @Getter private long monsterSpawnCounter = 50;
	
	/**
	 * rand
	 */
	private Pcg32 rand = new Pcg32();
	
	public TurnSystem(Dungeon dungeon) {
		this.dungeon = dungeon;
	}
	
	/**
	 * Mark this turn as a turn that something critical happened in. This usually means that this should interrupt
	 * bulk-actions, e.g. the player is knocking down a door as a bulk action, but then a monster attacks it. The
	 * monster attack marks the turn as 'something happened', and the bulk action is cancelled.
	 */
	public void markSomethingHappened() {
		somethingHappened = true;
		
		// TODO: trigger event here?
	}
	
	public void turn(Dungeon dungeon) {
		turn(false);
	}
	
	/**
	 * Triggers the next turn, increasing the turn counter, and updating all entities.
	 *
	 * @param isStart Whether or not this turn occurs during the start of a new level, or after loading a level. This
	 *                  is used to ensure that when processing entity queues, isNew is correctly passed to
	 *                  {@link EntityAddedEvent EntityAddedEvents}.
	 */
	public void turn(boolean isStart) {
		Level l = dungeon.getLevel();
		Player p = dungeon.getPlayer();
		EventSystem ev = dungeon.eventSystem;
		EntityStore es = l.entityStore;
		
		if (!p.isAlive()) {
			dungeon.eventSystem.triggerTurnCompleteEvents();
			return;
		}
		
		ev.triggerEvent(new BeforeTurnEvent(turn + 1));
		somethingHappened = false;
		
		l.entityStore.processEntityQueues(!isStart);
		
		p.setMovementPoints(p.getMovementPoints() - Dungeon.NORMAL_SPEED);
		
		do {
			boolean entitiesCanMove = false;
			
			do {
				if (!p.isAlive()) {
					break;
				}
				
				entitiesCanMove = moveEntities();
				
				if (p.getMovementPoints() > Dungeon.NORMAL_SPEED) {
					break;
				}
			} while (entitiesCanMove);
			
			if (!entitiesCanMove && p.getMovementPoints() < Dungeon.NORMAL_SPEED) {
				for (Entity entity : es.getEntities()) {
					if (!p.isAlive()) {
						break;
					}
					
					if (entity instanceof EntityLiving && !((EntityLiving) entity).isAlive()) {
						continue;
					}
					
					entity.update();
					
					if (entity instanceof EntityTurnBased) {
						EntityTurnBased turnBasedEntity = (EntityTurnBased) entity;
						
						turnBasedEntity.applyMovementPoints();
					}
				}
				
				if (p.getMovementPoints() < 0) {
					p.setMovementPoints(0);
				}
				
				turn++;
				
				update();
			}
		} while (p.isAlive() && p.getMovementPoints() < Dungeon.NORMAL_SPEED);
		
		if (p.isAlive()) {
			p.move();
		} else {
			ev.triggerTurnCompleteEvents();
			return;
		}
		
		es.processEntityQueues(!isStart);
		
		l.visibilityStore.updateSight(dungeon.getPlayer());
		l.lightStore.buildLight(false);
		
		ev.triggerTurnCompleteEvents();
		
		ev.triggerEvent(new TurnEvent(turn));
	}
	
	/**
	 * Makes all entities make their next move.
	 * @return false if nobody moved.
	 */
	public boolean moveEntities() {
		AtomicBoolean somebodyCanMove = new AtomicBoolean(false);
		
		dungeon.getLevel().entityStore.getEntities().stream()
			.filter(e -> e instanceof EntityTurnBased)
			.filter(e -> !(e instanceof Player))
			.filter(e -> !(((EntityTurnBased) e).getMovementPoints() < Dungeon.NORMAL_SPEED))
			.filter(e -> !(e instanceof EntityLiving) || ((EntityLiving) e).isAlive())
			.forEach(e -> {
				EntityTurnBased tbe = (EntityTurnBased) e;
				tbe.setMovementPoints(tbe.getMovementPoints() - Dungeon.NORMAL_SPEED);
				
				if (tbe.getMovementPoints() >= Dungeon.NORMAL_SPEED) {
					somebodyCanMove.set(true);
				}
				
				tbe.move();
			});
		
		return somebodyCanMove.get();
	}
	
	/**
	 * Updates the dungeon, which includes playing sounds and spawning monsters.
	 */
	public void update() {
		Level l = dungeon.getLevel();
		
		if (--passiveSoundCounter <= 0) {
			emitPassiveSounds();
			
			passiveSoundCounter = RandomUtils.roll(3, 4);
		}
		
		if (
			l.entityStore.getHostileMonsters().size() < Math.abs(l.getDepth() * 2 + 10) &&
			--monsterSpawnCounter <= 0
		) {
			l.monsterSpawner.spawnNewMonsters();
			
			monsterSpawnCounter = RandomUtils.random(PROBABILITY_MONSTER_SPAWN_COUNTER);
		}
	}
	
	/**
	 * Emits passive sounds.
	 */
	public void emitPassiveSounds() {
		List<Entity> emitters = dungeon.getLevel().entityStore.getEntities().stream()
			.filter(e -> e instanceof PassiveSoundEmitter)
			.collect(Collectors.toList());
		
		if (emitters.isEmpty()) {
			return;
		}
		
		Collections.shuffle(emitters);
		PassiveSoundEmitter soundEmitter = (PassiveSoundEmitter) emitters.get(0);
		
		if (rand.nextFloat() <= soundEmitter.getSoundProbability()) {
			String sound = RandomUtils.randomFrom(soundEmitter.getSounds());
			
			dungeon.log(sound);
		}
	}
}
