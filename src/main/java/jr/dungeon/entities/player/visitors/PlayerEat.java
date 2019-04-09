package jr.dungeon.entities.player.visitors;

import jr.dungeon.entities.actions.ActionEat;
import jr.dungeon.entities.containers.EntityItem;
import jr.dungeon.entities.player.Player;
import jr.dungeon.io.YesNoPrompt;
import jr.dungeon.items.ItemStack;
import jr.dungeon.items.comestibles.ItemComestible;
import jr.language.LanguageUtils;

import java.util.Optional;

public class PlayerEat extends PlayerItemVisitor {
	@Override
	public void visit(Player player) {
		Optional<EntityItem> floorFood = player.getLevel().entityStore.getItemsAt(player.getPosition())
			/* health and safety note: floor food is dangerous */
			.filter(e -> e.getItem() instanceof ItemComestible)
			.findFirst();
		
		if (floorFood.isPresent()) {
			eatFromFloor(player, floorFood.get());
		} else {
			eatFromInventory(player);
		}
	}
	
	private void eatFromFloor(Player player, EntityItem entity) {
		ItemStack stack = entity.getItemStack();
		ItemComestible item = (ItemComestible) entity.getItem();
		
		String msg = String.format("There is [YELLOW]%s[] here. Eat it?", LanguageUtils.anObject(player, item));
		
		player.getDungeon().prompt(new YesNoPrompt(msg, true, yes -> {
			if (!yes) {
				eatFromInventory(player);
				return;
			}
			
			if (stack.getCount() == 1) {
				entity.remove();
			} else {
				stack.subtractCount(1);
			}
			
			eatTurns(player, item);
			
			if (item.getEatenState() != ItemComestible.EatenState.EATEN) {
				EntityItem newStack = new EntityItem(
					player.getDungeon(),
					player.getLevel(),
					player.getPosition(),
					new ItemStack(item, 1)
				);
				
				player.getLevel().entityStore.addEntity(newStack);
			}
		}));
	}
	
	private void eatFromInventory(Player player) {
		String msg = "Eat what?";
		
		InventoryUseResult result = useInventoryItem(player, msg, s -> s.getItem() instanceof ItemComestible, (c, ce, inv) -> {
			ItemStack stack = ce.getStack();
			ItemComestible item = (ItemComestible) stack.getItem();
			
			if (stack.getCount() == 1) {
				inv.remove(ce.getLetter());
			} else {
				stack.subtractCount(1);
			}
			
			eatTurns(player, item);
			
			if (item.getEatenState() != ItemComestible.EatenState.EATEN) {
				inv.add(new ItemStack(item, 1));
			}
		});
		
		switch (result) {
			case NO_CONTAINER:
			case NO_ITEM:
				player.getDungeon().yellowYou("have nothing to eat.");
				break;
			default:
				break;
		}
	}
	
	private void eatTurns(Player player, ItemComestible item) {
		for (int i = 0; i < 15; i++) {
			if (i != 0) {
				player.getDungeon().turnSystem.setDoingBulkAction(true);
			}
			
			player.setAction(new ActionEat(item,null));
			player.getDungeon().turnSystem.turn();
			
			if (item.getEatenState() == ItemComestible.EatenState.EATEN) {
				break;
			}
			
			if (player.getDungeon().turnSystem.isSomethingHappened()) {
				player.getDungeon().log("You stop eating.");
				break;
			}
		}
		
		player.getDungeon().turnSystem.setDoingBulkAction(false);
	}
}
