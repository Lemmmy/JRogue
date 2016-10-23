package pw.lemmmy.jrogue.dungeon;

import pw.lemmmy.jrogue.JRogue;
import pw.lemmmy.jrogue.dungeon.generators.DungeonGenerator;
import pw.lemmmy.jrogue.dungeon.generators.DungeonNameGenerator;
import pw.lemmmy.jrogue.dungeon.generators.StandardDungeonGenerator;

import java.util.ArrayList;
import java.util.List;

public class Dungeon {
	private static final int LEVEL_WIDTH = 80;
	private static final int LEVEL_HEIGHT = 30;

	public static interface Listener {
		public void onTurn();
		public void onLog(String log);
	}

	/**
	 * Randomly generated name of this dungeon
	 */
	private String originalName;

	/**
	 * User-chosen name of this dungeon
	 */
	private String name;

	private Level level;

	private final List<Listener> listeners = new ArrayList<>();

	public List<DungeonGenerator.Room> rooms = new ArrayList<>();

	public Dungeon() {
		JRogue.getLogger().debug("Creating new dungeon");

		this.originalName = DungeonNameGenerator.generate();
		this.name = this.originalName;

		JRogue.getLogger().debug("Dungeon is called {}", originalName);

		this.level = new Level(LEVEL_WIDTH, LEVEL_HEIGHT, -1);
		rooms = new StandardDungeonGenerator(this.level).generate();

		JRogue.getLogger().debug("Generated first level, got {} rooms", rooms.size());
	}

	public void addListener(Listener listener) {
		listeners.add(listener);
	}

	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}

	public void regenerateLevel() {
		level = new Level(LEVEL_WIDTH, LEVEL_HEIGHT, -1);
		rooms = new StandardDungeonGenerator(level).generate();
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
}
