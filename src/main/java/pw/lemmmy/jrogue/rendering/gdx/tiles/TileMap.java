package pw.lemmmy.jrogue.rendering.gdx.tiles;

import pw.lemmmy.jrogue.dungeon.tiles.TileType;

public enum TileMap {
	TILE_GROUND(1, 1),
	TILE_GROUND_WATER(new TileRendererWater(2, 1, 1, 1)),
	
	TILE_DEBUG_A(0, 15),
	TILE_DEBUG_B(1, 15),
	TILE_DEBUG_C(2, 15),
	TILE_DEBUG_D(3, 15),
	TILE_DEBUG_E(4, 15),
	TILE_DEBUG_F(5, 15),
	TILE_DEBUG_G(6, 15),
	TILE_DEBUG_H(7, 15),
	
	TILE_ROOM_WALL(new TileRendererWall()),
	TILE_ROOM_TORCH_FIRE(new TileRendererTorch(6, 1, "torch_fire")),
	TILE_ROOM_TORCH_ICE(new TileRendererTorch(7, 1, "torch_ice")),
	TILE_ROOM_FLOOR(8, 0),
	TILE_ROOM_WATER(new TileRendererWater(2, 1, 8, 0)),
	TILE_ROOM_PUDDLE(new TileRendererWater(4, 1, 8, 0, false, TileType.TILE_ROOM_PUDDLE)),
	TILE_ROOM_RUG(new TileRendererRug(0, 2, 8, 0, false, TileType.TILE_ROOM_RUG)),
	TILE_ROOM_DIRT(new TileRendererDirt(4, 2, 8, 0, TileType.TILE_ROOM_DIRT)),
	TILE_ROOM_DOOR_LOCKED(new TileRendererDoor(TileRendererDoor.DoorState.LOCKED)),
	TILE_ROOM_DOOR_CLOSED(new TileRendererDoor(TileRendererDoor.DoorState.CLOSED)),
	TILE_ROOM_DOOR_OPEN(new TileRendererDoor(TileRendererDoor.DoorState.OPEN)),
	TILE_ROOM_DOOR_BROKEN(new TileRendererDoor(TileRendererDoor.DoorState.BROKEN)),
	
	TILE_ROOM_STAIRS_UP(new TileRendererStairs(TileRendererStairs.StairDirection.UP, 9, 0)),
	TILE_ROOM_STAIRS_DOWN(new TileRendererStairs(TileRendererStairs.StairDirection.DOWN, 10, 0)),
	
	TILE_ROOM_LADDER_UP(12, 0),
	TILE_ROOM_LADDER_DOWN(11, 0),
	
	TILE_SEWER_WALL(new TileRendererSewerWall()),
	TILE_SEWER_WATER(new TileRendererWater(13, 1, 8, 0, false, TileType.TILE_SEWER_WATER)),
	TILE_SEWER_DRAIN_EMPTY(14, 1),
	TILE_SEWER_DRAIN(new TileRendererSewerDrain()),
	
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
	
	public TileType getTile() {
		return TileType.valueOf(name());
	}
	
	public TileRenderer getRenderer() {
		return renderer;
	}
}
