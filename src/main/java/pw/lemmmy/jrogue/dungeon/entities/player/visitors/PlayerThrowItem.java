package pw.lemmmy.jrogue.dungeon.entities.player.visitors;

import pw.lemmmy.jrogue.dungeon.Prompt;
import pw.lemmmy.jrogue.dungeon.entities.player.Player;
import pw.lemmmy.jrogue.dungeon.items.Item;
import pw.lemmmy.jrogue.dungeon.items.ItemStack;
import pw.lemmmy.jrogue.dungeon.items.projectiles.ItemProjectile;
import pw.lemmmy.jrogue.dungeon.items.weapons.ItemProjectileLauncher;
import pw.lemmmy.jrogue.utils.Utils;

public class PlayerThrowItem extends PlayerItemVisitor {
	@Override
	public void visit(Player player) {
		String msg = "Throw what?";
		
		InventoryUseResult result = useInventoryItem(player, msg, is -> true, (c, ce, inv) -> {
			ItemStack stack = ce.getStack();
			Item item = stack.getItem();
			
			String msg2 = "In what direction?";
			
			player.getDungeon().prompt(new Prompt(msg2, null, true, new Prompt.SimplePromptCallback(player.getDungeon()) {
				@Override
				public void onResponse(char response) {
					if (!Utils.MOVEMENT_CHARS.containsKey(response)) {
						player.getDungeon().log(String.format("Invalid direction '[YELLOW]%s[]'.", response));
						return;
					}
					
					Integer[] d = Utils.MOVEMENT_CHARS.get(response);
					int dx = d[0];
					int dy = d[1];
					
					if (
						item instanceof ItemProjectile &&
							player.getRightHand() != null &&
							player.getRightHand().getItem() instanceof ItemProjectileLauncher
						) {
						ItemProjectileLauncher launcher = (ItemProjectileLauncher) player.getRightHand().getItem();
						boolean fired = launcher.fire(player, (ItemProjectile) item, dx, dy);
						
						if (fired) {
							if (stack.getCount() <= 1) {
								inv.remove(ce.getLetter());
							} else {
								stack.subtractCount(1);
							}
						}
					} else {
						// TODO: regular item throwing
					}
					
					player.getDungeon().turn();
				}
			}));
		});
		
		switch (result) {
			case NO_CONTAINER:
				player.getDungeon().yellowYou("can't hold anything!");
				break;
			case NO_ITEM:
				player.getDungeon().yellowYou("don't have any items to throw!");
				break;
			default:
				break;
		}
	}
}
