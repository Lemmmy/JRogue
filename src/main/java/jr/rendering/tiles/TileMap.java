package jr.rendering.tiles;

import jr.dungeon.Dungeon;
import jr.dungeon.tiles.TileType;
import jr.rendering.tiles.walls.TileRendererSewerWall;
import jr.rendering.tiles.walls.TileRendererWall;
import jr.rendering.utils.ImageLoader;
import lombok.Getter;

public enum TileMap {
	TILE_GROUND("ground"),
	TILE_GROUND_WATER(new TileRendererWater("water", "ground", 0.8f)),
	
	TILE_ROOM_WALL(new TileRendererWall()),
	TILE_ROOM_TORCH(new TileRendererTorch(6, 1, 7, 1, "torch")),
	TILE_ROOM_FLOOR("room_floor"),
	TILE_ROOM_WATER(new TileRendererWater("water", "room_floor", 0.8f)),
	TILE_ROOM_PUDDLE(new TileRendererWater("puddle", "room_floor", 0.4f, false, TileType.TILE_ROOM_PUDDLE)),
	TILE_ROOM_RUG(new TileRendererRug(0, 2, 8, 0, false, TileType.TILE_ROOM_RUG)),
	TILE_ROOM_DIRT(new TileRendererConnecting(4, 2, 8, 0, false, "dirt", TileType.TILE_ROOM_DIRT)),
	TILE_ROOM_DOOR_LOCKED("room_door_closed"),
	TILE_ROOM_DOOR_CLOSED("room_door_closed"),
	TILE_ROOM_DOOR_OPEN(new TileRendererDoor()),
	TILE_ROOM_DOOR_BROKEN("room_door_broken"),
	TILE_ROOM_ICE(new TileRendererReflective(5, 1, ReflectionSettings.create(0.0f, 0.0f, 0.0f, 1.0f, -0.2f))),
	
	TILE_ROOM_STAIRS_UP(new TileRendererStairs(TileRendererStairs.StairDirection.UP, 9, 0)),
	TILE_ROOM_STAIRS_DOWN(new TileRendererStairs(TileRendererStairs.StairDirection.DOWN, 10, 0)),
	
	TILE_LADDER_UP(new TileRendererLadder(12, 0)),
	TILE_LADDER_DOWN(new TileRendererLadder(11, 0)),
	
	TILE_SEWER_WALL(new TileRendererSewerWall()),
	TILE_SEWER_WATER(new TileRendererWater("water_sewer", "room_floor", 0.6f, false, TileType.TILE_SEWER_WATER)),
	TILE_SEWER_DRAIN_EMPTY("sewer_drain_empty"),
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
	
	TileMap(String fileName) {
		this.renderer = new TileRendererBasic(fileName);
	}
	
	public TileType getTile() {
		return TileType.valueOf(name());
	}
}
