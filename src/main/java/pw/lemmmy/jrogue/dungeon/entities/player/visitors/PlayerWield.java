package pw.lemmmy.jrogue.dungeon.entities.player.visitors;

import pw.lemmmy.jrogue.dungeon.entities.player.Player;
import pw.lemmmy.jrogue.dungeon.items.Item;
import pw.lemmmy.jrogue.dungeon.items.ItemStack;
import pw.lemmmy.jrogue.dungeon.items.Wieldable;

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
			
			ItemStack stack = ce.getStack();
			Item item = stack.getItem();
			
			if (player.getRightHand() != null && ((Wieldable) player.getRightHand().getItem()).isTwoHanded()) {
				player.setLeftHand(null);
			}
			
			player.setRightHand(ce);
			
			if (((Wieldable) item).isTwoHanded()) {
				player.setLeftHand(ce);
			}
			
			String name = stack.getName(false);
			
			if (item.isis() || stack.getCount() > 1) {
				player.getDungeon().You("wield [YELLOW]%s[] ([YELLOW]%s[]).", name, c);
			} else {
				player.getDungeon().You("wield %s [YELLOW]%s[] ([YELLOW]%s[]).", stack.beginsWithVowel() ? "an" : "a", name, c);
			}
			
			player.getDungeon().turn();
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
}
