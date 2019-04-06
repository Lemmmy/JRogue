package jr.rendering.assets;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
/**
 * Registers this class as an asset manager.
 *
 * If the class defines a static getAssets() method that returns a
 * {@link Collection}&gt;? extends {@link UsesAssets}&lt;, then {@link Assets} will handle calling
 * {@link UsesAssets#onLoad(Assets)} and {@link UsesAssets#onLoaded(Assets)} for you.
 *
 * Alternatively, if the class defines a static loadAssets({@link Assets}) method, it will be called to allow the class
 * to load its assets. Additionally, it can define a static afterAssetsLoaded({@link Assets}) method to be called after
 * all assets have been loaded.
 */
public @interface RegisterAssetManager {

}
