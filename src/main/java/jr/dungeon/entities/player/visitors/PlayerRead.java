package jr.dungeon.entities.player.visitors;

import jr.dungeon.entities.player.Player;
import jr.dungeon.items.ItemStack;
import jr.dungeon.items.Readable;

import java.util.Optional;

public class PlayerRead extends PlayerItemVisitor {
	@Override
	public void visit(Player player) {
		Optional<jr.dungeon.entities.interfaces.Readable> or =
			player.getLevel().getEntityStore().getEntitiesAt(player.getX(), player.getY()).stream()
			.filter(jr.dungeon.entities.interfaces.Readable.class::isInstance)
			.map(e -> (jr.dungeon.entities.interfaces.Readable) e)
			.filter(r -> r.canRead(player))
			.findFirst();
		
		if (or.isPresent()) {
			or.get().read(player);
		} else {
			readItem(player);
		}
	}
	
	private void readItem(Player player) {
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
