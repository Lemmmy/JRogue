package jr.rendering.assets;

import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.graphics.Texture;

public class Textures extends AssetHandler<Texture, TextureLoader.TextureParameter> {
	public Textures(Assets assets) {
		super(assets);
	}
	
	@Override
	protected Class<Texture> getAssetClass() {
		return Texture.class;
	}
	
	@Override
	public String getFileNamePrefix() {
		return "textures/";
	}
}
