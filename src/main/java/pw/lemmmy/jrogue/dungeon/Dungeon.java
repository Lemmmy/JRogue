package pw.lemmmy.jrogue.dungeon;

import pw.lemmmy.jrogue.dungeon.entities.Entity;
import pw.lemmmy.jrogue.dungeon.entities.Player;
import pw.lemmmy.jrogue.dungeon.generators.DungeonNameGenerator;
import pw.lemmmy.jrogue.dungeon.generators.StandardDungeonGenerator;

import java.util.ArrayList;
import java.util.List;

public class Dungeon {
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

	private Prompt prompt;

	public Dungeon() {
		this.originalName = DungeonNameGenerator.generate();
		this.name = this.originalName;

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
			level = new Level(LEVEL_WIDTH, LEVEL_HEIGHT, -1);

			if (!(new StandardDungeonGenerator(level).generate())) {
				continue;
			}

			level.buildLight();

			gotLevel = true;
		} while (!gotLevel);

		if (player == null) {
			player = new Player(this, level, level.getSpawnX(), level.getSpawnY(), System.getProperty("user.name"));
		} else {
			player.setPosition(level.getSpawnX(), level.getSpawnY());
		}

		player.setLevel(level);
		level.addEntity(player);

		for (Listener listener : listeners) {
			listener.onLevelChange(level);
		}
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
		for (Listener listener : listeners) {
			listener.onLog(String.format(s, objects));
		}
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

	public void start() {
		You("descend the stairs into [CYAN]%s[].", this.name);
		turn();
	}

	public void turn() {
		for (Listener listener : listeners) {
			listener.onBeforeTurn(turn + 1);
		}

		for (Entity entity : level.getEntities()) {
			entity.move();
			entity.update();
		}

		turn++;

		for (Listener listener : listeners) {
			listener.onTurn(turn);
		}
	}

	public long getTurn() {
		return turn;
	}

	public void prompt(Prompt prompt) {
		this.prompt = prompt;

		for (Listener listener : listeners) {
			listener.onPrompt(prompt);
		}
	}

	public void promptRespond(char response) {
		if (prompt != null) {
			Prompt prompt = this.prompt;
			this.prompt = null;
			prompt.respond(response);

			for (Listener listener : listeners) {
				listener.onPrompt(null);
			}
		}
	}

	public void escapePrompt() {
		if (prompt != null) {
			Prompt prompt = this.prompt;
			this.prompt = null;
			prompt.escape();

			for (Listener listener : listeners) {
				listener.onPrompt(null);
			}
		}
	}

	public boolean hasPrompt() {
		return prompt != null;
	}

	public boolean isPromptEscapable() {
		return prompt != null && prompt.isEscapable();
	}

	public interface Listener {
		void onLevelChange(Level level);

		void onBeforeTurn(long turn);

		void onTurn(long turn);

		void onLog(String log);
		void onPrompt(Prompt prompt);
	}
}
