package pw.lemmmy.jrogue.dungeon.entities.player.visitors;

import pw.lemmmy.jrogue.dungeon.Prompt;
import pw.lemmmy.jrogue.dungeon.entities.Entity;
import pw.lemmmy.jrogue.dungeon.entities.Quaffable;
import pw.lemmmy.jrogue.dungeon.entities.actions.ActionQuaff;
import pw.lemmmy.jrogue.dungeon.entities.actions.EntityAction;
import pw.lemmmy.jrogue.dungeon.entities.containers.Container;
import pw.lemmmy.jrogue.dungeon.entities.player.Player;
import pw.lemmmy.jrogue.dungeon.items.ItemStack;
import pw.lemmmy.jrogue.dungeon.items.quaffable.ItemQuaffable;
import pw.lemmmy.jrogue.dungeon.items.quaffable.potions.ItemPotion;

import java.util.concurrent.atomic.AtomicBoolean;

public class PlayerQuaff extends PlayerItemVisitor {
	@Override
	public void visit(Player player) {
		AtomicBoolean cancelled = new AtomicBoolean(false);
		
		player.getLevel().getEntitiesAt(player.getX(), player.getY()).stream()
			.filter(Quaffable.class::isInstance)
			.filter(e -> ((Quaffable) e).canQuaff(player))
			.findFirst()
			.ifPresent(q -> {
				String msg = ((Quaffable) q).getQuaffConfirmationMessage(player);
				
				player.getDungeon().prompt(new Prompt(msg, new char[] {'y', 'n'}, true, new Prompt.SimplePromptCallback(player.getDungeon()) {
					@Override
					public void onResponse(char response) {
						if (response != 'y') {
							quaffItem(player);
							return;
						}
						
						quaffEntity(player, q);
					}
				}));
				
				cancelled.set(true);
			});
		
		if (cancelled.get()) {
			return;
		}
		
		quaffItem(player);
	}
	
	private void quaffEntity(Player player, Entity entity) {
		Quaffable quaffable = (Quaffable) entity;
		quaffable.quaff(player);
	}
	
	private void quaffItem(Player player) {
		String msg = "Quaff what?";
		
		InventoryUseResult result = useInventoryItem(
			player,
			msg,
			s -> s.getItem() instanceof ItemQuaffable && ((ItemQuaffable) s.getItem()).canQuaff(player), (c, ce, inv) -> {
				ItemStack stack = ce.getStack();
				ItemQuaffable quaffable = (ItemQuaffable) stack.getItem();
				
				player.setAction(new ActionQuaff(
					quaffable,
					(EntityAction.CompleteCallback) entity -> quaffItemCallback(ce, inv, stack, quaffable))
				);
				
				player.getDungeon().turn();
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
	
	private void quaffItemCallback(Container.ContainerEntry ce, Container inv, ItemStack stack, ItemQuaffable quaffable) {
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
