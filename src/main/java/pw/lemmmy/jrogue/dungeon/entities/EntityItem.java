package pw.lemmmy.jrogue.dungeon.entities;

import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.items.Item;
import pw.lemmmy.jrogue.dungeon.items.ItemStack;
import pw.lemmmy.jrogue.dungeon.tiles.TileType;

public class EntityItem extends Entity {
	private ItemStack itemStack;

	public EntityItem(Dungeon dungeon, Level level, ItemStack itemStack, int x, int y) {
		super(dungeon, level, x, y);

		this.itemStack = itemStack;
	}

	public ItemStack getItemStack() {
		return itemStack;
	}

	public Item getItem() {
		return itemStack.getItem();
	}	@Override
	public int getDepth() {
		return 0;
	}




	@Override
	public String getName(boolean requiresCapitalisation) {
		return itemStack.getName(requiresCapitalisation);
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
	protected void onWalk(LivingEntity walker, boolean isPlayer) {
	}

	@Override
	public boolean canBeWalkedOn() {
		return true;
	}
}
