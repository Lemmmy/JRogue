package jr.rendering.assets;

/**
 * Used by classes that depend on assets to be loaded.
 */
public interface UsesAssets {
	/**
	 * Called before assets are loaded, allowing the object to mark its own assets to be loaded.
	 */
	default void onLoad(Assets assets) {}
	
	/**
	 * Called after all assets have been loaded and are ready for use. This is also called <i>after</i> the
	 * {@link AssetHandler.AssetCallback AssetCallbacks} have been called.
	 */
	default void onLoaded(Assets assets) {}
	
	
	default void dispose() {}
}
