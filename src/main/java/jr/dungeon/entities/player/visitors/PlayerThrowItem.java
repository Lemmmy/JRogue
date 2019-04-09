package jr.dungeon.entities.player.visitors;

import jr.dungeon.entities.containers.Container;
import jr.dungeon.entities.player.Player;
import jr.dungeon.io.Prompt;
import jr.dungeon.items.Item;
import jr.dungeon.items.ItemStack;
import jr.dungeon.items.projectiles.ItemProjectile;
import jr.dungeon.items.weapons.ItemProjectileLauncher;
import jr.utils.Directions;
import jr.utils.VectorInt;

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
                    if (!Directions.MOVEMENT_CHARS.containsKey(response)) {
                        player.getDungeon().log(String.format("Invalid direction '[YELLOW]%s[]'.", response));
                        return;
                    }
                    
                    throwItem(player, response, item, stack, inv, ce);
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
    
    private void throwItem(Player player,
                           char response,
                           Item item,
                           ItemStack stack,
                           Container inv,
                           Container.ContainerEntry ce) {
        VectorInt direction = Directions.MOVEMENT_CHARS.get(response);
        
        if (
            item instanceof ItemProjectile &&
            player.getRightHand() != null &&
            player.getRightHand().getItem() instanceof ItemProjectileLauncher
        ) {
            ItemProjectileLauncher launcher = (ItemProjectileLauncher) player.getRightHand().getItem();
            boolean fired = launcher.fire(player, (ItemProjectile) item, direction);
            
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
        
        player.getDungeon().turnSystem.turn();
    }
}
