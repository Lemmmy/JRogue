package jr.dungeon.entities.player.visitors;

import jr.dungeon.entities.actions.Action;
import jr.dungeon.entities.actions.ActionMove;
import jr.dungeon.entities.player.Player;
import jr.dungeon.events.PathShowEvent;
import jr.dungeon.tiles.Solidity;
import jr.dungeon.tiles.Tile;
import jr.utils.Path;
import jr.utils.Point;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@AllArgsConstructor
public class PlayerTravelPathfind implements PlayerVisitor {
    private Point position;
    
    @Override
    public void visit(Player player) {
        Tile destTile = player.getLevel().tileStore.getTile(position);
        
        if (destTile == null || !player.getLevel().visibilityStore.isTileDiscovered(position)) {
            player.getDungeon().You("can't travel there.");
            return;
        }
        
        Path path = player.getPathfinder().findPath(
            player.getLevel(),
            player.getPosition(),
            position,
            50,
            true,
            new ArrayList<>()
        );
        
        Path pathTaken = new Path();
        
        if (path == null || path.getLength() == 0) {
            player.getDungeon().You("can't travel there.");
            return;
        }
        
        AtomicBoolean stop = new AtomicBoolean(false);
        AtomicInteger i = new AtomicInteger(0);
        
        path.forEach(step -> {
            i.incrementAndGet();
            
            if (stop.get()) { return; }
            if (player.getPosition().equals(step.position)) { return; }
            
            if (step.getType().getSolidity() == Solidity.SOLID) {
                stop.set(true);
                return;
            }
            
            Point oldPos = player.getPosition();
            
            pathTaken.addStep(step);
            player.setAction(new ActionMove(step.position, new Action.NoCallback()));
            player.getDungeon().turnSystem.turn();
            
            if (oldPos.equals(player.getPosition())) {
                stop.set(true);
                return;
            }
            
            if (i.get() > 2 && player.getLevel().entityStore.getAdjacentMonsters(player.getPosition()).findAny().isPresent()) {
                stop.set(true);
            }
        });
        
        player.getDungeon().eventSystem.triggerEvent(new PathShowEvent(pathTaken));
    }
}
