package jr.rendering.assets;

import jr.JRogue;

import java.util.*;

/**
 * Used by classes that handles specific types of assets, e.g. {@link Textures}.
 *
 * When loading assets, the file names will be prefixed with {@link #getFileNamePrefix()} before loading them, unless
 * their file name already begins with a forward slash ({@code /}).
 *
 * @param <T> The type of asset that this class handles.
 */
public abstract class AssetHandler<T> {
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
	
	private String getPrefixedFileName(String fileName) {
		return fileName.startsWith("/") ? fileName : getFileNamePrefix() + fileName;
	}
	
	/**
	 * Called after all assets have been loaded and are ready for use.
	 */
	public void onLoaded() {
		callbacks.forEach((fileName, callback) -> {
			T asset = assets.manager.get(fileName);
			callback.forEach(c -> c.onLoad(asset));
		});
		
		callbacks.clear();
	}
	
	/**
	 * Queues an asset to be loaded.
	 *
	 * @param fileName The file name of the asset to load.
	 */
	public void load(String fileName) {
		JRogue.getLogger().debug("Loading asset {}", fileName);
		assets.manager.load(fileName, getAssetClass());
	}
	
	/**
	 * Queues an asset to be loaded, and registers a callback to be called when it has loaded.
	 *
	 * @param fileName The file name of the asset to load.
	 * @param callback The callback to be called when the asset has been loaded.
	 */
	public void load(String fileName, AssetCallback<T> callback) {
		load(fileName);
		
		if (assets.manager.isLoaded(fileName)) {
			callback.onLoad(assets.manager.get(fileName));
		} else {
			if (!callbacks.containsKey(fileName))
				callbacks.put(fileName, new HashSet<>());
			callbacks.get(fileName).add(callback);
		}
	}
	
	/**
	 * Immediately loads an asset. Useful for the loading screen.
	 *
	 * @param fileName The file name of the asset to load.
	 */
	public T loadImmediately(String fileName) {
		assets.manager.load(fileName, getAssetClass());
		assets.manager.finishLoadingAsset(fileName);
		return assets.manager.get(fileName);
	}
	
	@FunctionalInterface
	public interface AssetCallback<T> {
		void onLoad(T asset);
	}
}
