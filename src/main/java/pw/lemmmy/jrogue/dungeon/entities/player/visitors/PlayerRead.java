package pw.lemmmy.jrogue.dungeon.entities.player.visitors;

import pw.lemmmy.jrogue.dungeon.entities.player.Player;
import pw.lemmmy.jrogue.dungeon.items.ItemStack;
import pw.lemmmy.jrogue.dungeon.items.Readable;

public class PlayerRead extends PlayerItemVisitor {
	@Override
	public void visit(Player player) {
		String msg = "Read what?";
		
		InventoryUseResult result = useInventoryItem(
			player,
			msg,
			s -> s.getItem() instanceof Readable, (c, ce, inv) -> {
				ItemStack stack = ce.getStack();
				Readable readable = (Readable) stack.getItem();
				
				readable.onRead(player);
			}
		);
		
		switch (result) {
			case NO_CONTAINER:
			case NO_ITEM:
				player.getDungeon().yellowYou("have nothing to read.");
				break;
			default:
				break;
		}
	}
}
