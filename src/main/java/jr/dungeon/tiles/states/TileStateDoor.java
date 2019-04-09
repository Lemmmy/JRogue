package jr.dungeon.tiles.states;

import com.google.gson.annotations.Expose;
import jr.dungeon.serialisation.Registered;
import jr.dungeon.tiles.Tile;
import jr.dungeon.tiles.TileType;
import jr.utils.RandomUtils;
import lombok.Getter;

@Getter
@Registered(id="tileStateDoor")
public class TileStateDoor extends TileState {
    @Expose private int health = 0;
    
    public TileStateDoor(Tile tile) {
        super(tile);
        
        if (tile.getType() != TileType.TILE_ROOM_DOOR_BROKEN) {
            health = RandomUtils.roll(2, 3);
        }
    }
    
    public int damage(int damage) {
        health = Math.max(0, health - damage);
        
        if (health <= 0) {
            getTile().setType(TileType.TILE_ROOM_DOOR_BROKEN);
        }
        
        return health;
    }
}
