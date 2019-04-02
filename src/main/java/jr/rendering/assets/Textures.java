package jr.rendering.assets;

import com.badlogic.gdx.graphics.Texture;

public class Textures extends AssetHandler<Texture> {
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
