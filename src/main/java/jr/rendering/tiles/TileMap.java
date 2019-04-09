package jr.rendering.tiles;

import jr.dungeon.Dungeon;
import jr.dungeon.tiles.TileType;
import jr.rendering.assets.RegisterAssetManager;
import jr.rendering.assets.UsesAssets;
import jr.rendering.tiles.walls.TileRendererSewerWall;
import jr.rendering.tiles.walls.TileRendererWall;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

@RegisterAssetManager
public enum TileMap {
    TILE_GROUND("ground"),
    TILE_GROUND_WATER(new TileRendererWater("water", "ground", 0.8f)),
    
    TILE_ROOM_WALL(new TileRendererWall()),
    TILE_ROOM_TORCH(new TileRendererTorch("torch")),
    TILE_ROOM_FLOOR("room_floor"),
    TILE_ROOM_WATER(new TileRendererWater("water", "room_floor", 0.8f)),
    TILE_ROOM_PUDDLE(new TileRendererWater("puddle", "room_floor", 0.4f, false, TileType.TILE_ROOM_PUDDLE)),
    TILE_ROOM_RUG(new TileRendererRug("rug", "room_floor", false, TileType.TILE_ROOM_RUG)),
    TILE_ROOM_DIRT(new TileRendererConnecting("dirt", "room_floor", "dirt", false, TileType.TILE_ROOM_DIRT)),
    TILE_ROOM_DOOR_LOCKED("room_door_closed"),
    TILE_ROOM_DOOR_CLOSED("room_door_closed"),
    TILE_ROOM_DOOR_OPEN(new TileRendererDoor()),
    TILE_ROOM_DOOR_BROKEN("room_door_broken"),
    TILE_ROOM_ICE(new TileRendererReflective("ice", ReflectionSettings.create(0.0f, 0.0f, 0.0f, 1.0f, -0.2f))),
    
    TILE_ROOM_STAIRS_UP(new TileRendererStairs(TileRendererStairs.StairDirection.UP, "room_stairs_up")),
    TILE_ROOM_STAIRS_DOWN(new TileRendererStairs(TileRendererStairs.StairDirection.DOWN, "room_stairs_down")),
    
    TILE_LADDER_UP(new TileRendererLadder("ladder_up")),
    TILE_LADDER_DOWN(new TileRendererLadder("ladder_down")),
    
    TILE_SEWER_WALL(new TileRendererSewerWall()),
    TILE_SEWER_WATER(new TileRendererWater("water_sewer", "room_floor", 0.6f, false, TileType.TILE_SEWER_WATER)),
    TILE_SEWER_DRAIN_EMPTY("sewer_drain_empty"),
    TILE_SEWER_DRAIN(new TileRendererSewerDrain()),
    
    TILE_CORRIDOR(new TileRendererCorridor()),
    
    TILE_CAVE_WALL(new TileRendererConnecting("cave_wall", "ground", "cave_wall", false, TileType.TILE_CAVE_WALL, TileType.TILE_CAVE_FLOOR)),
    TILE_CAVE_FLOOR(new TileRendererCaveFloor()),

    TILE__NOISE(new TileRendererNoise("noise_bg.png", 0.2f, 0.2f * Dungeon.LEVEL_WIDTH / (float) Dungeon.LEVEL_HEIGHT)),
    TILE__FLOOR(new TileRenderer_Floor( ReflectionSettings.create(0.0f, 0.0f, 0.0f, 3.0f, -0.2f), false, TileType.TILE__FLOOR)),
    TILE__BRIDGE(new TileRenderer_Bridge()),

    TILE_TRAP(new TileRendererTrap("rug")); // TODO: trap image

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
    
    public static Collection<? extends UsesAssets> getAssets() {
        return Arrays.stream(values()).map(TileMap::getRenderer).collect(Collectors.toList());
    }
}
