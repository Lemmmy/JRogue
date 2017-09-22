package jr.rendering.ui.skin;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TooltipManager;
import jr.ErrorHandler;
import jr.JRogue;

import java.util.Comparator;

public class UISkin extends Skin {
	private static UISkin INSTANCE;
	
	private UISkin() {
		JRogue.getReflections().getTypesAnnotatedWith(UISkinStyleHandler.class).stream()
			.filter(UISkinStyle.class::isAssignableFrom)
			.sorted(Comparator.comparingInt(handlerClass -> {
				UISkinStyleHandler annotation = handlerClass.getAnnotation(UISkinStyleHandler.class);
				
				return -annotation.priority();
			}))
			.forEach(handlerClass -> {
				UISkinStyleHandler annotation = handlerClass.getAnnotation(UISkinStyleHandler.class);
				
				try {
					UISkinStyle handlerInstance = (UISkinStyle) handlerClass.newInstance();
					handlerInstance.add(this);
				} catch (InstantiationException | IllegalAccessException e) {
					ErrorHandler.error("Unable to initialise ui skin", e);
				}
			});
		
		initialiseTooltips();
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
}
