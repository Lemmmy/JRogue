package jr.dungeon.entities.player.visitors;

import jr.dungeon.entities.player.Player;
import jr.dungeon.items.Item;
import jr.dungeon.items.ItemStack;
import jr.language.LanguageUtils;

public class PlayerDrop extends PlayerItemVisitor {
	@Override
	public void visit(Player player) {
		String msg = "Drop what?";
		
		InventoryUseResult result = useInventoryItem(player, msg, is -> true, (c, ce, inv) -> {
			ItemStack stack = ce.getStack();
			Item item = stack.getItem();
			
			inv.remove(c);
			player.dropItem(stack);
			
			player.getDungeon().You(
				"drop [YELLOW]%s[] ([YELLOW]%s[]).",
				LanguageUtils.anObject(player, stack),
				c
			);
			
			player.getDungeon().turnSystem.turn(player.getDungeon());
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
