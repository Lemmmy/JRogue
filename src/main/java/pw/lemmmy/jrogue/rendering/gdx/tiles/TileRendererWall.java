package pw.lemmmy.jrogue.rendering.gdx.tiles;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.TileType;

public class TileRendererWall extends TileRenderer {
	private static TextureRegion wallH;
	private static TextureRegion wallV;
	private static TextureRegion wallCT;
	private static TextureRegion wallCB;

	public TileRendererWall() {
		if (wallH == null || wallV == null || wallCT == null || wallCB == null) {
			wallH = getImageFromSheet("tiles.png", 1, 0);
			wallV = getImageFromSheet("tiles.png", 0, 0);
			wallCT = getImageFromSheet("tiles.png", 2, 0);
			wallCB = getImageFromSheet("tiles.png", 3, 0);
		}
	}

	@Override
	public void draw(SpriteBatch batch, Dungeon dungeon, int x, int y) {
		TileType[] adjacentTiles = dungeon.getLevel().getAdjacentTiles(x, y);

		boolean h = adjacentTiles[0].isWallTile() || adjacentTiles[1].isWallTile();
		boolean v = adjacentTiles[2].isWallTile() || adjacentTiles[3].isWallTile();

		if (h && !v) {
			drawTile(batch, wallH, x, y);
		} else if (!h && v) {
			drawTile(batch, wallV, x, y);
		} else {
			if (adjacentTiles[2].isWallTile()) {
				drawTile(batch, wallCT, x, y);
			} else {
				drawTile(batch, wallCB, x, y);
			}
		}
	}
}
