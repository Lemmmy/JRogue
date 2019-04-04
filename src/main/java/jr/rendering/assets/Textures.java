package jr.rendering.assets;

import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PixmapPacker;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lombok.Getter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.badlogic.gdx.graphics.Texture.TextureFilter.Nearest;

public class Textures extends AssetHandler<Texture, TextureLoader.TextureParameter> {
	@Getter private final PixmapPacker pixmapPacker = new PixmapPacker(512, 512, Pixmap.Format.RGBA8888, 0, false);
	@Getter private final TextureAtlas pixmapAtlas = new TextureAtlas();
	
	private Map<String, Set<PackedTextureCallback>> callbacks = new HashMap<>();
	private Set<String> packedTextures = new HashSet<>();
	
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
	
	@Override
	public void onLoaded() {
		loadPackedTextures();
		
		super.onLoaded();
		
		callbacks.forEach((fileName, callback) ->
			callback.forEach(c -> c.onLoad(pixmapAtlas.findRegion(fileName))));
		callbacks.clear();
	}
	
	private void loadPackedTextures() {
		packedTextures.forEach(fileName -> pixmapPacker.pack(fileName, assets.manager.get(fileName)));
		pixmapPacker.updateTextureAtlas(pixmapAtlas, Nearest, Nearest, false);
	}
	
	public void loadPacked(String fileName) {
		load(fileName);
		packedTextures.add(fileName);
	}
	
	public void loadPacked(String fileName, PackedTextureCallback callback) {
		load(fileName);
		
		if (assets.manager.isLoaded(fileName)) {
			callback.onLoad(pixmapAtlas.findRegion(fileName));
		} else {
			if (!callbacks.containsKey(fileName))
				callbacks.put(fileName, new HashSet<>());
			callbacks.get(fileName).add(callback);
		}
	}
	
	@FunctionalInterface
	public interface PackedTextureCallback {
		void onLoad(TextureRegion texture);
	}
}
