package pw.lemmmy.jrogue.dungeon.entities.player.visitors;

import pw.lemmmy.jrogue.dungeon.entities.Entity;
import pw.lemmmy.jrogue.dungeon.entities.containers.Container;
import pw.lemmmy.jrogue.dungeon.entities.containers.EntityItem;
import pw.lemmmy.jrogue.dungeon.entities.player.Player;
import pw.lemmmy.jrogue.dungeon.items.Item;
import pw.lemmmy.jrogue.dungeon.items.ItemStack;
import pw.lemmmy.jrogue.dungeon.items.valuables.ItemGold;

import java.util.List;
import java.util.Optional;

public class PlayerPickup implements PlayerVisitor {
	@Override
	public void visit(Player player) {
		List<Entity> floorEntities = player.getLevel().getEntitiesAt(player.getX(), player.getY());
		
		for (Entity entity : floorEntities) {
			if (entity instanceof EntityItem) { // TODO: Prompt if there are multiple items
				ItemStack stack = ((EntityItem) entity).getItemStack();
				Item item = stack.getItem();
				
				if (item instanceof ItemGold) {
					player.giveGold(stack.getCount());
					player.getLevel().removeEntity(entity);
					player.getDungeon().turn();
					player.getDungeon().You("pick up [YELLOW]%s[].", stack.getName(false));
				} else if (player.getContainer().isPresent()) {
					Optional<Container.ContainerEntry> result = player.getContainer().get().add(stack);
					
					if (!result.isPresent()) {
						player.getDungeon().You("can't hold any more items.");
						return;
					}
					
					player.getLevel().removeEntity(entity);
					player.getDungeon().turn();
					
					if (item.isis() || stack.getCount() > 1) {
						player.getDungeon().You(
							"pick up [YELLOW]%s[] ([YELLOW]%s[]).",
							stack.getName(false),
							result.get().getLetter()
						);
					} else {
						player.getDungeon().You(
							"pick up %s [YELLOW]%s[] ([YELLOW]%s[]).",
							stack.beginsWithVowel() ? "an" : "a", stack.getName(false), result.get().getLetter()
						);
					}
					
					break;
				} else {
					player.getDungeon().yellowYou("can't hold anything!");
				}
			}
		}
	}
}
