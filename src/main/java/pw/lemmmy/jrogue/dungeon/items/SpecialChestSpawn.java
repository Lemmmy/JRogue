package pw.lemmmy.jrogue.dungeon.items;

import pw.lemmmy.jrogue.dungeon.entities.containers.Container;
import pw.lemmmy.jrogue.dungeon.entities.containers.EntityChest;

public interface SpecialChestSpawn {
	void onSpawnInChest(EntityChest chest, Container container);
}
