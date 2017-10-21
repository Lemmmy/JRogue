package jr.rendering.base.screens;

import com.badlogic.gdx.math.Vector3;
import jr.ErrorHandler;
import jr.JRogue;
import jr.Settings;
import jr.dungeon.Dungeon;
import jr.rendering.base.components.RendererComponent;
import jr.utils.Point;
import lombok.Getter;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public abstract class ComponentedScreen extends BasicScreen {
	@Getter protected Dungeon dungeon;
	@Getter protected Settings settings;
	
	/**
	 * The list of renderer components - components that get a chance to render to the screen at their specified
	 * Z-indexes.
	 */
	@Getter private Map<String, RendererComponent> rendererComponents = new HashMap<>();
	@Getter private List<RendererComponent> sortedComponents = new ArrayList<>();
	
	public ComponentedScreen(Dungeon dungeon) {
		this.dungeon = dungeon;
		this.settings = JRogue.getSettings();
		
		preInitialiseComponents();
		initialiseComponents();
		setupComponents();
	}
	
	public void preInitialiseComponents() {}
	public abstract void initialiseComponents();
	
	protected void setupComponents() {
		sortedComponents.sort(Comparator.comparingInt(RendererComponent::getZIndex));
		
		getRendererComponents().values().forEach(r -> dungeon.eventSystem.addListener(r));
		getRendererComponents().values().forEach(RendererComponent::initialise);
	}
	
	public void addComponent(int zIndex, String name, RendererComponent component) {
		component.setZIndex(zIndex);
		rendererComponents.put(name, component);
		sortedComponents.add(component);
	}
	
	public void addComponent(int zIndex, String name, Class<? extends RendererComponent> clazz) {
		try {
			Type genericSuperclass = clazz.getGenericSuperclass();
			Constructor componentConstructor;
			
			if (genericSuperclass instanceof ParameterizedType) {
				Class<? extends ComponentedScreen> componentGenericClass = (Class<? extends ComponentedScreen>)
					((ParameterizedType) genericSuperclass).getActualTypeArguments()[0];
				componentConstructor = clazz.getConstructor(componentGenericClass);
			} else {
				componentConstructor = clazz.getConstructor(ComponentedScreen.class);
			}
			
			RendererComponent component = (RendererComponent) componentConstructor.newInstance(this);
			addComponent(zIndex, name, component);
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			ErrorHandler.error(String.format(
				"Error adding component %s (%s)",
				name,
				clazz.getSimpleName()
			), e);
		}
	}
	
	public <ComponentT extends RendererComponent> ComponentT getComponent(Class<ComponentT> clazz, String name) {
		if (!rendererComponents.containsKey(name)) return null;
		
		RendererComponent c = rendererComponents.get(name);
		
		if (clazz.isInstance(c)) {
			return clazz.cast(c);
		} else {
			ErrorHandler.error(String.format(
				"Cannot cast component %s (%s) to %s",
				name,
				c.getClass().getSimpleName(),
				clazz.getSimpleName()
			), new RuntimeException());
			return null;
		}
	}
	
	public void updateRendererComponents(float dt) {
		sortedComponents.forEach(r -> r.update(dt));
	}
	
	public void renderMainBatchComponents(float dt) {
		sortedComponents.stream()
			.filter(RendererComponent::useMainBatch)
			.forEach(r -> r.render(dt));
	}
	
	public void renderOtherBatchComponents(float dt) {
		sortedComponents.stream()
			.filter(r -> !r.useMainBatch())
			.forEach(r -> r.render(dt));
	}
	
	public abstract Point unprojectWorldPos(float screenX, float screenY);
	public abstract Vector3 projectWorldPos(float worldX, float worldY);
	
	public boolean shouldAllowInput() {
		return true;
	}
	
	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		
		rendererComponents.values().forEach(r -> r.resize(width, height));
	}
	
	@Override
	public void dispose() {
		super.dispose();
		
		rendererComponents.values().forEach(RendererComponent::dispose);
	}
}
