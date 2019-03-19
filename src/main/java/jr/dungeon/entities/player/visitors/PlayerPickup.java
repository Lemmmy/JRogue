package jr.dungeon.entities.player.visitors;

import jr.dungeon.entities.Entity;
import jr.dungeon.entities.containers.Container;
import jr.dungeon.entities.containers.EntityItem;
import jr.dungeon.entities.player.Player;
import jr.dungeon.items.Item;
import jr.dungeon.items.ItemStack;
import jr.dungeon.items.valuables.ItemGold;
import jr.language.LanguageUtils;

import java.util.List;
import java.util.Optional;

public class PlayerPickup implements PlayerVisitor {
	@Override
	public void visit(Player player) {
		List<Entity> floorEntities = player.getLevel().entityStore.getEntitiesAt(player.getX(), player.getY());
		
		for (Entity entity : floorEntities) {
			if (entity instanceof EntityItem) { // TODO: Prompt if there are multiple items
				ItemStack stack = ((EntityItem) entity).getItemStack();
				Item item = stack.getItem();
				
				if (item instanceof ItemGold) {
					player.giveGold(stack.getCount());
					entity.remove();
					player.getDungeon().turnSystem.turn(player.getDungeon());
					player.getDungeon().You("pick up [YELLOW]%s[].", LanguageUtils.object(player, stack));
				} else if (player.getContainer().isPresent()) {
					Optional<Container.ContainerEntry> result = player.getContainer().get().add(stack);
					
					if (!result.isPresent()) {
						player.getDungeon().You("can't hold any more items.");
						return;
					}
					
					entity.remove();
					player.getDungeon().turnSystem.turn(player.getDungeon());
					
					player.getDungeon().You(
						"pick up [YELLOW]%s[] ([YELLOW]%s[]).",
						LanguageUtils.anObject(player, stack),
						result.get().getLetter()
					);
					
					break;
				} else {
					player.getDungeon().yellowYou("can't hold anything!");
				}
			}
		}
	}
}
