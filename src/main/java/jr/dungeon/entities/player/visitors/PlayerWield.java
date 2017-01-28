package jr.dungeon.entities.player.visitors;

import jr.dungeon.entities.containers.Container;
import jr.dungeon.entities.player.Player;
import jr.dungeon.items.Item;
import jr.dungeon.items.ItemStack;
import jr.dungeon.items.Wieldable;

public class PlayerWield extends PlayerItemVisitor {
	@Override
	public void visit(Player player) {
		String msg = "Wield what?";
		
		InventoryUseResult result = useInventoryItem(player, msg, s -> s.getItem() instanceof Wieldable, (c, ce, inv) -> {
			if (c == '-') {
				player.setLeftHand(null);
				player.setRightHand(null);
				player.getDungeon().You("unwield everything.");
				player.getDungeon().turn();
				return;
			}
			
			if (ce == null) {
				player.getDungeon().log(String.format("Invalid item '[YELLOW]%s[]'.", c));
				return;
			}
			
			wield(player, c, ce);
		}, true);
		
		switch (result) {
			case NO_CONTAINER:
			case NO_ITEM:
				player.getDungeon().yellowYou("have nothing to wield!");
				break;
			default:
				break;
		}
	}
	
	private void wield(Player player, Character c, Container.ContainerEntry ce) {
		ItemStack stack = ce.getStack();
		Item item = stack.getItem();
		
		if (player.getRightHand() != null && ((Wieldable) player.getRightHand().getItem()).isTwoHanded()) {
			player.setLeftHand(null);
		}
		
		player.setRightHand(ce);
		
		if (((Wieldable) item).isTwoHanded()) {
			player.setLeftHand(ce);
		}
		
		String name = stack.getName(player, false);
		
		if (item.isis() || stack.getCount() > 1) {
			player.getDungeon().You("wield [YELLOW]%s[] ([YELLOW]%s[]).", name, c);
		} else {
			player.getDungeon().You(
				"wield %s [YELLOW]%s[] ([YELLOW]%s[]).",
				stack.beginsWithVowel(player) ? "an" : "a",
				name,
				c
			);
		}
		
		player.getDungeon().turn();
	}
}
