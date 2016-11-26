package pw.lemmmy.jrogue.dungeon.tiles;

import pw.lemmmy.jrogue.JRogue;
import pw.lemmmy.jrogue.utils.Utils;

public class TileStateDoor extends TileState {
	private int health = 0;

	public TileStateDoor(Tile tile) {
		super(tile);

		if (tile.getType() == TileType.TILE_ROOM_DOOR_CLOSED || tile.getType() == TileType.TILE_ROOM_DOOR_OPEN) {
			health = Utils.roll(2, 3);
		}
	}

	public int getHealth() {
		return health;
	}

	public int damage(int damage) {
		health = Math.max(0, health - damage);

		if (health <= 0) {
			getTile().setType(TileType.TILE_ROOM_DOOR_BROKEN);
		}

		return health;
	}
}
