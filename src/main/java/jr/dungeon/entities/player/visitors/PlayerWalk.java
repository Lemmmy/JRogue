package jr.dungeon.entities.player.visitors;

import jr.dungeon.entities.Entity;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.actions.Action;
import jr.dungeon.entities.actions.ActionMove;
import jr.dungeon.entities.interfaces.Friendly;
import jr.dungeon.entities.monsters.familiars.Familiar;
import jr.dungeon.entities.player.Player;
import jr.dungeon.entities.player.events.PlayerWalkedIntoSolidEvent;
import jr.dungeon.io.YesNoPrompt;
import jr.dungeon.items.weapons.ItemWeaponMelee;
import jr.dungeon.tiles.Solidity;
import jr.dungeon.tiles.Tile;
import jr.utils.Point;
import jr.utils.VectorInt;
import lombok.AllArgsConstructor;

import java.util.Optional;

@AllArgsConstructor
public class PlayerWalk implements PlayerVisitor {
    private VectorInt direction;
    
    @Override
    public void visit(Player player) {
        Point newPosition = player.getPosition().add(direction);
        
        Tile tile = player.getLevel().tileStore.getTile(newPosition);
        if (tile == null) return;
        
        boolean acted = true;
        
        Optional<Entity> optEntity = player.getLevel().entityStore.getEntitiesAt(newPosition)
            .filter(e -> e instanceof EntityLiving)
            .findFirst();
        
        // TODO: if multiple entities occupy this tile, popup a dialog asking which one to attack
        
        if (optEntity.isPresent()) {
            Entity entity = optEntity.get();
            
            if (entity instanceof Friendly && ((Friendly) entity).isFriendly()) {
                if (entity instanceof Familiar) {
                    swapPlaces(player, (Familiar) entity, tile, newPosition);
                } else {
                    friendlyQuery(player, entity);
                    acted = false;
                }
            } else {
                meleeAction(player, entity);
            }
        } else {
            walkAction(player, tile, newPosition);
        }
        
        if (acted) {
            player.getDungeon().turnSystem.turn();
        }
    }
    
    private void swapPlaces(Player player, EntityLiving ent, Tile tile, Point newPosition) {
        walkAction(player, tile, newPosition);
        ent.setAction(new ActionMove(player.getLastPosition(), new Action.NoCallback()));
        
        player.getDungeon().You("swap places with [CYAN]%s[].", ent.getName(player));
    }
    
    private void friendlyQuery(Player player, Entity ent) {
        String msg = "Are you sure you want to attack [CYAN]" + ent.getName(player) + "[]?";
        
        player.getDungeon().prompt(new YesNoPrompt(msg, true, yes -> {
            if (yes) {
                meleeAction(player, ent);
                player.getDungeon().turnSystem.turn();
            }
        }));
    }
    
    private void meleeAction(Player player, Entity ent) {
        if (ent.equals(player)) return;
        
        if (player.getRightHand() != null && player.getRightHand().getItem() instanceof ItemWeaponMelee) {
            ((ItemWeaponMelee) player.getRightHand().getItem()).hit(player, (EntityLiving) ent);
        } else if (player.getLeftHand() != null && player.getLeftHand().getItem() instanceof ItemWeaponMelee) {
            ((ItemWeaponMelee) player.getLeftHand().getItem()).hit(player, (EntityLiving) ent);
        } else {
            player.getDungeon().You("have no weapon equipped!"); // TODO: Make it possible to attack bare-handed
        }
    }
    
    private void walkAction(Player player, Tile tile, Point newPosition) {
        if (tile.getType().getSolidity() != Solidity.SOLID) {
            player.setAction(new ActionMove(newPosition, new Action.NoCallback()));
        } else {
            player.getDungeon().eventSystem
                .triggerEvent(new PlayerWalkedIntoSolidEvent(player, tile, newPosition, direction));
        }
    }
}
