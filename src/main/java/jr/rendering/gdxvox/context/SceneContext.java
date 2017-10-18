package jr.rendering.gdxvox.context;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import jr.dungeon.Dungeon;
import jr.rendering.gdxvox.objects.entities.EntityRendererMap;
import jr.rendering.gdxvox.objects.tiles.TileRendererMap;

public class SceneContext extends Context {
	public final LightContext lightContext;
	public final GBuffersContext gBuffersContext;
	
	public final PerspectiveCamera sceneCamera;
	
	public final TileRendererMap tileRendererMap;
	public final EntityRendererMap entityRendererMap;
	
	public SceneContext(Dungeon dungeon) {
		super(dungeon);
		
		lightContext = new LightContext(dungeon);
		gBuffersContext = new GBuffersContext(dungeon);
		
		sceneCamera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		updateCameraViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		sceneCamera.lookAt(20, 1, 20);
		
		tileRendererMap = new TileRendererMap(this);
		tileRendererMap.initialise();
		dungeon.eventSystem.addListener(tileRendererMap);
		
		entityRendererMap = new EntityRendererMap(this);
		entityRendererMap.initialise();
		dungeon.eventSystem.addListener(entityRendererMap);
	}
	
	public void updateCameraViewport(float width, float height) {
		float aspectRatio = width / height;
		// sceneCamera.setToOrtho(false, VIEWPORT_SIZE * aspectRatio, VIEWPORT_SIZE);
	}
	
	public void updateCamera() {
		// sceneCamera.position.set(20f, 20f, 20f);
		// sceneCamera.direction.set(-0.69631064f, -0.5f, -0.69631064f);
		// sceneCamera.lookAt(0, 0, 0);
		// sceneCamera.zoom = 1f;
		sceneCamera.near = 0.1f;
		sceneCamera.update();
	}
	
	public void update() {
		updateCameraViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		updateCamera();
		lightContext.update();
	}
	
	public void resize(int width, int height) {
		gBuffersContext.resize(width, height);
	}
	
	public void renderAllMaps() {
		tileRendererMap.renderAll(sceneCamera);
		entityRendererMap.renderAll(sceneCamera);
	}
}
