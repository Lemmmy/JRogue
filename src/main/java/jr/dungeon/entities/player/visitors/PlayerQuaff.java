package jr.dungeon.entities.player.visitors;

import jr.dungeon.Prompt;
import jr.dungeon.entities.actions.Action;
import jr.dungeon.entities.actions.ActionQuaffEntity;
import jr.dungeon.entities.actions.ActionQuaffItem;
import jr.dungeon.entities.containers.Container;
import jr.dungeon.entities.interfaces.Quaffable;
import jr.dungeon.entities.player.Player;
import jr.dungeon.items.ItemStack;
import jr.dungeon.items.quaffable.ItemQuaffable;
import jr.dungeon.items.quaffable.potions.ItemPotion;

import java.util.concurrent.atomic.AtomicBoolean;

public class PlayerQuaff extends PlayerItemVisitor {
	@Override
	public void visit(Player player) {
		AtomicBoolean cancelled = new AtomicBoolean(false);
		
		player.getLevel().getEntityStore().getEntitiesAt(player.getX(), player.getY()).stream()
			.filter(Quaffable.class::isInstance)
			.map(e -> (Quaffable) e)
			.filter(q -> q.canQuaff(player))
			.findFirst()
			.ifPresent(q -> {
				String msg = q.getQuaffConfirmationMessage(player);
				
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
	
	private void quaffEntity(Player player, Quaffable quaffable) {
		player.setAction(new ActionQuaffEntity(quaffable, null));
		
		player.getDungeon().getTurnSystem().turn(player.getDungeon());
	}
	
	private void quaffItem(Player player) {
		String msg = "Quaff what?";
		
		InventoryUseResult result = useInventoryItem(
			player,
			msg,
			s -> s.getItem() instanceof ItemQuaffable && ((ItemQuaffable) s.getItem()).canQuaff(player), (c, ce, inv) -> {
				ItemStack stack = ce.getStack();
				ItemQuaffable quaffable = (ItemQuaffable) stack.getItem();
				
				player.setAction(new ActionQuaffItem(
					quaffable,
					(Action.CompleteCallback) entity -> quaffItemCallback(ce, inv, stack, quaffable))
				);
				
				player.getDungeon().getTurnSystem().turn(player.getDungeon());
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
