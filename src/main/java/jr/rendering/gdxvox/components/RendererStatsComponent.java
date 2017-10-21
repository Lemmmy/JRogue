package jr.rendering.gdxvox.components;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import jr.ErrorHandler;
import jr.rendering.base.components.RendererComponent;
import jr.rendering.gdxvox.context.SceneContext;
import jr.rendering.gdxvox.objects.entities.EntityRendererMap;
import jr.rendering.gdxvox.objects.tiles.TileRendererMap;
import jr.rendering.gdxvox.screens.VoxGameScreen;
import jr.rendering.utils.FontLoader;

public class RendererStatsComponent extends RendererComponent<VoxGameScreen> {
	private SpriteBatch batch;
	private BitmapFont font;
	
	private SceneContext sceneContext;
	
	private TileRendererMap tileRendererMap;
	private EntityRendererMap entityRendererMap;
	
	private Camera camera;
	
	public RendererStatsComponent(VoxGameScreen voxGameScreen) {
		super(voxGameScreen);
	}
	
	@Override
	public void initialise() {
		sceneContext = renderer.getSceneContext();
		tileRendererMap = sceneContext.tileRendererMap;
		entityRendererMap = sceneContext.entityRendererMap;
		camera = sceneContext.sceneCamera;
		
		batch = new SpriteBatch();
		font = FontLoader.getFont("fonts/Lato-Regular.ttf", 12, true, false);
	}
	
	@Override
	public void render(float dt) {
		ErrorHandler.glErrorCheck("before RendererStatsComponent.render");
		
		batch.begin();
		
		ErrorHandler.glErrorCheck("after RendererStatsComponent.render.batch.begin()");
		
		int tileBatches = tileRendererMap.getObjectRendererMap().size();
		int tileVoxels = tileRendererMap.getVoxelCount();
		
		int entityBatches = entityRendererMap.getObjectRendererMap().size();
		int entityVoxels = entityRendererMap.getVoxelCount();
		
		ErrorHandler.glErrorCheck("before RendererStatsComponent.render.font.draw()");
		
		font.draw(batch, String.format(
			"Tile batches: %,d  Tile voxels: %,d  Entity batches: %,d  Entity voxels: %,d \n" +
				"Total batches: %,d  Total voxels: %,d  Lights: %,d\n" +
				"Camera pos: %f %f %f",
			tileBatches,
			tileVoxels,
			entityBatches,
			entityVoxels,
			tileBatches + entityBatches,
			tileVoxels + entityVoxels,
			sceneContext.lightContext.getLights().size(),
			camera.position.x, camera.position.y, camera.position.z
		), 16, 64);
		
		ErrorHandler.glErrorCheck("before RendererStatsComponent.render.batch.end()");
		
		batch.end();
		
		ErrorHandler.glErrorCheck("after RendererStatsComponent.render");
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
