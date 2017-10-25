package jr.rendering.gdxvox.objects.tiles;

import jr.dungeon.Level;
import jr.dungeon.tiles.Tile;
import jr.dungeon.tiles.TileType;
import jr.rendering.gdxvox.context.SceneContext;
import jr.rendering.gdxvox.objects.AbstractObjectRendererManager;

import java.lang.annotation.Annotation;

public class TileRendererManager extends AbstractObjectRendererManager<TileType, Tile, TileVoxelBatch, TileRenderer> {
	public TileRendererManager(SceneContext scene) {
		super(scene);
	}
	
	@Override
	public TileVoxelBatch initialiseBatch() {
		return new TileVoxelBatch(getClass());
	}
	
	@Override
	public TileVoxelBatch[] initialiseStaticBatchArray(int size) {
		return new TileVoxelBatch[size];
	}
	
	@Override
	public void findObjects(Level level) {
		for (Tile tile : level.tileStore.getTiles()) {
			TileType type = tile.getType();
			
			if (!objectRendererMap.containsKey(type)) continue;
			objectRendererMap.get(type).objectAdded(tile);
		}
	}
	
	@Override
	public Class<? extends TileType> getObjectKeyClass() {
		return TileType.class;
	}
	
	@Override
	public Class<? extends Tile> getObjectValueClass() {
		return Tile.class;
	}
	
	@Override
	public Class<? extends Annotation> getListAnnotationClass() {
		return TileRendererList.class;
	}
}
