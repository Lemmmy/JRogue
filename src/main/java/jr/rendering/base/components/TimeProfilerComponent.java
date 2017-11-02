package jr.rendering.base.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import jr.rendering.base.screens.ComponentedScreen;
import jr.rendering.utils.FontLoader;
import jr.rendering.utils.TimeProfiler;

import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class TimeProfilerComponent extends RendererComponent {
	private static final int MAX_ITEMS = 15;
	
	private static final float KEY_X = 256;
	private static final float VALUE_X = 96;
	
	private static final float START_Y = 64;
	
	private SpriteBatch spriteBatch;
	
	private OrthographicCamera screenCamera;
	
	private BitmapFont font;
	private float yIncrement;
	
	public TimeProfilerComponent(ComponentedScreen componentedScreen) {
		super(componentedScreen);
	}
	
	@Override
	public void initialise() {
		spriteBatch = new SpriteBatch();
		
		screenCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		screenCamera.setToOrtho(true, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		font = FontLoader.getFont("fonts/Lato-Regular.ttf", 11, false, true);
		yIncrement = font.getLineHeight();
	}
	
	@Override
	public void render(float dt) {
		screenCamera.update();
		
		spriteBatch.begin();
		spriteBatch.setProjectionMatrix(screenCamera.combined);
		drawTimes();
		spriteBatch.end();
		
		TimeProfiler.reset();
	}
	
	private void drawTimes() {
		float width = Gdx.graphics.getWidth();
		AtomicReference<Float> y = new AtomicReference<>(START_Y);
		
		TimeProfiler.getTimes().entrySet().stream()
			.sorted(Comparator.comparingLong(Map.Entry::getValue))
			.limit(MAX_ITEMS)
			.forEach(timeEntry -> {
				font.draw(spriteBatch, timeEntry.getKey(), width - KEY_X, y.get());
				font.draw(spriteBatch, String.format("%,.2f ms", timeEntry.getValue() / 1E6), width - VALUE_X, y.get());
				
				y.set(y.get() + yIncrement);
			});
	}
	
	@Override
	public void update(float dt) {
		
	}
	
	@Override
	public void resize(int width, int height) {
		screenCamera.setToOrtho(true, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}
	
	@Override
	public void dispose() {
		spriteBatch.dispose();
	}
}
