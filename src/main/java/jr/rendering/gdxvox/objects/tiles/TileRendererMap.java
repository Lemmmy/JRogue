package jr.rendering.gdxvox.objects.tiles;

import jr.dungeon.Level;
import jr.dungeon.tiles.Tile;
import jr.dungeon.tiles.TileType;
import jr.rendering.gdxvox.objects.AbstractObjectRendererMap;

import java.lang.annotation.Annotation;

public class TileRendererMap extends AbstractObjectRendererMap {
	@Override
	public void findObjects(Level level) {
		for (Tile tile : level.tileStore.getTiles()) {
			TileType type = tile.getType();
			
			if (!objectRendererMap.containsKey(type)) continue;
			((TileRenderer) objectRendererMap.get(type)).objectAdded(tile);
		}
	}
	
	@Override
	public Class getObjectKeyClass() {
		return TileType.class;
	}
	
	@Override
	public Class getObjectValueClass() {
		return Tile.class;
	}
	
	@Override
	public Class<? extends Annotation> getListAnnotationClass() {
		return TileRendererList.class;
	}
}
