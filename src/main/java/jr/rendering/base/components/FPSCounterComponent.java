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
	
	private SpriteBatch spriteBatch;
	private OrthographicCamera counterCamera;
	private StreamingGraph graph;
	private BitmapFont font;
	
	@SuppressWarnings("unchecked")
	public FPSCounterComponent(ComponentedScreen renderer) {
		super(renderer);
	}
	
	@Override
	public void initialise() {
		spriteBatch = new SpriteBatch();
		
		counterCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		font = FontLoader.getFont("fonts/Lato-Regular.ttf", 11, false, true);
		
		graph = new StreamingGraph(counterCamera, GRAPH_BACKGROUND, GRAPH_FOREGROUND, GRAPH_WIDTH, GRAPH_HEIGHT, frameTimeHistory);
	}
	
	@Override
	public void render(float dt) {
		counterCamera.update();
		
		spriteBatch.setProjectionMatrix(counterCamera.combined);
		
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		
		graph.render(Gdx.graphics.getWidth() - GRAPH_WIDTH, Gdx.graphics.getHeight() - GRAPH_HEIGHT - 16);
		
		spriteBatch.begin();
		drawText();
		spriteBatch.end();
		
		Gdx.gl.glDisable(GL20.GL_BLEND);
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
		spriteBatch.dispose();
		graph.dispose();
	}
}
