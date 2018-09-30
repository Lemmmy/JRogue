package jr.dungeon.wishes;

import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.QuickSpawn;
import jr.dungeon.entities.player.Player;

public class WishSpawn<T extends Entity> implements Wish {
	private Class<T> entityClass;

	public WishSpawn(Class<T> entityClass) {
		this.entityClass = entityClass;
	}

	@Override
	public void grant(Dungeon dungeon, Player player, String... args) {
		QuickSpawn.spawnClass(entityClass, dungeon.getLevel(), player.getX(), player.getY());
		dungeon.turnSystem.turn(dungeon);
	}
}
