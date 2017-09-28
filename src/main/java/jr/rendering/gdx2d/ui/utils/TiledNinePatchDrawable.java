package jr.rendering.gdx2d.ui.utils;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;

public class TiledNinePatchDrawable extends BaseDrawable {
	private NinePatchDrawable npd;
	private TiledDrawable td;
	
	private int left, right, top, bottom, width, height;
	
	public TiledNinePatchDrawable (TextureRegion region, int left, int right, int top, int bottom) {
		if (region == null) throw new IllegalArgumentException("region cannot be null.");
		
		this.left = left;
		this.right = right;
		this.top = top;
		this.bottom = bottom;
		this.width = region.getRegionWidth();
		this.height = region.getRegionHeight();
		
		TextureRegion middle = region;
		
		// construct our own ninepatch without the middle
		
		final int middleWidth = region.getRegionWidth() - left - right;
		final int middleHeight = region.getRegionHeight() - top - bottom;
		
		TextureRegion[] patches = new TextureRegion[9];
		
		if (top > 0) {
			if (left > 0) patches[0] = new TextureRegion(region, 0, 0, left, top);
			if (middleWidth > 0) patches[1] = new TextureRegion(region, left, 0, middleWidth, top);
			if (right > 0) patches[2] = new TextureRegion(region, left + middleWidth, 0, right, top);
		}
		if (middleHeight > 0) {
			if (left > 0) patches[3] = new TextureRegion(region, 0, top, left, middleHeight);
			if (middleWidth > 0) patches[4] = null;
			middle = new TextureRegion(region, left, top, middleWidth, middleHeight);
			if (right > 0) patches[5] = new TextureRegion(region, left + middleWidth, top, right, middleHeight);
		}
		if (bottom > 0) {
			if (left > 0) patches[6] = new TextureRegion(region, 0, top + middleHeight, left, bottom);
			if (middleWidth > 0) patches[7] = new TextureRegion(region, left, top + middleHeight, middleWidth, bottom);
			if (right > 0) patches[8] = new TextureRegion(region, left + middleWidth, top + middleHeight, right, bottom);
		}
		
		npd = new NinePatchDrawable(new NinePatch(patches));
		td = new TiledDrawable(middle);
	}
	
	@Override
	public void draw(Batch batch, float x, float y, float width, float height) {
		super.draw(batch, x, y, width, height);
		
		npd.draw(batch, x, y, width, height);
		td.draw(
			batch,
			x + left,
			y + bottom,
			width - left - right,
			height - top - bottom
		);
	}
}
