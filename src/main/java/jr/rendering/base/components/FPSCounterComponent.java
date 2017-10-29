package jr.rendering.base.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import jr.rendering.base.screens.ComponentedScreen;
import jr.rendering.utils.FontLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FPSCounterComponent extends RendererComponent {
	private static final int GRAPH_WIDTH = 150,
							 GRAPH_HEIGHT = 32;
	
	private static final Color GRAPH_BACKGROUND = new Color(0x00000099);
	private static final Color GRAPH_FOREGROUND = new Color(0x8866eecc);
	
	private List<Float> frameTimeHistory = new ArrayList<>();
	
	private ShapeRenderer shapeBatch;
	private SpriteBatch spriteBatch;
	
	private OrthographicCamera counterCamera;
	
	private BitmapFont font;
	
	@SuppressWarnings("unchecked")
	public FPSCounterComponent(ComponentedScreen renderer) {
		super(renderer);
	}
	
	@Override
	public void initialise() {
		shapeBatch = new ShapeRenderer();
		spriteBatch = new SpriteBatch();
		
		counterCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		font = FontLoader.getFont("fonts/Lato-Regular.ttf", 11, false, true);
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
		int y = Gdx.graphics.getHeight() - GRAPH_HEIGHT - 16;
		
		shapeBatch.setColor(GRAPH_BACKGROUND);
		shapeBatch.rect(x, y, GRAPH_WIDTH, GRAPH_HEIGHT);
		
		shapeBatch.setColor(GRAPH_FOREGROUND);
		
		float peakTime = Collections.max(frameTimeHistory);
		float scale = GRAPH_HEIGHT / peakTime;
		
		for (int i = 0; i < frameTimeHistory.size(); i++) {
			float barHeight = frameTimeHistory.get(i) * scale;
			
			shapeBatch.rect(x + i, y + (GRAPH_HEIGHT - barHeight), 1, barHeight);
		}
	}
	
	private void drawText() {
		int x = Gdx.graphics.getWidth() - GRAPH_WIDTH;
		int y = Gdx.graphics.getHeight() - GRAPH_HEIGHT - 16;
				
		float peakTime = Collections.max(frameTimeHistory) * 1000;
		
		font.draw(
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
		counterCamera.setToOrtho(true, width, height);
	}
	
	@Override
	public void dispose() {
		
	}
}
