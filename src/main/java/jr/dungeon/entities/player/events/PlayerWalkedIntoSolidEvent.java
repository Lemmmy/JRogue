package jr.dungeon.entities.player.events;

import jr.dungeon.entities.player.Player;
import jr.dungeon.events.Event;
import jr.dungeon.tiles.Tile;
import jr.utils.Point;
import jr.utils.VectorInt;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PlayerWalkedIntoSolidEvent extends Event {
    private Player player;
    private Tile tile;
    private Point position;
    private VectorInt direction;
}
