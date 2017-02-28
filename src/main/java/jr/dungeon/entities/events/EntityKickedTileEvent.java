package jr.dungeon.entities.events;

import jr.dungeon.entities.Entity;
import jr.dungeon.entities.player.Player;
import jr.dungeon.events.DungeonEvent;
import jr.dungeon.tiles.Tile;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EntityKickedTileEvent extends DungeonEvent {
	private Entity kicker;
	private Tile tile;
	
	public boolean isKickerPlayer() {
		return kicker instanceof Player;
	}
	
	@Override
	public boolean isSelf(Object other) {
		return other.equals(kicker);
	}
}
