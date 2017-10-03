package jr.rendering.gdxvox.objects.tiles.renderers;

import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import jr.dungeon.tiles.Tile;
import jr.dungeon.tiles.TileType;
import jr.rendering.gdxvox.models.magicavoxel.ModelConverter;
import jr.rendering.gdxvox.objects.tiles.TileRenderer;
import jr.rendering.gdxvox.objects.tiles.TileRendererInstance;

public class TileRendererWall extends TileRenderer {
	private ModelBuilder builder;
	private Model modelWall, modelWallCorner;
	
	public TileRendererWall() {
		builder = new ModelBuilder();
		modelWall = ModelConverter.loadModel("models/tiles/wall.vox");
		modelWallCorner = ModelConverter.loadModel("models/tiles/wall-corner.vox");
	}
	
	@Override
	public void tileAdded(Tile tile) {
		int x = tile.getX();
		int y = tile.getY();
		TileType[] adjacentTiles = tile.getLevel().tileStore.getAdjacentTileTypes(x, y);
		
		boolean h = adjacentTiles[0].isWallTile() || adjacentTiles[1].isWallTile();
		boolean v = adjacentTiles[2].isWallTile() || adjacentTiles[3].isWallTile();
		
		int dx = 0;
		int dy = h && !v ? 1 : 0;
		float rotation = 0f;
		
		if (h && !v) rotation = 90f;
		
		ModelInstance instance = new ModelInstance(h && v ? modelWallCorner : modelWall);
		instance.transform.translate(x + dx, 0, y + dy);
		instance.transform.scale(1f / 16f, 1f / 16f, 1f / 16f);
		instance.transform.rotate(0, 1, 0, rotation);
		objectInstanceMap.put(tile, new TileRendererInstance(tile, instance));
	}
	
	@Override
	public boolean shouldDraw(TileRendererInstance instance) {
		return true;
	}
	
	@Override
	public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
		objectInstanceMap.values().forEach(instance -> instance.getModelInstance().getRenderables(renderables, pool));
	}
}
