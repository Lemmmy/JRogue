package jr.dungeon.entities.events;

import jr.dungeon.entities.Entity;
import jr.dungeon.entities.containers.EntityItem;
import jr.dungeon.events.DungeonEvent;
import jr.dungeon.items.Item;
import jr.dungeon.items.ItemStack;

public class EntityItemDroppedOnEvent extends DungeonEvent {
	private Entity droppedOn;
	private EntityItem itemEntity;
	
	public EntityItemDroppedOnEvent(Entity droppedOn, EntityItem itemEntity) {
		this.droppedOn = droppedOn;
		this.itemEntity = itemEntity;
	}
	
	public Entity getDroppedOn() {
		return droppedOn;
	}
	
	public EntityItem getItemEntity() {
		return itemEntity;
	}
	
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
