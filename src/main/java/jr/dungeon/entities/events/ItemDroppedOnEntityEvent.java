package jr.dungeon.entities.events;

import jr.dungeon.entities.Entity;
import jr.dungeon.entities.containers.EntityItem;
import jr.dungeon.events.DungeonEvent;
import jr.dungeon.items.Item;
import jr.dungeon.items.ItemStack;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ItemDroppedOnEntityEvent extends DungeonEvent {
	private Entity droppedOn;
	private EntityItem itemEntity;
	
	public ItemStack getItemStack() {
		return itemEntity.getItemStack();
	}
	
	public Item getItem() {
		return itemEntity.getItem();
	}
	
	@Override
	public boolean isSelf(Object other) {
		return other.equals(droppedOn);
	}
}
