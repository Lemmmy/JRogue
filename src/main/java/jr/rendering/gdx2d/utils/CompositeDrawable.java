package jr.rendering.gdx2d.utils;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class CompositeDrawable extends BaseDrawable {
	private final Drawable[] drawables;
	private float width, height;
	
	public CompositeDrawable(float width, float height, Drawable... drawables) {
		this.drawables = drawables;
		this.width = width;
		this.height = height;
	}
	
	@Override
	public void draw(Batch batch, float x, float y, float width, float height) {
		if (drawables != null) {
			for (Drawable d : drawables) {
				d.draw(batch, x, y, width, height);
			}
		}
	}
	
	@Override
	public float getMinWidth() {
		return width;
	}
	
	@Override
	public float getMinHeight() {
		return height;
	}
}
