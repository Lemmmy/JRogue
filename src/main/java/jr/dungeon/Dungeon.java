package jr.dungeon;

import jr.ErrorHandler;
import jr.JRogue;
import jr.Settings;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.events.EntityAddedEvent;
import jr.dungeon.entities.player.Player;
import jr.dungeon.events.*;
import jr.dungeon.generators.DungeonGenerator;
import jr.dungeon.generators.DungeonNameGenerator;
import jr.dungeon.generators.GeneratorStandard;
import jr.dungeon.tiles.Tile;
import jr.dungeon.wishes.Wishes;
import jr.utils.*;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;
import org.json.JSONTokener;

import javax.swing.*;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

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
	 * The 'GAME' log level.
	 */
	private static org.apache.logging.log4j.Level gameLogLevel;
	
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
	
	@Getter private EventSystem eventSystem;
	
	@Getter private TurnSystem turnSystem;
	
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
	
	private List<String> logHistory = new LinkedList<>();
	
	/**
	 * The entire Dungeon object. This object contains all information about the actual game state, including the turn,
	 * levels, and player.
	 */
	public Dungeon() {
		this.settings = JRogue.getSettings();
		
		eventSystem = new EventSystem(this);
		turnSystem = new TurnSystem(this);
		
		gameLogLevel = org.apache.logging.log4j.Level.getLevel("GAME");
	}
	
	/**
	 * Starts/resumes a game. Should be called after the first level in the session is loaded.
	 */
	public void start() {
		eventSystem.triggerEvent(new LevelChangeEvent(level));
		eventSystem.triggerEvent(new BeforeGameStartedEvent(turnSystem.getTurn() <= 0));
		
		if (turnSystem.getTurn() <= 0) {
			You("drop down into [CYAN]%s[].", this.name);
			turnSystem.turn(true);
			eventSystem.triggerEvent(new GameStartedEvent(true));
		} else {
			eventSystem.triggerEvent(new BeforeTurnEvent(turnSystem.getTurn()));
			log("Welcome back to [CYAN]%s[].", this.name);
			level.getEntityStore().processEntityQueues(false);
			eventSystem.triggerEvent(new TurnEvent(turnSystem.getTurn()));
			eventSystem.triggerEvent(new GameStartedEvent(false));
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
		
		turnSystem.turn(true);
		
		eventSystem.triggerEvent(new LevelChangeEvent(level));
		
		level.getEntityStore().getEntities().forEach(e -> eventSystem.triggerEvent(new EntityAddedEvent(e, false)));
	}
	
	
	/**
	 * Switches the level to <code>level</code>.
	 * @param level The level to switch to.
	 * @param point The position to spawn the player at.
	 */
	public void changeLevel(Level level, Point point) {
		changeLevel(level, point.getX(), point.getY());
	}
	
	@Override
	public List<String> getLogHistory() {
		return logHistory;
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
		
		logHistory.add(logString);
		eventSystem.triggerEvent(new LogEvent(logString));
	}

	/**
	 * Triggers a {@link jr.dungeon.Prompt}.
	 * @param prompt The prompt to trigger.
	 */
	public void prompt(Prompt prompt) {
		this.prompt = prompt;
		eventSystem.triggerEvent(new PromptEvent(prompt));
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
				eventSystem.triggerEvent(new PromptEvent(null));
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
			
			eventSystem.triggerEvent(new PromptEvent(null));
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
					File file = new File(Paths.get(dataDir.toString(), "dungeon.save.gz").toString());
					
					if (file.exists() && !file.delete()) {
						ErrorHandler.error("Failed to delete save file. Please delete the file at " + file.getAbsolutePath(), null);
					}
					
					eventSystem.triggerEvent(new QuitEvent());
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
					eventSystem.triggerEvent(new SaveAndQuitEvent());
				}
			}
		}));
	}
	
	/**
	 * Randomly generates a level and switches the dungeon to it.
	 */
	public Level generateLevel() {
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
				settings.getRole()
			);
		} else {
			player.setPosition(level.getSpawnX(), level.getSpawnY());
		}
		
		player.setLevel(level);
		level.getEntityStore().addEntity(player);
		
		eventSystem.triggerEvent(new LevelChangeEvent(level));
		
		return level;
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
	
	public static boolean canLoad() {
		return new File(Paths.get(dataDir.toString(), "dungeon.save.gz").toString()).exists();
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
		
		Level firstLevel = dungeon.generateLevel();
		dungeon.getPersistence().put("firstLevel", firstLevel.getUUID().toString());
		return dungeon;
	}
	
	@Override
	public void serialise(JSONObject obj) {
		obj.put("version", JRogue.VERSION);
		obj.put("name", getName());
		obj.put("originalName", getOriginalName());
		
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
						File file = new File(Paths.get(dataDir.toString(), "dungeon.save.gz").toString());
						
						if (file.exists() && !file.delete()) {
							JRogue.getLogger().error("Failed to delete save file. Panic!");
						}
						
						JOptionPane.showMessageDialog(null, "Please restart JRogue.");
						System.exit(0);
						break;
					default:
						System.exit(0);
						break;
				}
			}
			
			name = obj.getString("name");
			originalName = obj.getString("originalName");
			
			JSONObject serialisedLevels = obj.getJSONObject("levels");
			serialisedLevels.keySet().forEach(k -> {
				UUID uuid = UUID.fromString(k);
				JSONObject serialisedLevel = serialisedLevels.getJSONObject(k);
				Level.createFromJSON(uuid, serialisedLevel, this).ifPresent(level -> levels.put(uuid, level));
			});
			
			if (player == null) {
				File file = new File(Paths.get(dataDir.toString(), "dungeon.save.gz").toString());
				
				if (file.exists() && !file.delete()) {
					JRogue.getLogger().error("Failed to delete save file. Panic!");
				}
				
				JOptionPane.showMessageDialog(null, "Please restart JRogue.");
				System.exit(0);
				
				return;
			}
			
			level = player.getLevel();
			eventSystem.triggerEvent(new LevelChangeEvent(level));
			
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
		File file = new File(Paths.get(dataDir.toString(), "dungeon.save.gz").toString());
		
		if (file.exists() && !file.delete()) {
			ErrorHandler.error("Failed to delete save file. Please delete the file at " + file.getAbsolutePath(), null);
		}
	}
}
