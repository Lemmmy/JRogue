package jr.rendering.gdxvox.objects.tiles;

import jr.dungeon.tiles.TileType;
import jr.rendering.gdxvox.objects.tiles.renderers.TileRendererWall;

public class TileRenderers {
	@TileRendererList
	public static void addRenderers(TileRendererMap t) {
		t.addRenderers(new TileRendererWall(), TileType.TILE_ROOM_WALL);
	}
}
