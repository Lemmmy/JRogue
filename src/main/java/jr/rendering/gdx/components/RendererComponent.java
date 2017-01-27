package jr.rendering.gdx.components;

import com.badlogic.gdx.graphics.OrthographicCamera;
import jr.Settings;
import jr.dungeon.Dungeon;
import jr.rendering.gdx.GDXRenderer;

public abstract class RendererComponent implements Dungeon.Listener {
	public GDXRenderer renderer;
	public Dungeon dungeon;
	public Settings settings;
	
	public OrthographicCamera camera;
	
	public RendererComponent(GDXRenderer renderer, Dungeon dungeon, Settings settings) {
		this.renderer = renderer;
		this.dungeon = dungeon;
		this.settings = settings;
	}
	
	public OrthographicCamera getCamera() {
		return camera;
	}
	
	public void setCamera(OrthographicCamera camera) {
		this.camera = camera;
	}
	
	public boolean useMainBatch() {
		return false;
	}
	
	public abstract void initialise();
	
	public abstract void render(float dt);
	
	public abstract void update(float dt);
	
	public abstract void resize(int width, int height);
	
	public abstract int getZIndex();
	
	public abstract void dispose();
}
