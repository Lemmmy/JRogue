package jr.rendering.tiles.walls;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import jr.dungeon.tiles.Tile;
import jr.rendering.assets.UsesAssets;
import jr.rendering.tiles.TileRenderer;
import jr.utils.Point;

import java.util.Random;

public abstract class WallDecoration implements UsesAssets {
	public void draw(TileRenderer tr, SpriteBatch batch, Tile tile, Point p, Random random) {}
	
	public void drawExtra(TileRenderer tr, SpriteBatch batch, Tile tile, Point p, Random rand) {}
}
