package pw.lemmmy.jrogue.dungeon;

import pw.lemmmy.jrogue.dungeon.generators.DungeonNameGenerator;

import java.util.ArrayList;
import java.util.List;

public class Dungeon {
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

	private final List<Listener> listeners = new ArrayList<>();

	public Dungeon() {
		this.originalName = DungeonNameGenerator.generate();
		this.name = this.originalName;
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
}
