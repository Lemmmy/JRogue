package jr.rendering.gdxvox.objects.tiles;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import jr.dungeon.tiles.Tile;
import jr.rendering.gdxvox.objects.AbstractObjectRendererInstance;

public class TileRendererInstance extends AbstractObjectRendererInstance<Tile> {
	public TileRendererInstance(Tile objectInstance, ModelInstance modelInstance) {
		super(objectInstance, modelInstance);
	}
	
	public Tile getTile() {
		return getObjectInstance();
	}
}
