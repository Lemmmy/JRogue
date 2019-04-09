package jr.dungeon.entities.monsters.ai;

import com.google.gson.annotations.Expose;
import jr.dungeon.entities.monsters.Monster;
import jr.dungeon.serialisation.Registered;
import jr.dungeon.tiles.Tile;
import jr.dungeon.tiles.TileType;
import jr.utils.RandomUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.Objects;

@Registered(id="aiFish")
public class FishAI extends AI {
    private static final int SLEEP_DISTANCE = 20;
    
    @Expose @Getter @Setter private float moveProbability = 0.1f;
    
    public FishAI(Monster monster) {
        super(monster);
    }
    
    protected FishAI() { super(); }
    
    @Override
    public void update() {
        if (distanceFromPlayer() >= SLEEP_DISTANCE) {
            return; // no need to move if we're far away from the player
        }
        
        if (RandomUtils.randomFloat() < moveProbability) {
            Tile[] tiles = getLevel().tileStore.getAdjacentTiles(getMonster().getPosition());
            Tile[] waterTiles = Arrays.stream(tiles)
                .filter(Objects::nonNull)
                .filter(t -> t.getType() == TileType.TILE_GROUND_WATER)
                .toArray(Tile[]::new);
            
            if (waterTiles.length > 0) {
                Tile destination = RandomUtils.randomFrom(waterTiles);
                moveTowards(destination.position);
            }
        }
    }
}
