package jr.rendering.gdx.tiles;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import jr.dungeon.Dungeon;
import jr.utils.RandomUtils;

public class TileRendererNoise extends TileRendererGlobalRepeat {
	public TileRendererNoise(Texture texture, float scaleX, float scaleY) {
		super(texture, scaleX, scaleY);
	}

	@Override
	public void draw(SpriteBatch batch, Dungeon dungeon, int x, int y) {
		setOffsetX(RandomUtils.randomFloat() * 128.0f);
		setOffsetY(RandomUtils.randomFloat() * 128.0f);
		super.draw(batch, dungeon, x, y);
	}
}
