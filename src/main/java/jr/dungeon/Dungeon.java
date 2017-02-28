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
import org.json.JSONObject;
import org.json.JSONTokener;

import javax.swing.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class Dungeon implements Messenger, Serialisable, Persisting {
	public static final int NORMAL_SPEED = 12;
	
	public static final int LEVEL_WIDTH = 90;
	public static final int LEVEL_HEIGHT = 40;
	
	private static final Range<Integer> PROBABILITY_MONSTER_SPAWN_COUNTER = Range.between(40, 100);
	
	private static org.apache.logging.log4j.Level gameLogLevel;
	
	private final List<DungeonEventListener> listeners = new ArrayList<>();
	private final List<DungeonEvent> eventQueueNextTurn = new LinkedList<>();
	
	private Pcg32 rand = new Pcg32();
	
	/**
	 * Randomly generated name of this dungeon
	 */
	@Getter @Setter private String originalName;
	
	/**
	 * User-chosen name of this dungeon
	 */
	@Getter @Setter private String name;
	
	private Map<UUID, Level> levels = new HashMap<>();
	@Getter private Level level;
	@Getter @Setter private Player player;
	
	@Getter private long turn = 0;
	@Getter private long exerciseCounter = 500;
	@Getter private long passiveSoundCounter = 0;
	@Getter private long monsterSpawnCounter = 50;
	
	@Getter private boolean somethingHappened;
	@Getter @Setter private boolean doingMassAction;
	
	@Getter private Prompt prompt;
	private Settings settings;
	
	private static Path dataDir = OperatingSystem.get().getAppDataDir().resolve("jrogue");

	private final JSONObject persistence = new JSONObject();

	public Dungeon() {
		this.settings = JRogue.getSettings();
		
		gameLogLevel = org.apache.logging.log4j.Level.getLevel("GAME");
	}
	
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
		obj.put("exerciseCounter", exerciseCounter);
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
			exerciseCounter = obj.getInt("exerciseCounter");
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
	
	public void deleteSave() {
		File file = new File(Paths.get(dataDir.toString(), "dungeon.save").toString());
		
		if (file.exists() && !file.delete()) {
			JRogue.getLogger().error("Failed to delete save file. Panic!");
		}
	}
	
	public Level newLevel(int depth, Tile sourceTile, Class<? extends DungeonGenerator> generatorClass) {
		Level level = new Level(UUID.randomUUID(), this, LEVEL_WIDTH, LEVEL_HEIGHT, depth);
		levels.put(level.getUUID(), level);
		level.generate(sourceTile, generatorClass);
		return level;
	}
	
	public void changeLevel(Level level, int x, int y) {
		this.level = level;
		
		getPlayer().getLevel().getEntityStore().removeEntity(player);
		getPlayer().getLevel().getEntityStore().processEntityQueues();
		
		getPlayer().setLevel(level);
		level.getEntityStore().addEntity(player);
		level.getEntityStore().processEntityQueues();
		
		getPlayer().setPosition(x, y);
		
		turn();
		
		triggerEvent(new LevelChangeEvent(level));
		
		level.getEntityStore().getEntities().forEach(e -> triggerEvent(new EntityAddedEvent(e)));
	}
	
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
	
	public void addListener(DungeonEventListener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(DungeonEventListener listener) {
		listeners.remove(listener);
	}
	
	public void markSomethingHappened() {
		somethingHappened = true;
		
		// TODO: trigger event here?
	}
	
	public void start() {
		triggerEvent(new LevelChangeEvent(level));
		triggerEvent(new BeforeGameStartedEvent(turn <= 0));
		
		if (turn <= 0) {
			You("drop down into [CYAN]%s[].", this.name);
			turn();
			triggerEvent(new GameStartedEvent(true));
		} else {
			triggerEvent(new BeforeTurnEvent(turn));
			log("Welcome back to [CYAN]%s[].", this.name);
			level.getEntityStore().processEntityQueues();
			triggerEvent(new TurnEvent(turn));
			triggerEvent(new GameStartedEvent(false));
		}
	}
	
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
	
	public void prompt(Prompt prompt) {
		this.prompt = prompt;
		triggerEvent(new PromptEvent(prompt));
	}
	
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
	
	public void escapePrompt() {
		if (prompt != null) {
			Prompt prompt = this.prompt;
			this.prompt = null;
			prompt.escape();
			
			triggerEvent(new PromptEvent(null));
		}
	}
	
	public boolean hasPrompt() {
		return prompt != null;
	}
	
	public boolean isPromptEscapable() {
		return prompt != null && prompt.isEscapable();
	}
	
	public void turn() {
		if (!player.isAlive()) {
			return;
		}
		
		triggerEvent(new BeforeTurnEvent(turn + 1));
		somethingHappened = false;
		
		level.getEntityStore().processEntityQueues();
		
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
		
		level.getEntityStore().processEntityQueues();
		
		level.getVisibilityStore().updateSight(player);
		level.getLightStore().buildLight(false);
		
		for (Iterator<DungeonEvent> iterator = eventQueueNextTurn.iterator(); iterator.hasNext(); ) {
			DungeonEvent event = iterator.next();
			triggerEvent(event, DungeonEventInvocationTime.NEXT_TURN);
			iterator.remove();
		}
		
		triggerEvent(new TurnEvent(turn));
	}
	
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
	
	public void wish(String wish) {
		Wishes.get().makeWish(this, wish);
	}
	
	public Level getLevelFromUUID(UUID uuid) {
		return levels.get(uuid);
	}

	@Override
	public JSONObject getPersistence() {
		return persistence;
	}
	
	public void triggerEvent(DungeonEvent event) {
		eventQueueNextTurn.add(event);
		triggerEvent(event, DungeonEventInvocationTime.IMMEDIATELY);
	}
	
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
	public void invokeEvent(DungeonEventListener listener, DungeonEvent event, DungeonEventInvocationTime invocationTime) {
		event.setDungeon(this);
		
		Arrays.stream(listener.getClass().getMethods())
			.filter(m -> m.isAnnotationPresent(DungeonEventHandler.class))
			.filter(m -> m.getParameterCount() == 1)
			.filter(m -> m.getParameterTypes()[0].isAssignableFrom(event.getClass()))
			.forEach(m -> {
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
