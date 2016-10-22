package pw.lemmmy.jrogue.dungeon;

import pw.lemmmy.jrogue.dungeon.generators.DungeonNameGenerator;
import pw.lemmmy.jrogue.dungeon.generators.StandardDungeonGenerator;

import java.util.ArrayList;
import java.util.List;

public class Dungeon {
	private static final int LEVEL_WIDTH = 60;
	private static final int LEVEL_HEIGHT = 20;

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

	public Dungeon() {
		this.originalName = DungeonNameGenerator.generate();
		this.name = this.originalName;

		this.level = new Level(LEVEL_WIDTH, LEVEL_HEIGHT, -1);
		new StandardDungeonGenerator(this.level).generate();
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

	public Level getLevel() {
		return level;
	}
}
