package pw.lemmmy.jrogue.dungeon.entities;

import org.json.JSONObject;
import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.items.Item;
import pw.lemmmy.jrogue.dungeon.items.ItemStack;
import pw.lemmmy.jrogue.dungeon.tiles.TileType;

import java.util.Optional;

public class EntityItem extends Entity {
	private ItemStack itemStack;

	public EntityItem(Dungeon dungeon, Level level, int x, int y) { // unserialisation constructor
		super(dungeon, level, x, y);
	}

	public EntityItem(Dungeon dungeon, Level level, ItemStack itemStack, int x, int y) {
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
		return 0;
	}

	@Override
	public EntityAppearance getAppearance() {
		return EntityAppearance.APPEARANCE_ITEM;
	}

	@Override
	protected void onKick(LivingEntity kicker, boolean isPlayer, int x, int y) {
		TileType tile = getLevel().getTileType(x, y);

		if (tile == null || tile.getSolidity() == TileType.Solidity.SOLID) {
			if (isPlayer) {
				getDungeon().You("cannot kick the %s any further that way.", getName(false));
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
	protected void onWalk(LivingEntity walker, boolean isPlayer) {
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
}
