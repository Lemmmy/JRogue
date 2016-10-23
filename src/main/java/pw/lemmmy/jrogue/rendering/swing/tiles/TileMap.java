package pw.lemmmy.jrogue.rendering.swing.tiles;

import pw.lemmmy.jrogue.dungeon.Tiles;

public enum TileMap {
	TILE_EMPTY(1, 1),

	TILE_ROOM_WALL(new TileRendererWall()),
	TILE_ROOM_FLOOR(8, 0),
	TILE_ROOM_DOOR(new TileRendererDoor()),

	TILE_CORRIDOR(new TileRendererCorridor());

	public static final int TILE_WIDTH = 16;
	public static final int TILE_HEIGHT = 16;

	private TileRenderer renderer;

	TileMap(TileRenderer renderer) {
		this.renderer = renderer;
	}

	TileMap(int sheetX, int sheetY) {
		this("tiles.png", sheetX, sheetY);
	}

	TileMap(String sheetName, int sheetX, int sheetY) {
		this.renderer = new TileRendererBasic(sheetName, sheetX, sheetY);
	}

	public Tiles getTile() {
		return Tiles.valueOf(name());
	}

	public TileRenderer getRenderer() {
		return renderer;
	}
}
