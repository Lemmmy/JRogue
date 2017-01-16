package pw.lemmmy.jrogue.dungeon.entities.player.visitors;

import pw.lemmmy.jrogue.dungeon.entities.player.Player;
import pw.lemmmy.jrogue.dungeon.items.Item;
import pw.lemmmy.jrogue.dungeon.items.ItemStack;

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
			
			player.getDungeon().turn();
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
