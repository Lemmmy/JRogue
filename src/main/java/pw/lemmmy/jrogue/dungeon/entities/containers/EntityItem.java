package pw.lemmmy.jrogue.dungeon.entities.containers;

import org.json.JSONObject;
import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.entities.Entity;
import pw.lemmmy.jrogue.dungeon.entities.EntityAppearance;
import pw.lemmmy.jrogue.dungeon.entities.LivingEntity;
import pw.lemmmy.jrogue.dungeon.items.Item;
import pw.lemmmy.jrogue.dungeon.items.ItemStack;
import pw.lemmmy.jrogue.dungeon.items.Shatterable;
import pw.lemmmy.jrogue.dungeon.tiles.TileType;

import java.util.Optional;

public class EntityItem extends Entity {
	private ItemStack itemStack;
	
	public EntityItem(Dungeon dungeon, Level level, int x, int y) { // unserialisation constructor
		super(dungeon, level, x, y);
	}
	
	public EntityItem(Dungeon dungeon, Level level, int x, int y, ItemStack itemStack) {
		super(dungeon, level, x, y);
		
		this.itemStack = itemStack;
	}
	
	public ItemStack getItemStack() {
		return itemStack;
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
	protected void onKick(LivingEntity kicker, boolean isPlayer, int x, int y) {
		if (getItem() instanceof Shatterable) {
			if (isPlayer) {
				getDungeon().The("%s shatters into a thousand pieces!", getName(false));
			}
			
			getLevel().removeEntity(this);
			return;
		}
		
		x += (kicker.getX() < x ? 1 : -1) + (kicker.getX() == x ? 1 : 0);
		y += (kicker.getY() < y ? 1 : -1) + (kicker.getY() == y ? 1 : 0);
		
		TileType tile = getLevel().getTileType(x, y);
		
		if (tile == null || tile.getSolidity() == TileType.Solidity.SOLID) {
			if (isPlayer) {
				getDungeon().The("%s strikes the side of the wall.", getName(false));
				// "wall" seems appropriate for all current SOLID tiles
			}
			
			return;
		}
		
		setPosition(x, y);
	}
	
	@Override
	public String getName(boolean requiresCapitalisation) {
		return itemStack.getName(requiresCapitalisation);
	}
	
	@Override
	protected void onWalk(LivingEntity walker, boolean isPlayer) {}
	
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
}
