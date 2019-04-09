package jr.rendering.tiles;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import jr.dungeon.tiles.Tile;
import jr.utils.Point;
import jr.utils.RandomUtils;

public class TileRendererNoise extends TileRendererGlobalRepeat {
    public TileRendererNoise(String fileName, float scaleX, float scaleY) {
        super(fileName, scaleX, scaleY);
    }

    @Override
    public void draw(SpriteBatch batch, Tile tile, Point p) {
        setOffsetX(RandomUtils.randomFloat() * 128.0f);
        setOffsetY(RandomUtils.randomFloat() * 128.0f);
        super.draw(batch, tile, p);
    }
}
