package jr.rendering.gdxvox.objects.tiles.renderers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import jr.dungeon.tiles.Tile;
import jr.rendering.gdxvox.models.magicavoxel.ModelConverter;
import jr.rendering.gdxvox.objects.tiles.TileRenderer;
import jr.rendering.gdxvox.objects.tiles.TileRendererInstance;

public class TileRendererWall extends TileRenderer {
	private ModelBuilder builder;
	private Model model;
	
	public TileRendererWall() {
		builder = new ModelBuilder();
		model = ModelConverter.loadModel("models/tiles/wall.vox");
	}
	
	@Override
	public void tileAdded(Tile tile) {
		ModelInstance instance = new ModelInstance(model);
		instance.transform.translate(tile.getX(), 0, tile.getY());
		instance.transform.scale(1f / 16f, 1f / 16f, 1f / 16f);
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
