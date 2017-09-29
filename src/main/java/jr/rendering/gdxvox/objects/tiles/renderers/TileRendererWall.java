package jr.rendering.gdxvox.objects.tiles.renderers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import jr.dungeon.tiles.Tile;
import jr.rendering.gdxvox.objects.tiles.TileRenderer;
import jr.rendering.gdxvox.objects.tiles.TileRendererInstance;

public class TileRendererWall extends TileRenderer {
	private ModelBuilder builder;
	private Model model;
	
	public TileRendererWall() {
		builder = new ModelBuilder();
		model = builder.createBox(
			1f, 1f, 1f,
			new Material(ColorAttribute.createDiffuse(Color.GREEN)),
			VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal
		);
	}
	
	@Override
	public void tileAdded(Tile tile) {
		objectInstanceMap.put(tile, new TileRendererInstance(tile, new ModelInstance(model)));
	}
	
	@Override
	public void render(ModelBatch batch, TileRendererInstance instance) {
		batch.render(instance.getModelInstance());
	}
	
	@Override
	public boolean shouldDraw(TileRendererInstance instance) {
		return true;
	}
}
