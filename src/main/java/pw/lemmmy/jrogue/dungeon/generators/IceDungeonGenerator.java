package pw.lemmmy.jrogue.dungeon.generators;

import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.tiles.Tile;
import pw.lemmmy.jrogue.dungeon.tiles.TileType;

public class IceDungeonGenerator extends RoomGenerator {
	public IceDungeonGenerator(Level level, Tile sourceTile) {
		super(level, sourceTile);
	}
	
	@Override
	public Climate getClimate() {
		return Climate.COLD;
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
