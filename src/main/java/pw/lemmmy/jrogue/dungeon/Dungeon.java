package pw.lemmmy.jrogue.dungeon;

import pw.lemmmy.jrogue.JRogue;
import pw.lemmmy.jrogue.Settings;
import pw.lemmmy.jrogue.dungeon.entities.*;
import pw.lemmmy.jrogue.dungeon.entities.roles.RoleWizard;
import pw.lemmmy.jrogue.dungeon.generators.DungeonNameGenerator;
import pw.lemmmy.jrogue.dungeon.generators.StandardDungeonGenerator;
import pw.lemmmy.jrogue.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class Dungeon {
	public static final int NORMAL_SPEED = 12;

	private static final int LEVEL_WIDTH = 80;
	private static final int LEVEL_HEIGHT = 30;

	private final List<Listener> listeners = new ArrayList<>();
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

	private Prompt prompt;

	private Settings settings;

	public Dungeon(Settings settings) {
		this.originalName = DungeonNameGenerator.generate();
		this.name = this.originalName;
		this.settings = settings;

		generateLevel();
	}

	public void addListener(Listener listener) {
		listeners.add(listener);
	}

	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}

	public void generateLevel() {
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
			player = new Player(this, level, level.getSpawnX(), level.getSpawnY(), settings.getPlayerName(), new RoleWizard());
		} else {
			player.setPosition(level.getSpawnX(), level.getSpawnY());
		}

		player.setLevel(level);
		level.addEntity(player);

		listeners.forEach(l -> l.onLevelChange(level));
	}

	public String getOriginalName() {
		return originalName;
	}

	public String getName() {
		return name;
	}

	public Level getLevel() {
		return level;
	}

	public void rerollName() {
		this.originalName = DungeonNameGenerator.generate();
		this.name = this.originalName;
	}

	public Player getPlayer() {
		return player;
	}

	public void log(String s, Object... objects) {
		JRogue.getLogger().info(String.format(s, objects));

		listeners.forEach(l -> l.onLog(String.format(s, objects)));
	}

	public void You(String s, Object... objects) {
		log("You " + s, objects);
	}

	public void Your(String s, Object... objects) {
		log("Your " + s, objects);
	}

	public void The(String s, Object... objects) {
		log("The " + s, objects);
	}

	public void logRandom(String... strings) {
		log(Utils.randomFrom(strings));
	}

	public void start() {
		You("descend the stairs into [CYAN]%s[].", this.name);
		turn();
	}

	private boolean moveEntities() {
		AtomicBoolean somebodyCanMove = new AtomicBoolean(false);

		level.getEntities().stream()
			.filter(e -> e instanceof EntityTurnBased && !(e instanceof Player) &&
						(e instanceof LivingEntity && ((LivingEntity) e).isAlive()) &&
						!(((EntityTurnBased)e).getMovementPoints() < NORMAL_SPEED))
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

						turnBasedEntity.setMovementPoints(turnBasedEntity.getMovementSpeed());
					}
				}

				if (player.getMovementPoints() < 0) {
					player.setMovementPoints(0);
				}

				turn++;

				update();

				break;
			}
		} while (player.isAlive() && player.getMovementPoints() < NORMAL_SPEED);

		if (player.isAlive()) {
			player.move();
		} else {
			return;
		}

		level.processEntityQueues();

		getLevel().buildLight();
		getLevel().updateSight(getPlayer());

		listeners.forEach(l -> l.onTurn(turn));
	}

	private void update() {
		// random dungeon updates
	}

	public long getTurn() {
		return turn;
	}

	public void setTurn(long turn) {
		this.turn = turn;
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

	public void wish(String wish) {
		if (player.isDebugger()) {
			JRogue.getLogger().debug("Player wished for '{}'", wish);
		}

		wish = wish.toLowerCase();

		if (wish.equalsIgnoreCase("death")) {
			player.kill(DamageSource.WISH_FOR_DEATH);
		}
	}

	public interface Listener {
		void onLevelChange(Level level);

		void onBeforeTurn(long turn);

		void onTurn(long turn);

		void onLog(String log);
		void onPrompt(Prompt prompt);
	}
}
