package jr.rendering.gdxvox.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import jr.JRogue;
import jr.Settings;
import jr.dungeon.Dungeon;
import jr.dungeon.events.EventListener;
import jr.rendering.gdx2d.GameAdapter;
import jr.rendering.gdx2d.screens.BasicScreen;
import jr.rendering.gdxvox.objects.tiles.TileRendererMap;

public class VoxGameScreen extends BasicScreen implements EventListener {
	/**
	 * The {@link GameAdapter} instance.
	 */
	private GameAdapter game;
	
	/**
	 * The {@link Dungeon} that this renderer should render.
	 */
	private Dungeon dungeon;
	
	/**
	 * The user's {@link Settings}.
	 */
	private Settings settings;
	
	private TileRendererMap tileRendererMap;
	private OrthographicCamera camera;
	private ModelBatch modelBatch;
	
	public VoxGameScreen(GameAdapter game, Dungeon dungeon) {
		this.game = game;
		this.dungeon = dungeon;
		this.dungeon.eventSystem.addListener(this);
		
		JRogue.INSTANCE.setDungeon(dungeon);
		
		settings = JRogue.getSettings();
		
		initialise();
		
		dungeon.start();
	}
	
	private void initialise() {
		tileRendererMap = new TileRendererMap();
		tileRendererMap.initialise();
		dungeon.eventSystem.addListener(tileRendererMap);
		
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		modelBatch = new ModelBatch();
	}
	
	@Override
	public void render(float delta) {
		super.render(delta);
		
		camera.position.set(1f, 1f, 1f);
		camera.lookAt(0, 0, 0);
		camera.zoom = 0.01f;
		camera.update();
		
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT |
			(Gdx.graphics.getBufferFormat().coverageSampling?GL20.GL_COVERAGE_BUFFER_BIT_NV:0));
		
		modelBatch.begin(camera);
		tileRendererMap.renderAll(modelBatch);
		modelBatch.end();
	}
	
	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		camera.setToOrtho(true, width, height);
	}
}
