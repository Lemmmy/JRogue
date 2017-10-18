package jr.rendering.base.components;

import jr.Settings;
import jr.dungeon.Dungeon;
import jr.dungeon.events.EventListener;
import jr.rendering.base.screens.ComponentedScreen;
import lombok.Getter;
import lombok.Setter;

public abstract class RendererComponent<Renderer extends ComponentedScreen> implements EventListener {
	public Renderer renderer;
	public Dungeon dungeon;
	public Settings settings;
	
	@Getter @Setter private int zIndex;
	
	/**
	 * A component that gets a chance to render on the screen at the specified Z-index. Lower Z-indexes are drawn first.
	 *
	 * @param renderer The renderer that uses this component.
	 */
	public RendererComponent(Renderer renderer) {
		this.renderer = renderer;
		
		if (renderer != null) {
			this.dungeon = renderer.getDungeon();
			this.settings = renderer.getSettings();
		}
	}
	
	/**
	 * @return Whether or not this should be drawn in the main batch. The main batch is the batch the level is drawn on,
	 * in the viewport sceneCamera.
	 */
	public boolean useMainBatch() {
		return false;
	}
	
	/**
	 * Called when the renderer is being initialised - this is where batches, cameras etc. should be initialised. You
	 * should also load textures here.
	 */
	public abstract void initialise();
	
	/**
	 * Called every frame - this is when draw calls can be done.
	 *
	 * @param dt The time since the last frame in seconds. May be smoothed.
	 */
	public abstract void render(float dt);
	
	/**
	 * Called every frame before rendering.
	 *
	 * @param dt The time since the last frame in seconds. May be smoothed.
	 */
	public abstract void update(float dt);
	
	/**
	 * Called when the screen is resized.
	 *
	 * @param width The new screen width, in pixels.
	 * @param height The new screen height, in pixels.
	 */
	public abstract void resize(int width, int height);
	
	/**
	 * Called when the game is being exited and the resources should be cleaned up.
	 */
	public abstract void dispose();
}
