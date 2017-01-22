package pw.lemmmy.jrogue.dungeon;

import com.github.alexeyr.pcg.Pcg32;
import org.apache.commons.lang3.Range;
import org.json.JSONObject;
import org.json.JSONTokener;
import pw.lemmmy.jrogue.ErrorHandler;
import pw.lemmmy.jrogue.JRogue;
import pw.lemmmy.jrogue.Settings;
import pw.lemmmy.jrogue.dungeon.entities.Entity;
import pw.lemmmy.jrogue.dungeon.entities.EntityLiving;
import pw.lemmmy.jrogue.dungeon.entities.EntityTurnBased;
import pw.lemmmy.jrogue.dungeon.entities.PassiveSoundEmitter;
import pw.lemmmy.jrogue.dungeon.entities.player.Player;
import pw.lemmmy.jrogue.dungeon.entities.player.roles.RoleWizard;
import pw.lemmmy.jrogue.dungeon.generators.DungeonGenerator;
import pw.lemmmy.jrogue.dungeon.generators.DungeonNameGenerator;
import pw.lemmmy.jrogue.dungeon.generators.GeneratorStandard;
import pw.lemmmy.jrogue.dungeon.tiles.Tile;
import pw.lemmmy.jrogue.utils.OperatingSystem;
import pw.lemmmy.jrogue.utils.Persisting;
import pw.lemmmy.jrogue.utils.RandomUtils;
import pw.lemmmy.jrogue.utils.Serialisable;

import javax.swing.*;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class Dungeon implements Messenger, Serialisable, Persisting {
	public static final int NORMAL_SPEED = 12;
	
	private static final int LEVEL_WIDTH = 90;
	private static final int LEVEL_HEIGHT = 40;
	
	private static final Range<Integer> PROBABILITY_MONSTER_SPAWN_COUNTER = Range.between(40, 100);
	
	private static org.apache.logging.log4j.Level gameLogLevel;
	
	private final List<Listener> listeners = new ArrayList<>();
	
	private Pcg32 rand = new Pcg32();
	
	/**
	 * Randomly generated name of this dungeon
	 */
	private String originalName;
	
	/**
	 * User-chosen name of this dungeon
	 */
	private String name;
	
	private Map<UUID, Level> levels = new HashMap<>();
	private Level level;
	private Player player;
	
	private long turn = 0;
	private long exerciseCounter = 500;
	private long passiveSoundCounter = 0;
	private long monsterSpawnCounter = 50;
	
	private Prompt prompt;
	private Settings settings;
	
	private static Path dataDir = OperatingSystem.get().getAppDataDir().resolve("jrogue");

	private final JSONObject persistence = new JSONObject();

	public Dungeon(Settings settings) {
		this.settings = settings;
		
		gameLogLevel = org.apache.logging.log4j.Level.getLevel("GAME");
	}
	
	public void generateLevel() {
		this.originalName = DungeonNameGenerator.generate();
		this.name = this.originalName;
		
		if (level != null) {
			level.removeEntity(player);
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
		level.addEntity(player);
		
		listeners.forEach(l -> l.onLevelChange(level));
	}
	
	public void save() {
		if (!dataDir.toFile().isDirectory() && !dataDir.toFile().mkdirs()) {
			JRogue.getLogger().error("Failed to create save directory. Permissions problem?");
			return;
		}
		
		File file = new File(Paths.get(dataDir.toString(), "dungeon.save").toString());
		
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
	
	public static Dungeon load(Settings settings) {
		Dungeon dungeon = new Dungeon(settings);
		
		File file = new File(Paths.get(dataDir.toString(), "dungeon.save").toString());
		
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
			listeners.forEach(l -> l.onLevelChange(level));
			
			level.buildLight(true);
			level.updateSight(player);
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
		
		getPlayer().getLevel().removeEntity(player);
		getPlayer().getLevel().processEntityQueues();
		
		getPlayer().setLevel(level);
		level.addEntity(player);
		level.processEntityQueues();
		
		getPlayer().setPosition(x, y);
		
		turn();
		
		listeners.forEach(l -> l.onLevelChange(level));
		
		level.getEntities().forEach(e -> listeners.forEach(l -> l.onEntityAdded(e)));
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
					
					listeners.forEach(Listener::onQuit);
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
					listeners.forEach(Listener::onSaveAndQuit);
				}
			}
		}));
	}
	
	public void addListener(Listener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}
	
	public String getOriginalName() {
		return originalName;
	}
	
	public String getName() {
		return name;
	}
	
	public void rerollName() {
		this.originalName = DungeonNameGenerator.generate();
		this.name = this.originalName;
	}
	
	public void start() {
		if (turn <= 0) {
			You("drop down into [CYAN]%s[].", this.name);
			turn();
		} else {
			listeners.forEach(l -> l.onBeforeTurn(turn));
			log("Welcome back to [CYAN]%s[].", this.name);
			level.processEntityQueues();
			listeners.forEach(l -> l.onTurn(turn));
		}
	}
	
	public void log(String s, Object... objects) {
		String logString = String.format(s, objects);
		logString = logString.replaceAll("\\[]", "\u001b[0m");
		logString = logString.replaceAll("\\[RED]", "\u001b[31m");
		logString = logString.replaceAll("\\[ORANGE]", "\u001b[31m");
		logString = logString.replaceAll("\\[YELLOW]", "\u001b[33m");
		logString = logString.replaceAll("\\[GREEN]", "\u001b[32m");
		logString = logString.replaceAll("\\[BLUE]", "\u001b[34m");
		logString = logString.replaceAll("\\[CYAN]", "\u001b[36m");
		logString = logString + "\u001b[0m";
		JRogue.getLogger().log(gameLogLevel, logString);
		
		listeners.forEach(l -> l.onLog(String.format(s, objects)));
	}
	
	public void prompt(Prompt prompt) {
		this.prompt = prompt;
		listeners.forEach(l -> l.onPrompt(prompt));
	}
	
	public void promptRespond(char response) {
		if (prompt != null) {
			Prompt prompt = this.prompt;
			prompt.respond(response);
			
			if (prompt == this.prompt) {
				this.prompt = null;
				listeners.forEach(l -> l.onPrompt(null));
			}
		}
	}
	
	public void escapePrompt() {
		if (prompt != null) {
			Prompt prompt = this.prompt;
			this.prompt = null;
			prompt.escape();
			
			listeners.forEach(l -> l.onPrompt(null));
		}
	}
	
	public boolean hasPrompt() {
		return prompt != null;
	}
	
	public boolean isPromptEscapable() {
		return prompt != null && prompt.isEscapable();
	}
	
	public void showContainer(Entity containerEntity) {
		listeners.forEach(l -> l.onContainerShow(containerEntity));
	}
	
	public void showPath(pw.lemmmy.jrogue.utils.Path path) {
		listeners.forEach(l -> l.onPathShow(path));
	}
	
	public void turn() {
		if (!player.isAlive()) {
			return;
		}
		
		listeners.forEach(l -> l.onBeforeTurn(turn + 1));
		level.processEntityQueues();
		
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
				for (Entity entity : level.getEntities()) {
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
		
		level.processEntityQueues();
		
		level.updateSight(player);
		level.buildLight(false);
		
		listeners.forEach(l -> l.onTurn(turn));
	}
	
	private boolean moveEntities() {
		AtomicBoolean somebodyCanMove = new AtomicBoolean(false);
		
		level.getEntities().stream()
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
			level.getHostileMonsters().size() < Math.abs(level.getDepth() * 2 + 10) &&
				--monsterSpawnCounter <= 0
			) {
			level.spawnNewMonsters();
			
			monsterSpawnCounter = RandomUtils.random(PROBABILITY_MONSTER_SPAWN_COUNTER);
		}
	}
	
	private void emitPassiveSounds() {
		List<Entity> emitters = level.getEntities().stream()
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
		Wish.wish(this, wish);
	}
	
	public Level getLevel() {
		return level;
	}
	
	public Level getLevelFromUUID(UUID uuid) {
		return levels.get(uuid);
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public void setPlayer(Player player) {
		this.player = player;
	}
	
	public long getTurn() {
		return turn;
	}
	
	public void setTurn(long turn) {
		this.turn = turn;
	}
	
	public void entityAdded(Entity entity) {
		listeners.forEach(l -> l.onEntityAdded(entity));
	}
	
	public void entityMoved(Entity entity, int lastX, int lastY, int newX, int newY) {
		listeners.forEach(l -> l.onEntityMoved(entity, lastX, lastY, newX, newY));
	}
	
	public void entityAttacked(Entity entity, int x, int y, int roll, int toHit) {
		listeners.forEach(l -> l.onEntityAttacked(entity, x, y, roll, toHit));
	}
	
	public void entityRemoved(Entity entity) {
		listeners.forEach(l -> l.onEntityRemoved(entity));
	}

	@Override
	public JSONObject getPersistence() {
		return persistence;
	}

	public interface Listener {
		default void onLevelChange(Level level) {}
		
		default void onBeforeTurn(long turn) {}
		
		default void onTurn(long turn) {}
		
		default void onLog(String log) {}
		
		default void onPrompt(Prompt prompt) {}
		
		default void onContainerShow(Entity containerEntity) {}
		
		default void onPathShow(pw.lemmmy.jrogue.utils.Path path) {}
		
		default void onEntityAdded(Entity entity) {}
		
		default void onEntityMoved(Entity entity, int lastX, int lastY, int newX, int newY) {}
		
		/**
		 * Used for attack popups in advanced mode
		 **/
		default void onEntityAttacked(Entity entity, int x, int y, int roll, int toHit) {}
		
		default void onEntityRemoved(Entity entity) {}
		
		default void onQuit() {}
		
		default void onSaveAndQuit() {}
	}
}
