package pw.lemmmy.jrogue.dungeon.generators;

import pw.lemmmy.jrogue.dungeon.Level;

public class StandardDungeonGenerator extends DungeonGenerator {
	public StandardDungeonGenerator(Level level) {
		super(level);
	}

	@Override
	public void generate() {
		buildRoom(1, 1, 10, 6);
	}
}
