package jr.debugger.tree.namehints.jr;

import jr.debugger.tree.namehints.TypeNameHint;
import jr.debugger.tree.namehints.TypeNameHintHandler;
import jr.dungeon.tiles.Tile;

import java.lang.reflect.Field;

@TypeNameHintHandler(Tile.class)
public class TileNameHint extends TypeNameHint<Tile> {
	@Override
	public String toNameHint(Field field, Tile instance) {
		if (instance == null) return "null tile";
		
		return String.format(
			"[P_GREY_3]%s[] %,d, %,d",
			instance.getType().name().replaceFirst("^TILE_", ""),
			instance.getX(), instance.getY()
		);
	}
}