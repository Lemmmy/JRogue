package pw.lemmmy.jrogue.dungeon;

import com.github.alexeyr.pcg.Pcg32;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import pw.lemmmy.jrogue.JRogue;
import pw.lemmmy.jrogue.Settings;
import pw.lemmmy.jrogue.dungeon.entities.*;
import pw.lemmmy.jrogue.dungeon.entities.roles.RoleWizard;
import pw.lemmmy.jrogue.dungeon.generators.DungeonNameGenerator;
import pw.lemmmy.jrogue.dungeon.generators.StandardDungeonGenerator;
import pw.lemmmy.jrogue.utils.OperatingSystem;
import pw.lemmmy.jrogue.utils.Utils;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class Dungeon {
	public static final int NORMAL_SPEED = 12;

	private static final int LEVEL_WIDTH = 80;
	private static final int LEVEL_HEIGHT = 30;

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

	private Level level;
	private Player player;

	private long turn = 0;
	private long nextExerciseCounter = 500;
	private long passiveSoundCounter = 0;

	private Prompt prompt;
	private Settings settings;

	private static Path dataDir = OperatingSystem.get().getAppDataDir().resolve("jrogue");

	public Dungeon(Settings settings) {
		this.settings = settings;
	}

	public void generateLevel() {
		this.originalName = DungeonNameGenerator.generate();
		this.name = this.originalName;

		if (level != null) {
			level.removeEntity(player);
		}

		boolean gotLevel = false;

		do {
			level = new Level(this, LEVEL_WIDTH, LEVEL_HEIGHT, -1);

			if (!(new StandardDungeonGenerator(level).generate())) {
				continue;
			}

			level.buildLight();

			gotLevel = true;
		} while (!gotLevel);

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
			JSONObject serialisedDungeon = serialise();
			writer.append(serialisedDungeon.toString());
		} catch (IOException e) {
			JRogue.getLogger().error("Error saving dungeon:");
			JRogue.getLogger().error(e);
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
			} catch (IOException e) {
				JRogue.getLogger().error("Error loading dungeon:");
				JRogue.getLogger().error(e);
			}
		}

		dungeon.generateLevel();
		return dungeon;
	}

	private JSONObject serialise() {
		JSONObject obj = new JSONObject();

		obj.put("name", getName());
		obj.put("originalName", getOriginalName());
		obj.put("turn", getTurn());
		obj.put("nextExerciseCounter", nextExerciseCounter);
		obj.put("passiveSoundCounter", passiveSoundCounter);

		obj.append("levels", getLevel().serialise()); // TODO: multi level support

		return obj;
	}

	private void unserialise(JSONObject obj) {
		try {
			name = obj.getString("name");
			originalName = obj.getString("originalName");
			turn = obj.getInt("turn");
			nextExerciseCounter = obj.getInt("nextExerciseCounter");
			passiveSoundCounter = obj.getInt("passiveSoundCounter");

			JSONArray levels = obj.getJSONArray("levels");
			levels.forEach(serialisedLevel ->
				   Level.createFromJSON((JSONObject) serialisedLevel, this).ifPresent(l -> level = l));

			listeners.forEach(l -> l.onLevelChange(level));

			level.buildLight();
			level.updateSight(player);
		} catch (JSONException e) {
			JRogue.getLogger().error("Error loading dungeon:");
			JRogue.getLogger().error(e);
		}
	}

	public void quit() {
		prompt(new Prompt("Really quit?", new char[] {'y', 'n'}, true, new Prompt.PromptCallback() {
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

	public void Your(String s, Object... objects) {
		log("Your " + s, objects);
	}

	public void log(String s, Object... objects) {
		JRogue.getLogger().info(String.format(s, objects));

		listeners.forEach(l -> l.onLog(String.format(s, objects)));
	}

	public void The(String s, Object... objects) {
		log("The " + s, objects);
	}

	public void logRandom(String... strings) {
		log(Utils.randomFrom(strings));
	}

	public void start() {
		if (turn <= 0) {
			You("descend the stairs into [CYAN]%s[].", this.name);
			turn();
		} else {
			listeners.forEach(l -> l.onBeforeTurn(turn));
			log("Welcome back to [CYAN]%s[].", this.name);
			level.processEntityQueues();
			listeners.forEach(l -> l.onTurn(turn));
		}
	}

	public void You(String s, Object... objects) {
		log("You " + s, objects);
	}

	public void prompt(Prompt prompt) {
		this.prompt = prompt;
		listeners.forEach(l -> l.onPrompt(prompt));
	}

	public void promptRespond(char response) {
		if (prompt != null) {
			Prompt prompt = this.prompt;
			this.prompt = null;
			prompt.respond(response);

			listeners.forEach(l -> l.onPrompt(null));
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

	public void showPath(pw.lemmmy.jrogue.dungeon.entities.Path path) {
		listeners.forEach(l -> l.onPathShow(path));
	}

	public void turn() {
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

					if (entity instanceof LivingEntity && !((LivingEntity) entity).isAlive()) {
						continue;
					}

					entity.update();

					if (entity instanceof EntityTurnBased) {
						EntityTurnBased turnBasedEntity = (EntityTurnBased) entity;

						turnBasedEntity.calculateMovement();
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

		level.buildLight();
		level.updateSight(player);

		if (settings.autosave()) {
			save();
		}

		listeners.forEach(l -> l.onTurn(turn));
	}

	private boolean moveEntities() {
		AtomicBoolean somebodyCanMove = new AtomicBoolean(false);

		level.getEntities().stream()
			 .filter(e -> e instanceof EntityTurnBased && !(e instanceof Player) &&
				 (e instanceof LivingEntity && ((LivingEntity) e).isAlive()) &&
				 !(((EntityTurnBased) e).getMovementPoints() < NORMAL_SPEED))
			 .forEach(e -> {
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

			passiveSoundCounter = Utils.roll(3, 4);
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
			String sound = Utils.randomFrom(soundEmitter.getSounds());

			log(sound);
		}
	}

	public void wish(String wish) {
		Wish.wish(this, wish);
	}

	public Level getLevel() {
		return level;
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

	public void entityRemoved(Entity entity) {
		listeners.forEach(l -> l.onEntityRemoved(entity));
	}

	public interface Listener {
		void onLevelChange(Level level);

		void onBeforeTurn(long turn);

		void onTurn(long turn);

		void onLog(String log);

		void onPrompt(Prompt prompt);

		void onContainerShow(Entity containerEntity);

		void onPathShow(pw.lemmmy.jrogue.dungeon.entities.Path path);

		void onEntityAdded(Entity entity);

		void onEntityMoved(Entity entity, int lastX, int lastY, int newX, int newY);

		void onEntityRemoved(Entity entity);

		void onQuit();
	}
}
