package jr.dungeon.entities.player.visitors;

import jr.dungeon.entities.interfaces.ReadableEntity;
import jr.dungeon.entities.player.Player;
import jr.dungeon.items.ItemStack;
import jr.dungeon.items.ReadableItem;

import java.util.Optional;

public class PlayerRead extends PlayerItemVisitor {
    @Override
    public void visit(Player player) {
        Optional<ReadableEntity> or =
            player.getLevel().entityStore.getEntitiesAt(player.getPosition())
            .filter(ReadableEntity.class::isInstance)
            .map(e -> (ReadableEntity) e)
            .filter(r -> r.canRead(player))
            .findFirst();
        
        if (or.isPresent()) {
            or.get().read(player);
        } else {
            readItem(player);
        }
    }
    
    private void readItem(Player player) {
        String msg = "Read what?";
        
        InventoryUseResult result = useInventoryItem(
            player,
            msg,
            s -> s.getItem() instanceof ReadableItem, (c, ce, inv) -> {
                ItemStack stack = ce.getStack();
                ReadableItem readable = (ReadableItem) stack.getItem();
                
                readable.onRead(player);
            }
        );
        
        switch (result) {
            case NO_CONTAINER:
            case NO_ITEM:
                player.getDungeon().yellowYou("have nothing to read.");
                break;
            default:
                break;
        }
    }
}
