package jr.dungeon.entities.events;

import jr.dungeon.entities.containers.EntityItem;
import jr.dungeon.events.DungeonEvent;
import jr.dungeon.items.Item;
import jr.dungeon.items.ItemStack;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ItemDroppedEvent extends DungeonEvent {
	private EntityItem itemEntity;
	
	public ItemStack getItemStack() {
		return itemEntity.getItemStack();
	}
	
	public Item getItem() {
		return itemEntity.getItem();
	}
}
