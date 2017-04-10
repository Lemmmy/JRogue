package jr.rendering.screens.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;

public class SlidingTransition implements ScreenTransition {
	public enum Direction {
		LEFT, RIGHT, UP, DOWN
	}
	
	private Direction direction;
	private boolean slideOut;
	private Interpolation interpolation;
	
	public SlidingTransition(Direction direction, boolean slideOut, Interpolation interpolation) {
		this.direction = direction;
		this.slideOut = slideOut;
		this.interpolation = interpolation;
	}
	
	@Override
	public void render(Batch batch, Texture oldTex, Texture newTex, float percent) {
		float width = oldTex.getWidth();
		float height = oldTex.getHeight();
		float x1 = 0f, y1 = 0f, x2 = 0f, y2 = 0f;
		
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		if (interpolation != null) percent = interpolation.apply(percent);
		
		switch (direction) {
			case LEFT:
				x2 = -width * percent;
				x1 = x2;
				if (!slideOut) x2 += width;
				break;
			case RIGHT:
				x2 = width * percent;
				x1 = x2;
				if (!slideOut) x2 -= width;
				break;
			case UP:
				y2 = height * percent;
				y1 = y2;
				if (!slideOut) y2 -= height;
				break;
			case DOWN:
				y2 = -height * percent;
				y1 = y2;
				if (!slideOut) y2 += height;
				break;
		}
		
		Texture texBottom = slideOut ? newTex : oldTex;
		Texture texTop = slideOut ? oldTex : newTex;
		
		batch.begin();
		batch.draw(
			texBottom,
			(int) x1, (int) y1,
			0, 0,
			width, height,
			1, 1,
			0,
			0, 0,
			(int) width,
			(int) height,
			false,true
		);
		batch.draw(
			texTop,
			(int) x2, (int) y2,
			0, 0,
			newTex.getWidth(), newTex.getHeight(),
			1, 1,
			0,
			0, 0,
			newTex.getWidth(), newTex.getHeight(),
			false,true
		);
		batch.end();
	}
}
