package jr.dungeon.generators;

import jr.dungeon.Level;
import jr.dungeon.serialisation.Registered;
import jr.dungeon.tiles.Tile;
import jr.dungeon.tiles.TileType;

@Registered(id="generator_")
public class Generator_ extends GeneratorStandard {
    public Generator_(Level level, Tile sourceTile) {
        super(level, sourceTile);
        
        spawnWater = false;
        spawnSewers = false;
    }
    
    @Override
    public boolean generate() {
        level.setName("________");
        
        return super.generate();
    }
    
    @Override
    public Climate getClimate() {
        return Climate.__;
    }
    
    @Override
    public MonsterSpawningStrategy getMonsterSpawningStrategy() {
        return MonsterSpawningStrategy.NONE;
    }
    
    @Override
    public TileType getTorchTileType() {
        return null;
    }
    
    @Override
    public TileType getGroundTileType() {
        return TileType.TILE__NOISE;
    }

    @Override
    public TileType getFloorTileType() {
        return TileType.TILE__FLOOR;
    }

    @Override
    public TileType getWallTileType() {
        return TileType.TILE__FLOOR;
    }
    
    @Override
    public TileType getCorridorTileType() {
        return TileType.TILE__BRIDGE;
    }
}
