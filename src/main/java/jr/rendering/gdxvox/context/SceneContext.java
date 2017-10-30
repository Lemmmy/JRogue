package jr.rendering.gdxvox.context;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;
import jr.dungeon.Dungeon;
import jr.dungeon.entities.player.Player;
import jr.rendering.gdxvox.objects.entities.EntityRendererManager;
import jr.rendering.gdxvox.objects.tiles.TileRendererManager;
import jr.rendering.utils.TimeProfiler;
import jr.utils.Utils;

public class SceneContext extends Context {
	private static final float VIEWPORT_SIZE = 20;
	
	public static final int CAMERA_Y = 5;
	public static final int CAMERA_OFFSET_BEHIND = 5;
	
	public static final float CAMERA_LERP_DURATION = 0.125f;
	public static final float CAMERA_LERP_BIAS = 0.01f;
	
	public final TileRendererManager tileRendererManager;
	public final EntityRendererManager entityRendererManager;
	
	public final LightContext lightContext;
	public final GBuffersContext gBuffersContext;
	
	public final PerspectiveCamera sceneCamera;
	
	public float cameraRotation = 0f;
	public float cameraStartRotation = 0f;
	public float cameraTargetRotation = 0f;
	public float cameraLerpElapsed = 0f;
	public boolean cameraLerping = false;
	
	public SceneContext(Dungeon dungeon) {
		super(dungeon);
		
		lightContext = new LightContext(dungeon);
		gBuffersContext = new GBuffersContext(dungeon);
		
		sceneCamera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		tileRendererManager = new TileRendererManager(this);
		tileRendererManager.initialise();
		dungeon.eventSystem.addListener(tileRendererManager);
		
		entityRendererManager = new EntityRendererManager(this);
		entityRendererManager.initialise();
		dungeon.eventSystem.addListener(entityRendererManager);
	}
	
	public void updateCamera(float delta) {
		TimeProfiler.begin("[P_BLUE_0]SceneContext.updateCamera[]");
		
		if (cameraLerping) {
			cameraLerpElapsed += delta;
			
			if (cameraLerpElapsed >= CAMERA_LERP_DURATION - CAMERA_LERP_BIAS) {
				cameraLerping = false;
				cameraRotation = cameraTargetRotation % 360;
			} else {
				cameraRotation = Utils.easeInOut(
					cameraLerpElapsed,
					cameraStartRotation,
					cameraTargetRotation - cameraStartRotation,
					CAMERA_LERP_DURATION
				);
			}
		}
		
		if (getDungeon().getPlayer() != null) {
			Player p = getDungeon().getPlayer();
			
			float dx = (float) (Math.sin(Math.toRadians(cameraRotation)) * CAMERA_OFFSET_BEHIND);
			float dy = (float) (Math.cos(Math.toRadians(cameraRotation)) * CAMERA_OFFSET_BEHIND);
			
			sceneCamera.position.set(p.getX() + dx, CAMERA_Y, p.getY() + dy);
			sceneCamera.lookAt(p.getX(), 0.5f, p.getY());
			sceneCamera.up.set(Vector3.Y);
			// sceneCamera.direction.x = 0f; // sometimes the camera likes to wobble out of its angle
		}
		
		sceneCamera.near = 0.1f;
		sceneCamera.update();
		
		TimeProfiler.end("[P_BLUE_0]SceneContext.updateCamera[]");
	}
	
	public void update(float delta) {
		updateCamera(delta);
		lightContext.update(delta);
	}
	
	public void resize(int width, int height) {
		gBuffersContext.resize(width, height);
	}
	
	public void renderAllMaps() {
		tileRendererManager.renderAll(sceneCamera);
		entityRendererManager.renderAll(sceneCamera);
	}
	
	public void rotateCamera(float degrees) {
		cameraLerping = true;
		cameraLerpElapsed = 0.0f;
		cameraStartRotation = cameraRotation;
		cameraTargetRotation = (cameraRotation % (360 + 90) + degrees) % (360 + 90);
	}
}
