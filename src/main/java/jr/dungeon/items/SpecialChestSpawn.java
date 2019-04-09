package jr.dungeon.items;

import jr.dungeon.entities.containers.Container;
import jr.dungeon.entities.containers.EntityChest;

public interface SpecialChestSpawn {
    void onSpawnInChest(EntityChest chest, Container container);
}
