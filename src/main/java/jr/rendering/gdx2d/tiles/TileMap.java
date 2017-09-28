package jr.rendering.gdx2d.tiles;

import jr.dungeon.Dungeon;
import jr.dungeon.tiles.TileType;
import jr.rendering.gdx2d.tiles.walls.TileRendererSewerWall;
import jr.rendering.gdx2d.tiles.walls.TileRendererWall;
import jr.rendering.gdx2d.utils.ImageLoader;
import lombok.Getter;

public enum TileMap {
	TILE_GROUND(1, 1),
	TILE_GROUND_WATER(new TileRendererWater(2, 1, 1, 1, 0.8f)),
	
	TILE_DEBUG_A(0, 15),
	TILE_DEBUG_B(1, 15),
	TILE_DEBUG_C(2, 15),
	TILE_DEBUG_D(3, 15),
	TILE_DEBUG_E(4, 15),
	TILE_DEBUG_F(5, 15),
	TILE_DEBUG_G(6, 15),
	TILE_DEBUG_H(7, 15),
	
	TILE_ROOM_WALL(new TileRendererWall()),
	TILE_ROOM_TORCH(new TileRendererTorch(6, 1, 7, 1, "torch")),
	TILE_ROOM_FLOOR(8, 0),
	TILE_ROOM_WATER(new TileRendererWater(2, 1, 8, 0, 0.8f)),
	TILE_ROOM_PUDDLE(new TileRendererWater(5, 2, 8, 0, 0.4f, false, TileType.TILE_ROOM_PUDDLE)),
	TILE_ROOM_RUG(new TileRendererRug(0, 2, 8, 0, false, TileType.TILE_ROOM_RUG)),
	TILE_ROOM_DIRT(new TileRendererConnecting(4, 2, 8, 0, false, "dirt", TileType.TILE_ROOM_DIRT)),
	TILE_ROOM_DOOR_LOCKED(new TileRendererDoor(TileRendererDoor.DoorState.LOCKED)),
	TILE_ROOM_DOOR_CLOSED(new TileRendererDoor(TileRendererDoor.DoorState.CLOSED)),
	TILE_ROOM_DOOR_OPEN(new TileRendererDoor(TileRendererDoor.DoorState.OPEN)),
	TILE_ROOM_DOOR_BROKEN(new TileRendererDoor(TileRendererDoor.DoorState.BROKEN)),
	TILE_ROOM_ICE(new TileRendererReflective(5, 1, ReflectionSettings.create(0.0f, 0.0f, 0.0f, 1.0f, -0.2f))),
	
	TILE_ROOM_STAIRS_UP(new TileRendererStairs(TileRendererStairs.StairDirection.UP, 9, 0)),
	TILE_ROOM_STAIRS_DOWN(new TileRendererStairs(TileRendererStairs.StairDirection.DOWN, 10, 0)),
	
	TILE_LADDER_UP(new TileRendererLadder(12, 0)),
	TILE_LADDER_DOWN(new TileRendererLadder(11, 0)),
	
	TILE_SEWER_WALL(new TileRendererSewerWall()),
	TILE_SEWER_WATER(new TileRendererWater(13, 1, 8, 0, 0.6f, false, TileType.TILE_SEWER_WATER)),
	TILE_SEWER_DRAIN_EMPTY(14, 1),
	TILE_SEWER_DRAIN(new TileRendererSewerDrain()),
	
	TILE_CORRIDOR(new TileRendererCorridor()),
	
	TILE_CAVE_WALL(new TileRendererConnecting(7, 2, 1, 1, false, "cavewall", TileType.TILE_CAVE_WALL, TileType.TILE_CAVE_FLOOR)),
	TILE_CAVE_FLOOR(new TileRendererCaveFloor()),

	TILE__NOISE(new TileRendererNoise(ImageLoader.getImage("textures/noise_bg.png"), 0.2f, 0.2f * Dungeon.LEVEL_WIDTH / (float)Dungeon.LEVEL_HEIGHT)),
	TILE__FLOOR(new TileRenderer_Floor(6, 6, ReflectionSettings.create(0.0f, 0.0f, 0.0f, 3.0f, -0.2f), false, TileType.TILE__FLOOR)),
	TILE__BRIDGE(new TileRenderer_Bridge(7, 6)),

	TILE_TRAP(new TileRendererTrap(ImageLoader.getImageFromSheet("textures/tiles.png", 1, 15)));

	public static final int TILE_WIDTH = 16;
	public static final int TILE_HEIGHT = 16;
	
	@Getter private TileRenderer renderer;
	
	TileMap(TileRenderer renderer) {
		this.renderer = renderer;
	}
	
	TileMap(int sheetX, int sheetY) {
		this("textures/tiles.png", sheetX, sheetY);
	}
	
	TileMap(String sheetName, int sheetX, int sheetY) {
		this.renderer = new TileRendererBasic(sheetName, sheetX, sheetY);
	}
	
	public TileType getTile() {
		return TileType.valueOf(name());
	}
}
