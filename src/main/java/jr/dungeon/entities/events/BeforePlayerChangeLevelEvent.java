package jr.dungeon.entities.events;

import jr.dungeon.entities.player.Player;
import jr.dungeon.events.Event;
import jr.dungeon.tiles.Tile;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BeforePlayerChangeLevelEvent extends Event {
    private Player player;
    private Tile src;
    private Tile dest;
    
    @Override
    public boolean isSelf(Object other) {
        return other.equals(player);
    }
}
