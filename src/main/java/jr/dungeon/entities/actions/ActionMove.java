package jr.dungeon.entities.actions;

import jr.dungeon.Level;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.containers.EntityItem;
import jr.dungeon.entities.player.Player;
import jr.dungeon.io.Messenger;
import jr.dungeon.items.ItemStack;
import jr.dungeon.tiles.Tile;
import jr.language.LanguageUtils;
import jr.language.Lexicon;
import jr.language.transformers.Capitalise;
import jr.utils.Point;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Move/walk action.
 *
 * @see Action
 */
public class ActionMove extends Action {
    private Point point;
    
    /**
     * Move/walk action.
     *
     * @param point The position to move to.
     * @param callback {@link Action.ActionCallback Callback} to call when action-related events occur.
     */
    public ActionMove(Point point, ActionCallback callback) {
        super(callback);
        this.point = point;
    }
    
    @Override
    public void execute(Entity entity, Messenger msg) {
        Level level = entity.getLevel();
        runBeforeRunCallback(entity);
        
        Optional<Entity> optUnwalkable = level.entityStore.getUnwalkableEntitiesAt(point).findFirst();
        if (optUnwalkable.isPresent()) {
            if (entity instanceof Player) {
                Entity unwalkableEnt = optUnwalkable.get();
                
                if (!unwalkableEnt.getLastPosition().equals(unwalkableEnt.getPosition())) {
                    msg.log(
                        "%s beats you to it!",
                        LanguageUtils.subject(unwalkableEnt).build(Capitalise.first)
                    );
                }
            }
            
            return;
        }
        
        entity.setPosition(point);
        
        if (entity instanceof Player) {
            Tile tile = level.tileStore.getTile(point);
            
            if (tile.getType().onWalk() != null) {
                msg.log(tile.getType().onWalk());
            }
        }
        
        level.entityStore.getWalkableEntitiesAt(point)
            .forEach(e -> e.walk((EntityLiving) entity));
        
        if (entity instanceof Player) {
            List<EntityItem> items = level.entityStore.getItemsAt(point).collect(Collectors.toList());
            
            if (items.size() == 1) {
                ItemStack stack = items.get(0).getItemStack();
                
                msg.log(
                    "There %s [YELLOW]%s[] here.",
                    LanguageUtils.autoTense((Player) entity, Lexicon.be.clone(), stack),
                    LanguageUtils.anObject((Player) entity, stack)
                );
            } else if (items.size() > 1) {
                msg.log("There are [YELLOW]%d[] items here.", items.size());
            }
        }
        
        runOnCompleteCallback(entity);
    }
}
