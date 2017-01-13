package pw.lemmmy.jrogue.dungeon.entities.player.visitors;

import pw.lemmmy.jrogue.dungeon.Prompt;
import pw.lemmmy.jrogue.dungeon.entities.Entity;
import pw.lemmmy.jrogue.dungeon.entities.actions.ActionEat;
import pw.lemmmy.jrogue.dungeon.entities.actions.EntityAction;
import pw.lemmmy.jrogue.dungeon.entities.containers.EntityItem;
import pw.lemmmy.jrogue.dungeon.entities.player.Player;
import pw.lemmmy.jrogue.dungeon.items.ItemStack;
import pw.lemmmy.jrogue.dungeon.items.comestibles.ItemComestible;

import java.util.List;
import java.util.Optional;

public class PlayerEat extends PlayerItemVisitor {
	@Override
	public void visit(Player player) {
		List<Entity> floorEntities = player.getLevel().getEntitiesAt(player.getX(), player.getY());
		
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
		
		String itemName = item.getName(false, false);
		String article = item.beginsWithVowel() ? "an" : "a";
		String msg = item.isis() ? String.format("There is [YELLOW]%s[] here. Eat it?", itemName) :
							  String.format("There is %s [YELLOW]%s[] here. Eat it?", article, itemName);
		
		char[] options = new char[]{'y', 'n'};
		
		player.getDungeon().prompt(new Prompt(msg, options, true, new Prompt.SimplePromptCallback(player.getDungeon()) {
			@Override
			public void onResponse(char response) {
				if (response == 'n') {
					eatFromInventory(player);
					return;
				}
				
				ItemComestible itemCopy = (ItemComestible) item.copy();
				
				player.setAction(new ActionEat(
					itemCopy,
					(EntityAction.CompleteCallback) ent -> {
						if (stack.getCount() == 1) {
							entity.getLevel().removeEntity(entity);
						} else {
							stack.subtractCount(1);
						}
						
						if (itemCopy.getEatenState() != ItemComestible.EatenState.EATEN) {
							EntityItem newStack = new EntityItem(
								player.getDungeon(),
								player.getLevel(),
								player.getX(),
								player.getY(),
								new ItemStack(itemCopy, 1)
							);
							
							player.getLevel().addEntity(newStack);
						}
					}
				));
				
				player.getDungeon().turn();
			}
		}));
	}
	
	private void eatFromInventory(Player player) {
		String msg = "Eat what?";
		
		InventoryUseResult result = useInventoryItem(player, msg, s -> s.getItem() instanceof ItemComestible, (c, ce, inv) -> {
			ItemStack stack = ce.getStack();
			ItemComestible item = (ItemComestible) stack.getItem();
			ItemComestible itemCopy = (ItemComestible) item.copy();
			
			player.setAction(new ActionEat(
				itemCopy,
				(EntityAction.CompleteCallback) entity -> {
					if (stack.getCount() == 1) {
						inv.remove(ce.getLetter());
					} else {
						stack.subtractCount(1);
					}
					
					if (itemCopy.getEatenState() != ItemComestible.EatenState.EATEN) {
						inv.add(new ItemStack(itemCopy, 1));
					}
				}
			));
			
			player.getDungeon().turn();
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
}
