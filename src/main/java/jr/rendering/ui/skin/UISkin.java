package jr.rendering.ui.skin;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TooltipManager;
import jr.ErrorHandler;
import jr.JRogue;
import jr.rendering.assets.RegisterAssetManager;
import jr.rendering.assets.UsesAssets;
import org.apache.commons.lang3.reflect.ConstructorUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@RegisterAssetManager
public class UISkin extends Skin {
	private static UISkin INSTANCE;
	
	private List<UISkinStyle> handlers = new ArrayList<>();
	
	private UISkin() {
		JRogue.getReflections().getTypesAnnotatedWith(UISkinStyleHandler.class).stream()
			.filter(UISkinStyle.class::isAssignableFrom)
			.sorted(Comparator.comparingInt(handlerClass -> {
				UISkinStyleHandler annotation = handlerClass.getAnnotation(UISkinStyleHandler.class);
				return -annotation.priority();
			}))
			.forEach(handlerClass -> loadHandler((Class<? extends UISkinStyle>) handlerClass));
		
		initialiseTooltips();
	}
	
	private void loadHandler(Class<? extends UISkinStyle> handlerClass) {
		try {
			Constructor<? extends UISkinStyle> ctor = ConstructorUtils.getAccessibleConstructor(handlerClass, UISkin.class);
			UISkinStyle handlerInstance = ctor.newInstance(this);
			handlers.add(handlerInstance);
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
			ErrorHandler.error("Unable to initialise ui skin", e);
		}
	}
	
	private void initialiseTooltips() {
		TooltipManager.getInstance().instant();
	}
	
	public static UISkin getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new UISkin();
		}
		
		return INSTANCE;
	}
	
	public static Collection<? extends UsesAssets> getAssets() {
		return getInstance().handlers;
	}
}
