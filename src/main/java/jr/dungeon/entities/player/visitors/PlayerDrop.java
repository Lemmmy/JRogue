package jr.dungeon.entities.player.visitors;

import jr.dungeon.entities.player.Player;
import jr.dungeon.items.Item;
import jr.dungeon.items.ItemStack;

public class PlayerDrop extends PlayerItemVisitor {
	@Override
	public void visit(Player player) {
		String msg = "Drop what?";
		
		InventoryUseResult result = useInventoryItem(player, msg, is -> true, (c, ce, inv) -> {
			ItemStack stack = ce.getStack();
			Item item = stack.getItem();
			
			inv.remove(c);
			player.dropItem(stack);
			
			if (item.isis() || stack.getCount() > 1) {
				player.getDungeon().You("drop [YELLOW]%s[] ([YELLOW]%s[]).", stack.getName(player, false), c);
			} else {
				player.getDungeon().You("drop %s [YELLOW]%s[] ([YELLOW]%s[]).",
					stack.beginsWithVowel(player) ? "an" : "a",
					stack.getName(player, false),
					c
				);
			}
			
			player.getDungeon().getTurnSystem().turn(player.getDungeon());
		});
		
		switch (result) {
			case NO_CONTAINER:
				player.getDungeon().yellowYou("can't hold anything!");
				break;
			case NO_ITEM:
				player.getDungeon().yellowYou("don't have any items to drop!");
				break;
			default:
				break;
		}
	}
}
