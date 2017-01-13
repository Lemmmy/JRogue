package pw.lemmmy.jrogue.dungeon.entities.player.visitors;

import pw.lemmmy.jrogue.dungeon.entities.actions.ActionQuaff;
import pw.lemmmy.jrogue.dungeon.entities.actions.EntityAction;
import pw.lemmmy.jrogue.dungeon.entities.containers.Container;
import pw.lemmmy.jrogue.dungeon.entities.player.Player;
import pw.lemmmy.jrogue.dungeon.items.ItemStack;
import pw.lemmmy.jrogue.dungeon.items.quaffable.ItemQuaffable;
import pw.lemmmy.jrogue.dungeon.items.quaffable.potions.ItemPotion;

public class PlayerQuaff extends PlayerItemVisitor {
	@Override
	public void visit(Player player) {
		String msg = "Quaff what?";
		
		InventoryUseResult result = useInventoryItem(
			player,
			msg,
			s -> s.getItem() instanceof ItemQuaffable && ((ItemQuaffable) s.getItem()).canQuaff(), (c, ce, inv) -> {
				ItemStack stack = ce.getStack();
				ItemQuaffable quaffable = (ItemQuaffable) stack.getItem();
				
				player.setAction(new ActionQuaff(
					quaffable,
					(EntityAction.CompleteCallback) entity -> quaffCallback(ce, inv, stack, quaffable))
				);
			}
		);
		
		switch (result) {
			case NO_CONTAINER:
			case NO_ITEM:
				player.getDungeon().yellowYou("have nothing to quaff.");
				break;
			default:
				break;
		}
	}
	
	private void quaffCallback(Container.ContainerEntry ce, Container inv, ItemStack stack, ItemQuaffable quaffable) {
		if (stack.getCount() == 1) {
			inv.remove(ce.getLetter());
		} else {
			stack.subtractCount(1);
		}
		
		if (quaffable instanceof ItemPotion) {
			ItemPotion potion = (ItemPotion) quaffable;
			
			ItemPotion emptyPotion = new ItemPotion();
			emptyPotion.setPotionType(potion.getPotionType());
			emptyPotion.setBottleType(potion.getBottleType());
			emptyPotion.setPotionColour(potion.getPotionColour());
			emptyPotion.setEmpty(true);
			inv.add(new ItemStack(emptyPotion, 1));
		}
	}
}
