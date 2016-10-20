package pw.lemmmy.jrogue.rendering.swing;

import pw.lemmmy.jrogue.dungeon.Tiles;

public enum TileMap {
	TILE_GROUND,

	TILE_ROOM_WALL_HORIZONTAL,
	TILE_ROOM_WALL_VERTICAL,
	TILE_ROOM_WALL_CORNER,
	TILE_ROOM_FLOOR,
	TILE_ROOM_DOOR,

	TILE_CORRIDOR;

	public Tiles getTile() {
		return Tiles.valueOf(name());
	}
}
