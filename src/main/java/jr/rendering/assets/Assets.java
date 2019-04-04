package jr.rendering.assets;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.Disposable;
import jr.rendering.entities.EntityMap;
import jr.rendering.items.ItemMap;
import jr.rendering.tiles.TileMap;

import java.util.Arrays;

public class Assets implements Disposable {
	public final AssetManager manager = new AssetManager();
	
	public final Textures textures = new Textures(this);
	public final Particles particles = new Particles(this);
	
	public void load() {
		Arrays.stream(TileMap.values()).map(TileMap::getRenderer).forEach(tr -> tr.onLoad(this));
		Arrays.stream(EntityMap.values()).map(EntityMap::getRenderer).forEach(er -> er.onLoad(this));
		Arrays.stream(ItemMap.values()).map(ItemMap::getRenderer).forEach(ir -> ir.onLoad(this));
	}
	
	private void loaded() {
		textures.onLoaded();
		particles.onLoaded();
		
		Arrays.stream(TileMap.values()).map(TileMap::getRenderer).forEach(tr -> tr.onLoaded(this));
		Arrays.stream(EntityMap.values()).map(EntityMap::getRenderer).forEach(er -> er.onLoaded(this));
		Arrays.stream(ItemMap.values()).map(ItemMap::getRenderer).forEach(ir -> ir.onLoaded(this));
	}
	
	public void syncLoad() {
		manager.finishLoading();
		loaded();
	}
	
	@Override
	public void dispose() {
		manager.dispose();
	}
}
