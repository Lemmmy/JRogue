package jr.rendering.gdxvox.objects.tiles;

import jr.dungeon.Level;
import jr.dungeon.tiles.Tile;
import jr.dungeon.tiles.TileType;
import jr.rendering.gdxvox.objects.AbstractObjectRendererMap;
import jr.rendering.gdxvox.utils.SceneContext;

import java.lang.annotation.Annotation;

public class TileRendererMap extends AbstractObjectRendererMap<TileType, Tile, TileRenderer> {
	public TileRendererMap(SceneContext scene) {
		super(scene);
	}
	
	@Override
	public void findObjects(Level level) {
		for (Tile tile : level.tileStore.getTiles()) {
			TileType type = tile.getType();
			
			if (!objectRendererMap.containsKey(type)) continue;
			objectRendererMap.get(type).objectAdded(tile, getScene());
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
