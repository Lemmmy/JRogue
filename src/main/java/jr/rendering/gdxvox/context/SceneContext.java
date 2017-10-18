package jr.rendering.gdxvox.context;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import jr.dungeon.Dungeon;
import jr.dungeon.entities.player.Player;
import jr.rendering.gdxvox.objects.entities.EntityRendererMap;
import jr.rendering.gdxvox.objects.tiles.TileRendererMap;

public class SceneContext extends Context {
	private static final float VIEWPORT_SIZE = 20;
	public static final int CAMERA_Y = 4;
	public static final int CAMERA_OFFSET_Z = 5;
	
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
		tileRendererMap = new TileRendererMap(this);
		tileRendererMap.initialise();
		dungeon.eventSystem.addListener(tileRendererMap);
		
		entityRendererMap = new EntityRendererMap(this);
		entityRendererMap.initialise();
		dungeon.eventSystem.addListener(entityRendererMap);
	}
	
	public void updateCamera() {
		if (getDungeon().getPlayer() != null) {
			Player p = getDungeon().getPlayer();
			
			sceneCamera.position.set(p.getX(), CAMERA_Y, p.getY() + CAMERA_OFFSET_Z);
			sceneCamera.lookAt(p.getX(), 0.5f, p.getY());
			sceneCamera.direction.x = 0f; // sometimes the camera likes to wobble out of its angle
		}
		
		sceneCamera.near = 0.1f;
		sceneCamera.update();
	}
	
	public void update() {
		updateCamera();
		lightContext.update();
	}
	
	public void resize(int width, int height) {
		gBuffersContext.resize(width, height);
	}
	
	public void renderAllMaps() {
		System.out.println("[PRINT DEBUGGING] before renderAllMaps");
		
		tileRendererMap.renderAll(sceneCamera);
		entityRendererMap.renderAll(sceneCamera);
		
		System.out.println("[PRINT DEBUGGING] after renderAllMaps");
	}
}
