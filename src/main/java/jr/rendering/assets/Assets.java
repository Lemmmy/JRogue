package jr.rendering.assets;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.Disposable;
import jr.ErrorHandler;
import jr.JRogue;
import lombok.val;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class Assets implements Disposable {
	public final AssetManager manager = new AssetManager();
	
	public final Textures textures = new Textures(this);
	public final Particles particles = new Particles(this);
	public final Shaders shaders = new Shaders(this);
	
	private List<Class<?>> managers;
	public void load() {
		findManagers();
		
		managers.forEach(manager -> {
			quietInvokeStatic(manager, "loadAssets", this);
			getAssetsFromManager(manager).forEach(a -> a.onLoad(this));
		});
	}
	
	private void loaded() {
		textures.onLoaded();
		particles.onLoaded();
		shaders.onLoaded();
		
		managers.forEach(manager -> {
			quietInvokeStatic(manager, "afterAssetsLoaded", this);
			getAssetsFromManager(manager).forEach(a -> a.onLoaded(this));
		});
		
		textures.afterLoaded();
		particles.afterLoaded();
		shaders.afterLoaded();
	}
	
	public void syncLoad() {
		manager.finishLoading();
		loaded();
	}
	
	private void findManagers() {
		managers = new ArrayList<>(JRogue.getReflections().getTypesAnnotatedWith(RegisterAssetManager.class));
	}
	
	private Collection<UsesAssets> getAssetsFromManager(Class<?> manager) {
		val assets = (Collection<UsesAssets>) quietInvokeStatic(manager, "getAssets");
		return assets != null ? assets : Collections.emptyList();
	}
	
	private Object quietInvokeStatic(Class<?> clazz, String methodName, Object... args) {
		try {
			Method method = MethodUtils.getAccessibleMethod(
				clazz, methodName,
				Arrays.stream(args).map(Object::getClass).toArray(Class[]::new)
			);
			
			if (method != null)
				return method.invoke(null, args);
		} catch (IllegalAccessException | InvocationTargetException e) {
			ErrorHandler.error("Error loading assets from " + clazz.getName(), e);
		}
		
		return null;
	}
	
	@Override
	public void dispose() {
		manager.dispose();
	}
}
