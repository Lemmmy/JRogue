package jr.rendering.gdxvox.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import jr.ErrorHandler;
import jr.JRogue;
import jr.Settings;
import jr.dungeon.Dungeon;
import jr.dungeon.events.EventListener;
import jr.rendering.base.components.FPSCounterComponent;
import jr.rendering.base.components.hud.HUDComponent;
import jr.rendering.base.screens.ComponentedScreen;
import jr.rendering.gdx2d.GameAdapter;
import jr.rendering.gdx2d.GameInputProcessor;
import jr.rendering.gdxvox.components.RendererStatsComponent;
import jr.rendering.gdxvox.components.SceneComponent;
import jr.rendering.gdxvox.context.SceneContext;
import jr.rendering.gdxvox.objects.entities.EntityRendererMap;
import jr.rendering.gdxvox.objects.tiles.TileRendererMap;
import jr.rendering.gdxvox.primitives.FullscreenQuad;
import jr.rendering.gdxvox.primitives.VoxelCube;
import jr.rendering.utils.FontLoader;
import jr.rendering.utils.ShaderLoader;
import jr.utils.Point;
import lombok.Getter;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GLUtil;

import java.awt.*;
import java.lang.reflect.Field;

public class VoxGameScreen extends ComponentedScreen {
	/**
	 * The {@link GameAdapter} instance.
	 */
	private GameAdapter game;
	
	@Getter private SceneContext sceneContext;
	
	@Getter private OrthographicCamera screenCamera;
	
	public VoxGameScreen(GameAdapter game, Dungeon dungeon) {
		super(dungeon);
		
		this.game = game;
		
		JRogue.INSTANCE.setDungeon(dungeon);
		
		initialise();
		
		dungeon.start();
	}
	
	@Override
	public void preInitialiseComponents() {
		super.preInitialiseComponents();
		
		sceneContext = new SceneContext(dungeon);
		screenCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}
	
	private void initialise() {
		updateWindowTitle();
	}
	
	@Override
	public void initialiseComponents() {
		addComponent(10, "scene", new SceneComponent(this, sceneContext));
		addComponent(100, "hud", HUDComponent.class);
		addComponent(150, "fps", FPSCounterComponent.class);
		addComponent(200, "stats", RendererStatsComponent.class);
	}
	
	@Override
	public void show() {
		super.show();
		
		clearInputProcessors();
		// addInputProcessor(new GameInputProcessor(dungeon, this));
		// addInputProcessor(getComponent(SceneComponent.class, "scene").getCameraController());
		// addInputProcessor(getComponent(HUDComponent.class, "hud").getStage());
	}
	
	private void updateWindowTitle() {
		Gdx.graphics.setTitle(String.format(
			"%s - %s - Turn %,d",
			GameAdapter.WINDOW_TITLE,
			dungeon.getName(),
			dungeon.turnSystem.getTurn()
		));
	}
	
	@Override
	public void render(float delta) {
		ErrorHandler.glErrorCheck("before VoxGameScreen.super.render");
		super.render(delta);
		ErrorHandler.glErrorCheck("after VoxGameScreen.super.render");
		
		screenCamera.update();
		ErrorHandler.glErrorCheck("before VoxGameScreen.updateRendererComponents");
		updateRendererComponents(delta);
		
		ErrorHandler.glErrorCheck("before VoxGameScreen.renderMainBatchComponents");
		renderMainBatchComponents(delta);
		ErrorHandler.glErrorCheck("before VoxGameScreen.renderOtherBatchComponents");
		renderOtherBatchComponents(delta);
		
		ErrorHandler.glErrorCheck("after VoxGameScreen.render");
	}
	
	@Override
	public Point unprojectWorldPos(float screenX, float screenY) {
		return null;
	}
	
	@Override
	public Vector3 projectWorldPos(float worldX, float worldY) {
		return null;
	}
	
	@Override
	public void resize(int width, int height) {
		// super.resize(width, height);
		screenCamera.setToOrtho(false, width, height);
	}
	
	@Override
	public void dispose() {
		super.dispose();
		VoxelCube.dispose();
	}
}
