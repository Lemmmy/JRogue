package jr.dungeon.wishes;

import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.player.Player;
import jr.dungeon.generators.DungeonGenerator;

public class WishLevel<T extends DungeonGenerator> implements Wish {
	private Class<T> generatorClass;
	
	public WishLevel(Class<T> generatorClass) {
		this.generatorClass = generatorClass;
	}
	
	@Override
	public void grant(Dungeon dungeon, Player player, String... args) {
		Level newLevel = dungeon.newLevel(
			player.getDepth() - 1,
			dungeon.getLevel().tileStore.getTile(player.getPosition()),
			generatorClass
		);
		
		dungeon.changeLevel(newLevel, newLevel.getSpawnPoint());
	}
}
