package jr.dungeon.entities.containers;

import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.EntityAppearance;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.effects.MercuryPoisoning;
import jr.dungeon.entities.events.EntityAddedEvent;
import jr.dungeon.entities.events.ItemDroppedEvent;
import jr.dungeon.entities.events.ItemDroppedOnEntityEvent;
import jr.dungeon.entities.events.EntityKickedEntityEvent;
import jr.dungeon.events.DungeonEventHandler;
import jr.dungeon.items.Item;
import jr.dungeon.items.ItemStack;
import jr.dungeon.items.Shatterable;
import jr.dungeon.items.valuables.ItemThermometer;
import jr.dungeon.tiles.TileType;
import lombok.Getter;
import org.json.JSONObject;

import java.util.Optional;

public class EntityItem extends Entity {
	@Getter private ItemStack itemStack;

	private final JSONObject persistence = new JSONObject();
	
	public EntityItem(Dungeon dungeon, Level level, int x, int y) { // unserialisation constructor
		super(dungeon, level, x, y);
	}
	
	public EntityItem(Dungeon dungeon, Level level, int x, int y, ItemStack itemStack) {
		super(dungeon, level, x, y);
		
		this.itemStack = itemStack;
	}
	
	public Item getItem() {
		return itemStack.getItem();
	}
	
	@Override
	public int getDepth() {
		return 2;
	}
	
	@Override
	public boolean isStatic() {
		return true;
	}
	
	@Override
	public EntityAppearance getAppearance() {
		return EntityAppearance.APPEARANCE_ITEM;
	}
	
	@Override
	public void update() {
		super.update();
		
		itemStack.getItem().update(this);
	}
	
	@DungeonEventHandler(selfOnly = true)
	public void onKick(EntityKickedEntityEvent e) {
		int x = getX() + e.getDeltaX();
		int y = getY() + e.getDeltaY();
		
		if (getItem() instanceof Shatterable) {
			getDungeon().The("%s shatters into a thousand pieces!", getName(getDungeon().getPlayer(), false));
			
			if (getItem() instanceof ItemThermometer) {
				e.getKicker().addStatusEffect(new MercuryPoisoning());
			}
			
			getLevel().getEntityStore().removeEntity(this);
			return;
		}
		
		TileType tile = getLevel().getTileStore().getTileType(x, y);
		
		if (tile == null || tile.getSolidity() == TileType.Solidity.SOLID) {
			getDungeon().The("%s strikes the side of the wall.", getName(getDungeon().getPlayer(), false));
			
			return;
		}
		
		setPosition(x, y);
	}
	
	@Override
	public String getName(EntityLiving observer, boolean requiresCapitalisation) {
		return itemStack.getName(observer, requiresCapitalisation);
	}
	
	@DungeonEventHandler(selfOnly = true)
	public void onSpawn(EntityAddedEvent event) {
		if (event.isNew()) {
			getDungeon().triggerEvent(new ItemDroppedEvent(this));
			
			getLevel().getEntityStore().getEntitiesAt(getX(), getY()).stream()
				.filter(e -> !e.equals(this))
				.forEach(e -> getDungeon().triggerEvent(new ItemDroppedOnEntityEvent(e, this)));
		}
	}
	
	@Override
	public boolean canBeWalkedOn() {
		return true;
	}
	
	@Override
	public void serialise(JSONObject obj) {
		super.serialise(obj);
		
		JSONObject serialisedItem = new JSONObject();
		itemStack.serialise(serialisedItem);
		obj.put("itemStack", serialisedItem);
	}
	
	@Override
	public void unserialise(JSONObject obj) {
		super.unserialise(obj);
		
		Optional<ItemStack> itemStackOptional = ItemStack.createFromJSON(obj.getJSONObject("itemStack"));
		itemStackOptional.ifPresent(i -> itemStack = i);
	}

	@Override
	public JSONObject getPersistence() {
		return persistence;
	}
}
