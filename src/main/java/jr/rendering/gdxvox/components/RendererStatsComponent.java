package jr.rendering.gdxvox.components;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import jr.rendering.base.components.RendererComponent;
import jr.rendering.gdxvox.context.SceneContext;
import jr.rendering.gdxvox.objects.AbstractObjectRendererManager;
import jr.rendering.gdxvox.objects.entities.EntityRendererManager;
import jr.rendering.gdxvox.objects.tiles.TileRendererManager;
import jr.rendering.gdxvox.screens.VoxGameScreen;
import jr.rendering.utils.FontLoader;

import java.util.Arrays;

public class RendererStatsComponent extends RendererComponent<VoxGameScreen> {
	private SpriteBatch batch;
	private BitmapFont font;
	
	private SceneContext sceneContext;
	
	private TileRendererManager tileRendererManager;
	private EntityRendererManager entityRendererManager;
	
	private Camera camera;
	
	public RendererStatsComponent(VoxGameScreen voxGameScreen) {
		super(voxGameScreen);
	}
	
	@Override
	public void initialise() {
		sceneContext = renderer.getSceneContext();
		tileRendererManager = sceneContext.tileRendererManager;
		entityRendererManager = sceneContext.entityRendererManager;
		camera = sceneContext.sceneCamera;
		
		batch = new SpriteBatch();
		font = FontLoader.getFont("fonts/Lato-Regular.ttf", 12, true, false);
	}
	
	@Override
	public void render(float dt) {
		batch.begin();
		
		font.draw(batch, String.format(
			"Tile voxels: %s\n" +
			"Entity voxels: %s\n" +
			"Total voxels: %s\n" +
			"Lights: %,d    Camera pos: %f %f %f",
			getVoxelStats(tileRendererManager),
			getVoxelStats(entityRendererManager),
			getVoxelStats(tileRendererManager, entityRendererManager),
			sceneContext.lightContext.getLights().size(),
			camera.position.x, camera.position.y, camera.position.z
		), 16, 80);
		
		batch.end();
	}
	
	public String getVoxelStats(AbstractObjectRendererManager... managers) {
		return getVoxelStats(
			Arrays.stream(managers).mapToInt(AbstractObjectRendererManager::getStaticVoxelCount).sum(),
			Arrays.stream(managers).mapToInt(AbstractObjectRendererManager::getStaticVisibleVoxelCount).sum(),
			Arrays.stream(managers).mapToInt(AbstractObjectRendererManager::getDynamicVoxelCount).sum(),
			Arrays.stream(managers).mapToInt(AbstractObjectRendererManager::getBatchCount).sum(),
			Arrays.stream(managers).mapToInt(AbstractObjectRendererManager::getVisibleBatchCount).sum()
		);
	}
	
	public String getVoxelStats(int staticVoxels, int staticVisibleVoxels, int dynamicVoxels, int batches, int visibleBatches) {
		return String.format(
			"sta: [P_GREEN_3]%,d[]/[P_GREEN_2]%,d[]  " +
			"dyn: [P_ORANGE_3]%,d[]  " +
			"tot: [P_ORANGE_1]%,d[]/[P_RED]%,d[]  " +
			"[P_BLUE_2]%,d[]/[P_BLUE_1]%,d[] batches",
			staticVisibleVoxels, staticVoxels,
			dynamicVoxels,
			staticVisibleVoxels + dynamicVoxels, staticVoxels + dynamicVoxels,
			visibleBatches, batches
		);
	}
	
	@Override
	public void update(float dt) {
	
	}
	
	@Override
	public void resize(int width, int height) {
		batch.dispose();
		batch = new SpriteBatch();
	}
	
	@Override
	public void dispose() {
		batch.dispose();
	}
}
