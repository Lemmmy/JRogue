package jr.dungeon.tiles.events;

import jr.dungeon.events.Event;
import jr.dungeon.tiles.Tile;
import jr.dungeon.tiles.TileType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TileChangedEvent extends Event {
    private Tile tile;
    private TileType oldType;
    private TileType newType;
}