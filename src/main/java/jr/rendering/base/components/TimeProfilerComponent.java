package jr.rendering.base.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import jr.rendering.base.screens.ComponentedScreen;
import jr.rendering.utils.FontLoader;
import jr.rendering.utils.TimeProfiler;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeProfilerComponent extends RendererComponent {
	private static final Color BACKGROUND = new Color(0x00000066);
	
	private static final float GRAPH_ALPHA = 0.85f;
	
	private static final int MAX_ITEMS = 15;
	private static final float KEY_X = 256;
	private static final float VALUE_X = 52;
	private static final float TEXT_OFFSET_Y = 2;
	private static final float START_Y = 64;
	
	private static final Pattern COLOUR_PATTERN = Pattern.compile("^\\[(\\w+?)]");
	
	private Map<String, StreamingGraph> graphs = new HashMap<>();
	
	private ShapeRenderer shapeBatch;
	private SpriteBatch spriteBatch;
	
	private OrthographicCamera screenCamera;
	
	private BitmapFont font;
	private float yIncrement;
	
	public TimeProfilerComponent(ComponentedScreen componentedScreen) {
		super(componentedScreen);
	}
	
	@Override
	public void initialise() {
		shapeBatch = new ShapeRenderer();
		spriteBatch = new SpriteBatch();
		
		screenCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		screenCamera.setToOrtho(true, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		font = FontLoader.getFont("fonts/Lato-Regular.ttf", 11, false, true);
		yIncrement = font.getLineHeight();
	}
	
	@Override
	public void render(float dt) {
		screenCamera.update();
		shapeBatch.setProjectionMatrix(screenCamera.combined);
		spriteBatch.setProjectionMatrix(screenCamera.combined);
		
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		
		shapeBatch.begin(ShapeRenderer.ShapeType.Filled);
		drawBackground();
		shapeBatch.end();
		
		drawTimes();
		
		Gdx.gl.glDisable(GL20.GL_BLEND);
		
		TimeProfiler.reset();
	}
	
	private void drawBackground() {
		shapeBatch.setColor(BACKGROUND);
		shapeBatch.rect(
			Gdx.graphics.getWidth() - KEY_X,
			START_Y,
			KEY_X,
			Math.min(TimeProfiler.getTimes().size(), MAX_ITEMS) * (yIncrement * 2)
		);
	}
	
	private void drawTimes() {
		float width = Gdx.graphics.getWidth();
		AtomicReference<Float> y = new AtomicReference<>(START_Y);
		
		TimeProfiler.getTimes().entrySet().stream()
			.sorted(Comparator.comparing(Map.Entry::getValue))
			.limit(MAX_ITEMS)
			.forEach(timeEntry -> {
				String key = timeEntry.getKey();
				float value = timeEntry.getValue();
				List<Long> data = TimeProfiler.getTimeHistory(key);
				float peak = data == null ? 0.0f : Collections.max(data).floatValue();;
				
				float yf = y.get();
				
				StreamingGraph graph = null;
				
				if (!graphs.containsKey(key)) {
					Color colour = Color.WHITE;
					Matcher colourMatcher = COLOUR_PATTERN.matcher(key);
					
					if (colourMatcher.find()) colour = Colors.get(colourMatcher.group(1));
					
					colour = new Color(colour);
					colour.mul(1f, 1f, 1f, GRAPH_ALPHA);
					
					if (data != null) {
						graph = new StreamingGraph(
							screenCamera,
							Color.CLEAR,
							colour,
							TimeProfiler.TIME_HISTORY_COUNT,
							(int) yIncrement,
							data
						);
						
						graphs.put(key, graph);
					}
				} else {
					graph = graphs.get(key);
				}
				
				if (graph != null) graph.render(width - KEY_X, yf + yIncrement);
				
				spriteBatch.begin();
				font.draw( // name
					spriteBatch,
					key,
					width - KEY_X, yf + TEXT_OFFSET_Y
				);
				font.draw( // current time
					spriteBatch,
					String.format("%,.2fms", value / 1E6),
					width - VALUE_X, yf + TEXT_OFFSET_Y
				);
				font.draw( // peak time
					spriteBatch,
					String.format("[P_ORANGE_2]%,.2fms[]", peak / 1E6),
					width - VALUE_X, yf + yIncrement + TEXT_OFFSET_Y
				);
				spriteBatch.end();
				
				y.set(yf + yIncrement * 2);
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
		graphs.values().forEach(StreamingGraph::dispose);
	}
}
