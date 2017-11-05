package jr.rendering.base.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.Collections;
import java.util.List;

public class StreamingGraph<NumberT extends Number & Comparable<? super NumberT>> {
	private OrthographicCamera camera;
	private Color background, foreground;
	private int width, height;
	
	private List<NumberT> data;
	
	private ShapeRenderer shapeBatch;
	
	public StreamingGraph(OrthographicCamera camera,
						  Color background,
						  Color foreground,
						  int width,
						  int height, List<NumberT> data) {
		this.camera = camera;
		this.background = background;
		this.foreground = foreground;
		this.width = width;
		this.height = height;
		this.data = data;
		
		shapeBatch = new ShapeRenderer();
	}
	
	public void render(float x, float y) {
		shapeBatch.setProjectionMatrix(camera.combined);
		
		shapeBatch.begin(ShapeRenderer.ShapeType.Filled);
		drawGraph(x, y);
		shapeBatch.end();
	}
	
	private void drawGraph(float x, float y) {
		if (background.a != 0) {
			shapeBatch.setColor(background);
			shapeBatch.rect(x, y, width, height);
		}
		
		shapeBatch.setColor(foreground);
		
		NumberT peakTime = Collections.max(data);
		float scale = height / peakTime.floatValue();
		
		for (int i = 0; i < data.size(); i++) {
			float barHeight = data.get(i).floatValue() * scale;
			
			shapeBatch.rect(x + i, y + (height - barHeight), 1, barHeight);
		}
	}
	
	public void dispose() {
		shapeBatch.dispose();
	}
}
