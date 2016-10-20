package pw.lemmmy.jrogue.dungeon;

public class Dungeon {
	public static interface Listener {
		public void onTurn();
		public void onLog(String log);
	}

	private String name;

	public Dungeon() {
		this.name = DungeonNameGenerator.generate();
	}

	public String getName() {
		return name;
	}
}
