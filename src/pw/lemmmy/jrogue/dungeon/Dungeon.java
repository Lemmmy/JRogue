package pw.lemmmy.jrogue.dungeon;

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

	public Dungeon() {
		this.originalName = DungeonNameGenerator.generate();
		this.name = this.originalName;
	}

	public String getOriginalName() {
		return originalName;
	}

	public String getName() {
		return name;
	}
}
