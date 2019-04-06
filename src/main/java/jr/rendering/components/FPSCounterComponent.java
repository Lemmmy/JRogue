package jr.rendering.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import jr.Settings;
import jr.dungeon.Dungeon;
import jr.rendering.assets.Assets;
import jr.rendering.assets.RegisterAssetManager;
import jr.rendering.screens.GameScreen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static jr.rendering.assets.Fonts.fontFile;

public class FPSCounterComponent extends RendererComponent {
	private static final int GRAPH_WIDTH = 150,
							 GRAPH_HEIGHT = 32;
	
	private static final Color GRAPH_BACKGROUND = new Color(0x00000099);
	private static final Color GRAPH_FOREGROUND = new Color(0x8866eecc);
	
	private List<Float> frameTimeHistory = new ArrayList<>();
	
	private ShapeRenderer shapeBatch;
	private SpriteBatch spriteBatch;
	
	private OrthographicCamera counterCamera;
	
	public FPSCounterComponent(GameScreen renderer, Dungeon dungeon, Settings settings) {
		super(renderer, dungeon, settings);
	}
	
	@Override
	public void initialise() {
		shapeBatch = new ShapeRenderer();
		spriteBatch = new SpriteBatch();
		
		counterCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}
	
	@Override
	public void render(float dt) {
		counterCamera.update();
		
		shapeBatch.setProjectionMatrix(counterCamera.combined);
		spriteBatch.setProjectionMatrix(counterCamera.combined);
		
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		
		shapeBatch.begin(ShapeRenderer.ShapeType.Filled);
		drawGraph();
		shapeBatch.end();
		
		spriteBatch.begin();
		drawText();
		spriteBatch.end();
		
		Gdx.gl.glDisable(GL20.GL_BLEND);
	}
	
	private void drawGraph() {
		int x = Gdx.graphics.getWidth() - GRAPH_WIDTH;
		int y = 16;
		
		shapeBatch.setColor(GRAPH_BACKGROUND);
		shapeBatch.rect(x, y, GRAPH_WIDTH, GRAPH_HEIGHT);
		
		shapeBatch.setColor(GRAPH_FOREGROUND);
		
		float peakTime = Collections.max(frameTimeHistory);
		float scale = GRAPH_HEIGHT / peakTime;
		
		for (int i = 0; i < frameTimeHistory.size(); i++) {
			float barHeight = frameTimeHistory.get(i) * scale;
			
			shapeBatch.rect(x + i, y, 1, barHeight);
		}
	}
	
	private void drawText() {
		int x = Gdx.graphics.getWidth() - GRAPH_WIDTH;
		int y = GRAPH_HEIGHT + 16;
				
		float peakTime = Collections.max(frameTimeHistory) * 1000;
		
		Font.font.draw(
			spriteBatch,
			String.format("current: %,dfps\npeak: %.2fms", Gdx.graphics.getFramesPerSecond(), peakTime),
			x,
			y
		);
	}
	
	@Override
	public void update(float dt) {
		if (frameTimeHistory.size() >= GRAPH_WIDTH) {
			frameTimeHistory.remove(0);
		}
		
		frameTimeHistory.add(dt);
	}
	
	@Override
	public void resize(int width, int height) {
		counterCamera.setToOrtho(false, width, height);
	}
	
	@Override
	public int getZIndex() {
		return 130;
	}
	
	@Override
	public void dispose() {
		
	}
	
	@RegisterAssetManager
	public static class Font {
		private static BitmapFont font;
		
		public static void loadAssets(Assets assets) {
			assets.fonts.load(fontFile("Lato-Regular"), 11, false, f -> font = f);
		}
	}
}
