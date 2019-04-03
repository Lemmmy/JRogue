package jr.rendering.tiles.walls;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import jr.dungeon.Dungeon;
import jr.rendering.assets.UsesAssets;
import jr.rendering.tiles.TileRenderer;

import java.util.Random;

public abstract class WallDecoration implements UsesAssets {
	public void draw(TileRenderer tr, SpriteBatch batch, Dungeon dungeon, int x, int y, Random random) {}
	
	public void drawExtra(TileRenderer tr, SpriteBatch batch, Dungeon dungeon, int x, int y, Random rand) {}
}
