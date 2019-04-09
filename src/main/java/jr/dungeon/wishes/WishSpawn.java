package jr.dungeon.wishes;

import jr.dungeon.Dungeon;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.QuickSpawn;
import jr.dungeon.entities.player.Player;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class WishSpawn<T extends Entity> implements Wish {
    private Class<T> entityClass;

    @Override
    public void grant(Dungeon dungeon, Player player, String... args) {
        QuickSpawn.spawnClass(entityClass, dungeon.getLevel(), player.getPosition());
        dungeon.turnSystem.turn();
    }
}
