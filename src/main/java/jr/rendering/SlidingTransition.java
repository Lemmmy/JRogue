package jr.rendering;

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
		float x = 0f;
		float y = 0f;
		
		if (interpolation != null) percent = interpolation.apply(percent);
		
		switch (direction) {
			case LEFT:
				x = -width * percent;
				if (!slideOut) x += width;
				break;
			case RIGHT:
				x = width * percent;
				if (!slideOut) x -= width;
				break;
			case UP:
				y = height * percent;
				if (!slideOut) y -= height;
				break;
			case DOWN:
				y = -height * percent;
				if (!slideOut) y += height;
				break;
		}
		
		Texture texBottom = slideOut ? newTex : oldTex;
		Texture texTop = slideOut ? oldTex : newTex;
		
		batch.begin();
		batch.draw(
			texBottom,
			0, 0,
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
			x, y,
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
