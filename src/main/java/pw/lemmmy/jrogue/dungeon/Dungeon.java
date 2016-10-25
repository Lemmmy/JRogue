package pw.lemmmy.jrogue.dungeon;

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

	public void You(String s) {
		for (Listener listener : listeners) {
			listener.onLog("You " + s);
		}
	}

	public void Your(String s) {
		for (Listener listener : listeners) {
			listener.onLog("Your " + s);
		}
	}

	public static interface Listener {
		public void onLevelChange(Level level);
		public void onTurn();
		public void onLog(String log);
	}
}
