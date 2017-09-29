package jr.rendering.gdxvox.objects;

import com.badlogic.gdx.graphics.g3d.ModelBatch;
import jr.ErrorHandler;
import jr.JRogue;
import jr.dungeon.Level;
import jr.dungeon.events.EventHandler;
import jr.dungeon.events.EventListener;
import jr.dungeon.events.LevelChangeEvent;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractObjectRendererMap<ObjectK, ObjectV, RendererT extends AbstractObjectRenderer> implements EventListener {
	protected Map<ObjectK, RendererT> objectRendererMap = new HashMap<>();
	
	public void initialise() {
		findLists();
	}
	
	private void findLists() {
		JRogue.getReflections().getMethodsAnnotatedWith(getListAnnotationClass())
			.forEach(listMethod -> {
				listMethod.setAccessible(true);
				
				if (!listMethod.getParameterTypes()[0].isAssignableFrom(getClass())) {
					throw new RuntimeException("List method " + listMethod + " has wrong parameter types");
				}
				
				try {
					listMethod.invoke(null, this);
				} catch (IllegalAccessException | InvocationTargetException e) {
					ErrorHandler.error("Error finding renderer lists", e);
				}
			});
	}
	
	@SuppressWarnings("unchecked")
	public void addRenderers(RendererT renderer, ObjectK... types) {
		for (ObjectK type : types) {
			renderer.objectKeys.add(type);
			objectRendererMap.put(type, renderer);
			
			JRogue.getLogger().info("Added renderer for {}", type.toString());
		}
	}
	
	public void renderAll(ModelBatch batch) {
		objectRendererMap.values().forEach(renderer -> renderer.renderAll(batch));
	}
	
	@EventHandler
	protected void onLevelChange(LevelChangeEvent e) {
		objectRendererMap.values().forEach(renderer -> renderer.objectInstanceMap.clear());
		findObjects(e.getLevel());
	}
	
	public abstract void findObjects(Level level);
	
	public abstract Class<? extends ObjectK> getObjectKeyClass();
	public abstract Class<? extends ObjectV> getObjectValueClass();
	public abstract Class<? extends Annotation> getListAnnotationClass();
}
