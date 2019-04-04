package jr.rendering.assets;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import jr.JRogue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Used by classes that handles specific types of assets, e.g. {@link Textures}.
 *
 * When loading assets, the file names will be prefixed with {@link #getFileNamePrefix()} before loading them, unless
 * their file name already begins with a forward slash ({@code /}).
 *
 * @param <T> The type of asset that this class handles.
 */
public abstract class AssetHandler<T, P extends AssetLoaderParameters> {
	private Map<String, Set<AssetCallback<T>>> callbacks = new HashMap<>();
	
	protected Assets assets;
	
	public AssetHandler(Assets assets) {
		this.assets = assets;
	}
	
	/**
	 * @return The type of asset this AssetHandler deals with. Should be the same as the class's type parameter T.
	 */
	protected abstract Class<T> getAssetClass();
	
	/**
	 * @return The string to prepend before all file names, unless they already begin with a forward slash ({@code /}).
	 */
	public String getFileNamePrefix() {
		return "";
	}
	
	protected String getPrefixedFileName(String fileName) {
		return fileName.startsWith("/")
			   ? fileName.replaceFirst("/", "")
			   : fileName.startsWith(getFileNamePrefix())
				 	? fileName
					: getFileNamePrefix() + fileName;
	}
	
	/**
	 * Called after all assets have been loaded and are ready for use.
	 */
	public void onLoaded() {
		callbacks.forEach((fileName, callbackSet) -> callbackSet.forEach(c -> c.onLoad(getLoaded(fileName))));
				callbacks.clear();
	}
	
	public void afterLoaded() {}
	
	public P getAssetParameters(String fileName) {
		return null;
	}
	
	/**
	 * Queues an asset to be loaded.
	 *
	 * @param rawFileName The file name of the asset to load.
	 */
	public void load(String rawFileName) {
		String fileName = getPrefixedFileName(rawFileName);
		
		if (assets.manager.contains(fileName)) return;
		
		JRogue.getLogger().debug("Loading asset {}", fileName);
		assets.manager.load(fileName, getAssetClass(), getAssetParameters(fileName));
	}
	
	/**
	 * Queues an asset to be loaded, and registers a callback to be called when it has loaded.
	 *
	 * @param rawFileName The file name of the asset to load.
	 * @param callback The callback to be called when the asset has been loaded.
	 */
	public void load(String rawFileName, AssetCallback<T> callback) {
		String fileName = getPrefixedFileName(rawFileName);
		
		if (!assets.manager.contains(fileName)) {
			load(fileName);
		}
		
		if (assets.manager.isLoaded(fileName)) {
			callback.onLoad(getLoaded(fileName));
		} else {
			if (!callbacks.containsKey(fileName))
				callbacks.put(fileName, new HashSet<>());
			callbacks.get(fileName).add(callback);
		}
	}
	
	/**
	 * Immediately loads an asset. Useful for the loading screen.
	 *
	 * @param rawFileName The file name of the asset to load.
	 */
	public T loadImmediately(String rawFileName) {
		String fileName = getPrefixedFileName(rawFileName);
		if (!assets.manager.contains(fileName)) assets.manager.load(fileName, getAssetClass());
		if (!assets.manager.isLoaded(fileName)) assets.manager.finishLoadingAsset(fileName);
		return assets.manager.get(fileName);
	}
	
	protected T getLoaded(String fileName) {
		T asset = assets.manager.get(fileName);
		assert asset != null : "Missing asset " + fileName;
		return asset;
	}
	
	@FunctionalInterface
	public interface AssetCallback<T> {
		void onLoad(T asset);
	}
}
