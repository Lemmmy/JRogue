package jr.dungeon.entities.player.visitors;

import jr.dungeon.entities.containers.Container;
import jr.dungeon.entities.containers.EntityItem;
import jr.dungeon.entities.player.Player;
import jr.dungeon.io.YesNoPrompt;
import jr.dungeon.items.Item;
import jr.dungeon.items.ItemStack;
import jr.dungeon.items.Wieldable;
import jr.dungeon.items.valuables.ItemGold;
import jr.language.LanguageUtils;

import java.util.Optional;

public class PlayerPickup implements PlayerVisitor {
    @Override
    public void visit(Player player) {
        //TODO: prompt if there are multiple items
        Optional<EntityItem> optEntity = player.getLevel().entityStore.getItemsAt(player.getPosition()).findFirst();
        if (!optEntity.isPresent()) return;
        EntityItem entity = optEntity.get();
        
        ItemStack stack = entity.getItemStack();
        Item item = stack.getItem();
        
        if (item instanceof ItemGold) {
            player.giveGold(stack.getCount());
            entity.remove();
            player.getDungeon().turnSystem.turn();
            player.getDungeon().You("pick up [YELLOW]%s[].", LanguageUtils.object(player, stack));
        } else if (player.getContainer().isPresent()) {
            Optional<Container.ContainerEntry> result = player.getContainer().get().add(stack);
            
            if (!result.isPresent()) {
                player.getDungeon().You("can't hold any more items.");
                return;
            }
            
            entity.remove();
            player.getDungeon().turnSystem.turn();
            
            player.getDungeon().You(
                "pick up [YELLOW]%s[] ([YELLOW]%s[]).",
                LanguageUtils.anObject(player, stack),
                result.get().getLetter()
            );
            
            if (item instanceof Wieldable) { // TODO: setting to disable this behaviour
                String wieldMsg = String.format("Do you want to wield [YELLOW]%s[]?", LanguageUtils.object(player, stack));
                
                player.getDungeon().prompt(new YesNoPrompt(wieldMsg, true, yes -> {
                    if (yes) player.defaultVisitors.wield(result.get());
                }));
            }
        } else {
            player.getDungeon().yellowYou("can't hold anything!");
        }
    }
}
