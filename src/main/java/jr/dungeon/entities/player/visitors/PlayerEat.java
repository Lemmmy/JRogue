package jr.dungeon.entities.player.visitors;

import jr.dungeon.io.Prompt;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.actions.ActionEat;
import jr.dungeon.entities.containers.EntityItem;
import jr.dungeon.entities.player.Player;
import jr.dungeon.items.ItemStack;
import jr.dungeon.items.comestibles.ItemComestible;
import jr.language.LanguageUtils;
import jr.language.Noun;

import java.util.List;
import java.util.Optional;

public class PlayerEat extends PlayerItemVisitor {
	@Override
	public void visit(Player player) {
		List<Entity> floorEntities = player.getLevel().entityStore.getEntitiesAt(player.getX(), player.getY());
		
		Optional<Entity> floorFood = floorEntities.stream()
			/* health and safety note: floor food is dangerous */
			.filter(e -> e instanceof EntityItem)
			.filter(e -> ((EntityItem) e).getItem() instanceof ItemComestible)
			.findFirst();
		
		if (floorFood.isPresent()) {
			eatFromFloor(player, (EntityItem) floorFood.get());
		} else {
			eatFromInventory(player);
		}
	}
	
	private void eatFromFloor(Player player, EntityItem entity) {
		ItemStack stack = entity.getItemStack();
		ItemComestible item = (ItemComestible) entity.getItem();
		
		String msg = String.format("There is [YELLOW]%s[] here. Eat it?", LanguageUtils.anObject(player, item));
		char[] options = new char[]{'y', 'n'};
		
		player.getDungeon().prompt(new Prompt(msg, options, true, new Prompt.SimplePromptCallback(player.getDungeon()) {
			@Override
			public void onResponse(char response) {
				if (response == 'n') {
					eatFromInventory(player);
					return;
				}
				
				if (stack.getCount() == 1) {
					entity.getLevel().entityStore.removeEntity(entity);
				} else {
					stack.subtractCount(1);
				}
				
				eatTurns(player, item);
				
				if (item.getEatenState() != ItemComestible.EatenState.EATEN) {
					EntityItem newStack = new EntityItem(
						player.getDungeon(),
						player.getLevel(),
						player.getX(),
						player.getY(),
						new ItemStack(item, 1)
					);
					
					player.getLevel().entityStore.addEntity(newStack);
				}
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
			player.getDungeon().turnSystem.turn(player.getDungeon());
			
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
