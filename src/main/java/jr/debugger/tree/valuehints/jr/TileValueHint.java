package jr.debugger.tree.valuehints.jr;

import jr.debugger.tree.valuehints.TypeValueHint;
import jr.debugger.tree.valuehints.TypeValueHintHandler;
import jr.dungeon.tiles.Tile;

import java.lang.reflect.Field;

@TypeValueHintHandler(Tile.class)
public class TileValueHint extends TypeValueHint<Tile> {
	@Override
	public String toValueHint(Field field, Tile instance) {
		if (instance == null) return "null tile";
		
		return String.format(
			"[P_GREY_3]%s[] %,d, %,d",
			instance.getType().name().replaceFirst("^TILE_", ""),
			instance.getX(), instance.getY()
		);
	}
}