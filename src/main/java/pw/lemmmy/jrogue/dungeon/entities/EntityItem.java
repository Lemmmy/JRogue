package pw.lemmmy.jrogue.dungeon.entities;

import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.tiles.TileType;
import pw.lemmmy.jrogue.dungeon.items.Item;
import pw.lemmmy.jrogue.dungeon.items.ItemStack;

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
		if (itemStack.getCount() > 1) {
			getDungeon().log("There are %s here.", getName(false));
		} else {
			if (beginsWithVowel()) {
				getDungeon().log("There is an %s here.", getName(false));
			} else {
				getDungeon().log("There is a %s here.", getName(false));
			}
		}
	}

	private boolean beginsWithVowel() {
		return itemStack.beginsWithVowel();
	}

	@Override
	public boolean canBeWalkedOn() {
		return true;
	}
}
