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
	@Getter private final PixmapPacker mainPacker = new PixmapPacker(512, 512, Pixmap.Format.RGBA8888, 0, false);
	@Getter private final TextureAtlas mainAtlas = new TextureAtlas();
	
	@Getter private final PixmapPacker blobPacker = new PixmapPacker(512, 512, Pixmap.Format.RGBA8888, 0, false);
	@Getter private final TextureAtlas blobAtlas = new TextureAtlas();
	
	private Map<String, Set<PackedTextureCallback>> callbacks = new HashMap<>();
	private Set<String> packedTextures = new HashSet<>();
	
	public Textures(Assets assets) {
		super(assets);
	}
	
	public static String tileFile(String fileName) {
		return "tiles/" + fileName + ".png";
	}
	
	public static String blobFile(String fileName) {
		return "blobs/" + fileName + ".png";
	}
	
	public static String entityFile(String fileName) {
		return "entities/" + fileName + ".png";
	}
	
	public static String itemFile(String fileName) {
		return "items/" + fileName + ".png";
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
		
		callbacks.forEach((fileName, callbackSet) -> callbackSet.forEach(c -> c.onLoad(getPacked(fileName))));
		callbacks.clear();
	}
	
	@Override
	public void afterLoaded() {
		super.afterLoaded();
		blobPacker.updateTextureAtlas(blobAtlas, Nearest, Nearest, false);
	}
	
	private TextureRegion getPacked(String fileName) {
		TextureRegion region = mainAtlas.findRegion(fileName);
		assert region != null : "Couldn't find atlas region " + fileName;
		return region;
	}
	
	private void loadPackedTextures() {
		packedTextures.forEach(fileName -> {
			Texture texture = getLoaded(fileName);
			if (!texture.getTextureData().isPrepared()) texture.getTextureData().prepare();
			mainPacker.pack(fileName, texture.getTextureData().consumePixmap());
		});
		mainPacker.updateTextureAtlas(mainAtlas, Nearest, Nearest, false);
	}
	
	public void loadPacked(String rawFileName) {
		String fileName = getPrefixedFileName(rawFileName);
		load(fileName);
		packedTextures.add(fileName);
	}
	
	public void loadPacked(String rawFileName, PackedTextureCallback callback) {
		String fileName = getPrefixedFileName(rawFileName);
		
		loadPacked(fileName);
		
		if (assets.manager.isLoaded(fileName)) {
			callback.onLoad(getPacked(fileName));
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
