package jr.rendering.assets;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.Disposable;

public class Assets implements Disposable {
	public final AssetManager manager = new AssetManager();
	
	public final Textures textures = new Textures(this);
	public final Particles particles = new Particles(this);
	
	public void load() {
	
	}
	
	private void loaded() {
		textures.onLoaded();
		particles.onLoaded();
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
