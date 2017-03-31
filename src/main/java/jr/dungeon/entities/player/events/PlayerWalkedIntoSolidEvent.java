package jr.dungeon.entities.player.events;

import jr.dungeon.entities.player.Player;
import jr.dungeon.events.Event;
import jr.dungeon.tiles.Tile;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PlayerWalkedIntoSolidEvent extends Event {
	private Player player;
	private Tile tile;
	private int x, y;
	@Getter(AccessLevel.NONE) private int dx, dy;
	
	public int getDirectionX() {
		return dx;
	}
	
	public int getDirectionY() {
		return dy;
	}
}
