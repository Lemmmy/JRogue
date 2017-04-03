package jr.dungeon;

import com.github.alexeyr.pcg.Pcg32;
import jr.ErrorHandler;
import jr.JRogue;
import jr.Settings;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.EntityTurnBased;
import jr.dungeon.entities.events.EntityAddedEvent;
import jr.dungeon.entities.interfaces.PassiveSoundEmitter;
import jr.dungeon.entities.player.Player;
import jr.dungeon.entities.player.roles.RoleWizard;
import jr.dungeon.events.*;
import jr.dungeon.generators.*;
import jr.dungeon.tiles.Tile;
import jr.dungeon.wishes.Wishes;
import jr.utils.OperatingSystem;
import jr.utils.Persisting;
import jr.utils.RandomUtils;
import jr.utils.Serialisable;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.Range;
import org.fusesource.jansi.AnsiConsole;
import org.json.JSONObject;
import org.json.JSONTokener;

import javax.swing.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static org.fusesource.jansi.Ansi.*;
import static org.fusesource.jansi.Ansi.Color.*;

/**
 * The entire Dungeon object. This object contains all information about the actual game state, including the turn,
 * levels, and player.
 */
public class Dungeon implements Messenger, Serialisable, Persisting {
	/**
	 * The amount of 'ticks' in a turn.
	 *
	 * @see EntityLiving
	 */
	public static final int NORMAL_SPEED = 12;
	
	/**
	 * The default {@link Level} width. This may not be the Level's actual width. Use {@link TileStore#getWidth()}
	 * for that instead.
	 */
	public static final int LEVEL_WIDTH = 90;
	/**
	 * The default {@link Level} height. This may not be the Level's actual height. Use {@link TileStore#getHeight()}
	 * for that instead.
	 */
	public static final int LEVEL_HEIGHT = 40;
	
	/**
	 * The random range of turns in which a random {@link jr.dungeon.entities.monsters.Monster} will spawn somewhere on
	 * the {@link Level}.
	 *
	 * @see MonsterSpawner#spawnNewMonsters()
	 */
	private static final Range<Integer> PROBABILITY_MONSTER_SPAWN_COUNTER = Range.between(40, 100);
	
	/**
	 * The 'GAME' log level.
	 */
	private static org.apache.logging.log4j.Level gameLogLevel;
	
	/**
	 * List of {@link DungeonEventListener}s that the Dungeon should send events to.
	 */
	private final Set<DungeonEventListener> listeners = new HashSet<>();
	/**
	 * List of {@link DungeonEvent}s to be sent to {@link DungeonEventListener}s with the flag
	 * {@link DungeonEventHandler#invocationTime()} set to {@link DungeonEventInvocationTime#TURN_COMPLETE}.
	 */
	private final List<DungeonEvent> eventQueueNextTurn = new LinkedList<>();
	
	/**
	 * rand
	 */
	private Pcg32 rand = new Pcg32();
	
	/**
	 * Randomly generated name of this dungeon
	 */
	@Getter @Setter private String originalName;
	
	/**
	 * User-chosen name of this dungeon
	 */
	@Getter @Setter private String name;
	
	/**
	 * Map of the Dungeon's {@link Level}s with unique UUIDs as keys for serialisation reference.
	 */
	private Map<UUID, Level> levels = new HashMap<>();
	/**
	 * The {@link Level} that the {@link Player} is currently on.
	 */
	@Getter private Level level;
	/**
	 * The actual {@link Player} entity.
	 */
	@Getter @Setter private Player player;
	
	/**
	 * The number of turns that have passed.
	 */
	@Getter private long turn = 0;
	/**
	 * Random counter for ambient dungeon 'sounds'.
	 *
	 * @see PassiveSoundEmitter
	 */
	@Getter private long passiveSoundCounter = 0;
	/**
	 * Random counter for new monster spwans.
	 *
	 * @see MonsterSpawner
	 */
	@Getter private long monsterSpawnCounter = 50;
	
	/**
	 * A turn in which something happened is usually a turn where something that should interrupt a
	 * {@link #isDoingBulkAction() bulk action}, for example a {@link jr.dungeon.entities.monsters.Monster} attacking
	 * the {@link Player}.
	 *
	 * @return Whether or not something critical happened in this turn.
	 */
	@Getter private boolean somethingHappened;
	
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
	 * @see Prompt
	 *
	 * @return The curernt {@link Prompt} or null.
	 */
	@Getter private Prompt prompt;
	/**
	 * The current user-specified {@link Settings}.
	 */
	private Settings settings;
	
	/**
	 * The directory in which user data is saved, including saves and bones.
	 */
	private static Path dataDir = OperatingSystem.get().getAppDataDir().resolve("jrogue");
	
	/**
	 * Persistent object for user-defined keys, typically for use by mods or the renderer.
	 */
	private final JSONObject persistence = new JSONObject();
	
	/**
	 * The entire Dungeon object. This object contains all information about the actual game state, including the turn,
	 * levels, and player.
	 */
	public Dungeon() {
		this.settings = JRogue.getSettings();
		
		gameLogLevel = org.apache.logging.log4j.Level.getLevel("GAME");
	}

	/**
	 * Randomly generates a level and switches the dungeon to it.
	 */
	public void generateLevel() {
		this.originalName = DungeonNameGenerator.generate();
		this.name = this.originalName;
		
		if (level != null) {
			level.getEntityStore().removeEntity(player);
		} else {
			level = new Level(this, LEVEL_WIDTH, LEVEL_HEIGHT, -1);
			levels.put(level.getUUID(), level);
		}
		
		level.generate(null, GeneratorStandard.class);
		
		if (player == null) {
			player = new Player(
				this,
				level,
				level.getSpawnX(),
				level.getSpawnY(),
				settings.getPlayerName(),
				new RoleWizard()
			);
		} else {
			player.setPosition(level.getSpawnX(), level.getSpawnY());
		}
		
		player.setLevel(level);
		level.getEntityStore().addEntity(player);
		
		triggerEvent(new LevelChangeEvent(level));
	}

	/**
	 * Saves this dungeon as dungeon.save.gz in the game data directory.
	 */
	public void save() {
		if (!dataDir.toFile().isDirectory() && !dataDir.toFile().mkdirs()) {
			JRogue.getLogger().error("Failed to create save directory. Permissions problem?");
			return;
		}
		
		File file = new File(Paths.get(dataDir.toString(), "dungeon.save.gz").toString());
		
		try (
			GZIPOutputStream os = new GZIPOutputStream(new FileOutputStream(file));
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"))
		) {
			JSONObject serialisedDungeon = new JSONObject();
			serialise(serialisedDungeon);
			writer.append(serialisedDungeon.toString());
		} catch (Exception e) {
			ErrorHandler.error("Error saving dungeon", e);
		}
	}

	/**
	 * @return The dungeon specified in dungeon.save.gz in the game data directory.
	 */
	public static Dungeon load() {
		Dungeon dungeon = new Dungeon();
		
		File file = new File(Paths.get(dataDir.toString(), "dungeon.save.gz").toString());
		
		if (file.exists()) {
			try (
				GZIPInputStream is = new GZIPInputStream(new FileInputStream(file));
				BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"))
			) {
				JSONTokener tokener = new JSONTokener(reader);
				JSONObject serialisedDungeon = new JSONObject(tokener);
				
				dungeon.unserialise(serialisedDungeon);
				return dungeon;
			} catch (Exception e) {
				ErrorHandler.error("Error loading dungeon", e);
			}
		}
		
		dungeon.generateLevel();
		return dungeon;
	}

	@Override
	public void serialise(JSONObject obj) {
		obj.put("version", JRogue.VERSION);
		obj.put("name", getName());
		obj.put("originalName", getOriginalName());
		obj.put("turn", getTurn());
		obj.put("passiveSoundCounter", passiveSoundCounter);
		obj.put("monsterSpawnCounter", monsterSpawnCounter);
		
		JSONObject serialisedLevels = new JSONObject();
		levels.forEach((uuid, level) -> {
			JSONObject j = new JSONObject();
			level.serialise(j);
			serialisedLevels.put(uuid.toString(), j);
		});
		obj.put("levels", serialisedLevels);

		serialisePersistence(obj);
	}

	@Override
	public void unserialise(JSONObject obj) {
		try {
			String version = obj.optString("version");
			
			if (!version.equals(JRogue.VERSION)) {
				int dialogResult = JOptionPane.showConfirmDialog(
					null,
					"This save was made in a different version of " +
					"JRogue. Would you still like to try and load it?",
					"JRogue",
					JOptionPane.YES_NO_CANCEL_OPTION
				);
				
				switch (dialogResult) {
					case JOptionPane.YES_OPTION:
						break;
					case JOptionPane.NO_OPTION:
						File file = new File(Paths.get(dataDir.toString(), "dungeon.save").toString());
						
						if (file.exists() && !file.delete()) {
							JRogue.getLogger().error("Failed to delete save file. Panic!");
						}
						
						JOptionPane.showMessageDialog(null, "Please restart JRogue.");
						System.exit(0);
					default:
						System.exit(0);
						break;
				}
			}
			
			name = obj.getString("name");
			originalName = obj.getString("originalName");
			turn = obj.getInt("turn");
			passiveSoundCounter = obj.getInt("passiveSoundCounter");
			monsterSpawnCounter = obj.getInt("monsterSpawnCounter");
			
			JSONObject serialisedLevels = obj.getJSONObject("levels");
			serialisedLevels.keySet().forEach(k -> {
				UUID uuid = UUID.fromString(k);
				JSONObject serialisedLevel = serialisedLevels.getJSONObject(k);
				Level.createFromJSON(uuid, serialisedLevel, this).ifPresent(level -> levels.put(uuid, level));
			});
			
			if (player == null) {
				File file = new File(Paths.get(dataDir.toString(), "dungeon.save").toString());
				
				if (file.exists() && !file.delete()) {
					JRogue.getLogger().error("Failed to delete save file. Panic!");
				}
				
				JOptionPane.showMessageDialog(null, "Please restart JRogue.");
				System.exit(0);
				
				return;
			}

			level = player.getLevel();
			triggerEvent(new LevelChangeEvent(level));
			
			level.getLightStore().buildLight(true);
			level.getVisibilityStore().updateSight(player);
		} catch (Exception e) {
			ErrorHandler.error("Error loading dungeon", e);
		}

		unserialisePersistence(obj);
	}

	/**
	 * Deletes the game save file.
	 */
	public void deleteSave() {
		File file = new File(Paths.get(dataDir.toString(), "dungeon.save").toString());
		
		if (file.exists() && !file.delete()) {
			JRogue.getLogger().error("Failed to delete save file. Panic!");
		}
	}

	/**
	 * Creates a new level.
	 * @param depth The depth the level is located at.
	 * @param sourceTile The tile the player came from (usually a staircase).
	 * @param generatorClass The {@link jr.dungeon.generators.DungeonGenerator} to use to generate this level.
	 * @return The generated level.
	 */
	public Level newLevel(int depth, Tile sourceTile, Class<? extends DungeonGenerator> generatorClass) {
		Level level = new Level(UUID.randomUUID(), this, LEVEL_WIDTH, LEVEL_HEIGHT, depth);
		levels.put(level.getUUID(), level);
		level.generate(sourceTile, generatorClass);
		return level;
	}

	/**
	 * Switches the level to <code>level</code>.
	 * @param level The level to switch to.
	 * @param x The x coordinate to spawn the player at.
	 * @param y The y coordinate to spawn the player at.
	 */
	public void changeLevel(Level level, int x, int y) {
		this.level = level;
		
		getPlayer().getLevel().getEntityStore().removeEntity(player);
		getPlayer().getLevel().getEntityStore().processEntityQueues(false);
		
		getPlayer().setLevel(level);
		level.getEntityStore().addEntity(player);
		level.getEntityStore().processEntityQueues(false);
		
		getPlayer().setPosition(x, y);
		
		turn(true);
		
		triggerEvent(new LevelChangeEvent(level));
		
		level.getEntityStore().getEntities().forEach(e -> triggerEvent(new EntityAddedEvent(e, false)));
	}

	/**
	 * Triggers the "Really quit without saving?" prompt.
	 */
	public void quit() {
		prompt(new Prompt("Really quit without saving?", new char[]{'y', 'n'}, true, new Prompt.PromptCallback() {
			@Override
			public void onNoResponse() {}
			
			@Override
			public void onInvalidResponse(char response) {}
			
			@Override
			public void onResponse(char response) {
				if (response == 'y') {
					File file = new File(Paths.get(dataDir.toString(), "dungeon.save").toString());
					
					if (file.exists() && !file.delete()) {
						JRogue.getLogger().error("Failed to delete save file. Panic!"); // fuck you
					}
					
					triggerEvent(new QuitEvent());
				}
			}
		}));
	}

	/**
	 * Triggers the "Really save and quit?" prompt.
	 */
	public void saveAndQuit() {
		prompt(new Prompt("Really save and quit?", new char[]{'y', 'n'}, true, new Prompt.PromptCallback() {
			@Override
			public void onNoResponse() {}
			
			@Override
			public void onInvalidResponse(char response) {}
			
			@Override
			public void onResponse(char response) {
				if (response == 'y' && player.isAlive()) {
					save();
					triggerEvent(new SaveAndQuitEvent());
				}
			}
		}));
	}

	/**
	 * Adds an event listener to this dungeon.
	 * @param listener The event listener to add.
	 */
	public void addListener(DungeonEventListener listener) {
		listeners.add(listener);
	}

	/**
	 * Removes an event listener from this dungeon.
	 * @param listener The event listener to remove.
	 */
	public void removeListener(DungeonEventListener listener) {
		listeners.remove(listener);
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

	/**
	 * Starts/resumes a game. Should be called after the first level in the session is loaded.
	 */
	public void start() {
		triggerEvent(new LevelChangeEvent(level));
		triggerEvent(new BeforeGameStartedEvent(turn <= 0));
		
		if (turn <= 0) {
			You("drop down into [CYAN]%s[].", this.name);
			turn(true);
			triggerEvent(new GameStartedEvent(true));
		} else {
			triggerEvent(new BeforeTurnEvent(turn));
			log("Welcome back to [CYAN]%s[].", this.name);
			level.getEntityStore().processEntityQueues(false);
			triggerEvent(new TurnEvent(turn));
			triggerEvent(new GameStartedEvent(false));
		}
	}

	/**
	 * Displays a message in the dungeon's event log.
	 * All messages are formatted using {@link java.lang.String}'s <code>format</code> method.
	 * @param s The format used for the message.
	 * @param objects The format parameters.
	 */
	public void log(String s, Object... objects) {
		String logString = String.format(s, objects);
		
		String printedLogString = logString;
		printedLogString = printedLogString.replaceAll("\\[]", "\u001b[0m");
		printedLogString = printedLogString.replaceAll("\\[RED]", "\u001b[31m");
		printedLogString = printedLogString.replaceAll("\\[ORANGE]", "\u001b[31m");
		printedLogString = printedLogString.replaceAll("\\[YELLOW]", "\u001b[33m");
		printedLogString = printedLogString.replaceAll("\\[GREEN]", "\u001b[32m");
		printedLogString = printedLogString.replaceAll("\\[BLUE]", "\u001b[34m");
		printedLogString = printedLogString.replaceAll("\\[CYAN]", "\u001b[36m");
		printedLogString = printedLogString + "\u001b[0m";
		JRogue.getLogger().log(gameLogLevel, printedLogString);
		
		triggerEvent(new LogEvent(logString));
	}

	/**
	 * Triggers a {@link jr.dungeon.Prompt}.
	 * @param prompt The prompt to trigger.
	 */
	public void prompt(Prompt prompt) {
		this.prompt = prompt;
		triggerEvent(new PromptEvent(prompt));
	}

	/**
	 * Responds to a prompt.
	 * @param response The char response to send.
	 */
	public void promptRespond(char response) {
		if (prompt != null) {
			Prompt prompt = this.prompt;
			prompt.respond(response);
			
			if (prompt == this.prompt) {
				this.prompt = null;
				triggerEvent(new PromptEvent(null));
			}
		}
	}

	/**
	 * Ends a prompt, behaves like pressing ESC in the game.
	 */
	public void escapePrompt() {
		if (prompt != null) {
			Prompt prompt = this.prompt;
			this.prompt = null;
			prompt.escape();
			
			triggerEvent(new PromptEvent(null));
		}
	}

	/**
	 * @return true if there is an active prompt.
	 */
	public boolean hasPrompt() {
		return prompt != null;
	}

	/**
	 * @return true if there is an active prompt that cannot be forcefully ended (escaped).
	 */
	public boolean isPromptEscapable() {
		return prompt != null && prompt.isEscapable();
	}
	
	public void turn() {
		turn(false);
	}

	/**
	 * Triggers the next turn, increasing the turn counter, and updating all entities.
	 */
	public void turn(boolean isStart) {
		if (!player.isAlive()) {
			return;
		}
		
		triggerEvent(new BeforeTurnEvent(turn + 1));
		somethingHappened = false;
		
		level.getEntityStore().processEntityQueues(!isStart);
		
		player.setMovementPoints(player.getMovementPoints() - NORMAL_SPEED);
		
		do {
			boolean entitiesCanMove = false;
			
			do {
				if (!player.isAlive()) {
					break;
				}
				
				entitiesCanMove = moveEntities();
				
				if (player.getMovementPoints() > NORMAL_SPEED) {
					break;
				}
			} while (entitiesCanMove);
			
			if (!entitiesCanMove && player.getMovementPoints() < NORMAL_SPEED) {
				for (Entity entity : level.getEntityStore().getEntities()) {
					if (!player.isAlive()) {
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
				
				if (player.getMovementPoints() < 0) {
					player.setMovementPoints(0);
				}
				
				turn++;
				
				update();
			}
		} while (player.isAlive() && player.getMovementPoints() < NORMAL_SPEED);
		
		if (player.isAlive()) {
			player.move();
		} else {
			return;
		}
		
		level.getEntityStore().processEntityQueues(!isStart);
		
		level.getVisibilityStore().updateSight(player);
		level.getLightStore().buildLight(false);
		
		for (Iterator<DungeonEvent> iterator = eventQueueNextTurn.iterator(); iterator.hasNext(); ) {
			DungeonEvent event = iterator.next();
			triggerEvent(event, DungeonEventInvocationTime.TURN_COMPLETE);
			iterator.remove();
		}
		
		triggerEvent(new TurnEvent(turn));
	}

	/**
	 * Makes all entities make their next move.
	 * @return false if nobody moved.
	 */
	private boolean moveEntities() {
		AtomicBoolean somebodyCanMove = new AtomicBoolean(false);
		
		level.getEntityStore().getEntities().stream()
			.filter(e -> e instanceof EntityTurnBased)
			.filter(e -> !(e instanceof Player))
			.filter(e -> !(((EntityTurnBased) e).getMovementPoints() < NORMAL_SPEED))
			.forEach(e -> {
				if (e instanceof EntityLiving && !((EntityLiving) e).isAlive()) {
					return;
				}
				
				EntityTurnBased tbe = (EntityTurnBased) e;
				tbe.setMovementPoints(tbe.getMovementPoints() - NORMAL_SPEED);
				
				if (tbe.getMovementPoints() >= NORMAL_SPEED) {
					somebodyCanMove.set(true);
				}
				
				tbe.move();
			});
		
		return somebodyCanMove.get();
	}

	/**
	 * Updates the dungeon, which includes playing sounds and spawning monsters.
	 */
	private void update() {
		if (--passiveSoundCounter <= 0) {
			emitPassiveSounds();
			
			passiveSoundCounter = RandomUtils.roll(3, 4);
		}
		
		if (
			level.getEntityStore().getHostileMonsters().size() < Math.abs(level.getDepth() * 2 + 10) &&
				--monsterSpawnCounter <= 0
			) {
			level.getMonsterSpawner().spawnNewMonsters();
			
			monsterSpawnCounter = RandomUtils.random(PROBABILITY_MONSTER_SPAWN_COUNTER);
		}
	}

	/**
	 * Emits passive sounds.
	 */
	private void emitPassiveSounds() {
		List<Entity> emitters = level.getEntityStore().getEntities().stream()
			.filter(e -> e instanceof PassiveSoundEmitter)
			.collect(Collectors.toList());
		
		if (emitters.isEmpty()) {
			return;
		}
		
		Collections.shuffle(emitters);
		PassiveSoundEmitter soundEmitter = (PassiveSoundEmitter) emitters.get(0);
		
		if (rand.nextFloat() <= soundEmitter.getSoundProbability()) {
			String sound = RandomUtils.randomFrom(soundEmitter.getSounds());
			
			log(sound);
		}
	}

	/**
	 * Make a wish, used in debug mode.
	 * @see jr.dungeon.wishes.Wishes
	 * @param wish The wish to make.
	 */
	public void wish(String wish) {
		Wishes.get().makeWish(this, wish);
	}

	/**
	 * @param uuid A level UUID.
	 * @return The level with the specified UUID.
	 */
	public Level getLevelFromUUID(UUID uuid) {
		return levels.get(uuid);
	}

	/**
	 * @return A JSONObject that persists across game sessions.
	 */
	@Override
	public JSONObject getPersistence() {
		return persistence;
	}

	/**
	 * Triggers a dungeon event, notifying all listeners.
	 * @param event The event to trigger.
	 */
	public void triggerEvent(DungeonEvent event) {
		eventQueueNextTurn.add(event);
		triggerEvent(event, DungeonEventInvocationTime.IMMEDIATELY);
	}

	/**
	 * Triggers a dungeon event, notifying all listeners.
	 * @param event The event to trigger.
	 * @param invocationTime When to trigger the event. <code>IMMEDIATELY</code> to trigger it right now or <code>TURN_COMPLETE</code> to delay it to the next turn.
	 */
	@SuppressWarnings("unchecked")
	public void triggerEvent(DungeonEvent event, DungeonEventInvocationTime invocationTime) {
		listeners.forEach(l -> invokeEvent(l, event, invocationTime));
		
		if (level != null) {
			level.getEntityStore().getEntities().forEach(e -> {
				invokeEvent(e, event, invocationTime);
				e.getSubListeners().forEach(l2 -> {
					if (l2 != null) {
						invokeEvent(l2, event, invocationTime);
					}
				});
			});
		}
	}
	
	@SuppressWarnings("unchecked")
	private void invokeEvent(DungeonEventListener listener, DungeonEvent event, DungeonEventInvocationTime invocationTime) {
		event.setDungeon(this);
		
		Class<?> listenerClass = listener.getClass();
		ArrayList<Method> listenerMethods = new ArrayList<>();
		
		while (listenerClass != null) {
			Method[] methods = listenerClass.getDeclaredMethods();
			
			for (Method method : methods) {
				if (
					method.isAnnotationPresent(DungeonEventHandler.class) &&
					method.getParameterCount() == 1 &&
					method.getParameterTypes()[0].isAssignableFrom(event.getClass())
				) {
					listenerMethods.add(method);
				}
			}
			
			listenerClass = listenerClass.getSuperclass();
		}
		
		listenerMethods.forEach(m -> {
			m.setAccessible(true); // ha ha
			
			if (event.isCancelled()) {
				return;
			}
			
			DungeonEventHandler annotation = m.getAnnotation(DungeonEventHandler.class);
			
			if (annotation.selfOnly() && !event.isSelf(listener)) {
				return;
			}
			
			if (annotation.invocationTime() != invocationTime) {
				return;
			}
			
			try {
				m.invoke(listener, event);
			} catch (IllegalAccessException | InvocationTargetException e) {
				ErrorHandler.error("Error triggering event " + event.getClass().getSimpleName(), e);
			}
		});
	}
}
