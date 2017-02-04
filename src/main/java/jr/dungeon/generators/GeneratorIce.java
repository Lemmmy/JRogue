package jr.dungeon.generators;

import jr.dungeon.Level;
import jr.dungeon.generators.rooms.RoomBasic;
import jr.dungeon.generators.rooms.RoomIce;
import jr.dungeon.tiles.Tile;
import jr.dungeon.tiles.TileType;

public class GeneratorIce extends GeneratorRooms {
	{
		roomTypes.clear();
		
		roomTypes.add(15, RoomBasic.class);
		roomTypes.add(1, RoomIce.class);
	}
	
	public GeneratorIce(Level level, Tile sourceTile) {
		super(level, sourceTile);
	}
	
	@Override
	public Climate getClimate() {
		return Climate.COLD;
	}
	
	@Override
	public Class<? extends DungeonGenerator> getNextGenerator() {
		return GeneratorIce.class;
	}
	
	@Override
	public MonsterSpawningStrategy getMonsterSpawningStrategy() {
		return MonsterSpawningStrategy.ICE;
	}
	
	@Override
	public boolean generate() {
		if (!super.generate()) {
			return false;
		}
		
		return verify();
	}
	
	@Override
	public TileType getTorchTileType() {
		return TileType.TILE_ROOM_TORCH_ICE;
	}
}
