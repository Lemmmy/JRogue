package pw.lemmmy.jrogue.rendering.gdx.tiles;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.TileType;

public class TileRendererWall extends TileRenderer {
	private TextureRegion wallH;
	private TextureRegion wallV;
	private TextureRegion wallCT;
	private TextureRegion wallCB;

	public TileRendererWall() {
		wallH = getImageFromSheet("tiles.png", 1, 0);
		wallV = getImageFromSheet("tiles.png", 0, 0);
		wallCT = getImageFromSheet("tiles.png", 2, 0);
		wallCB = getImageFromSheet("tiles.png", 3, 0);
	}

	@Override
	public void draw(SpriteBatch batch, Dungeon d, int x, int y) {
		TileType[] adjacentTiles = d.getLevel().getAdjacentTiles(x, y);

		boolean h = adjacentTiles[0] == TileType.TILE_ROOM_WALL || adjacentTiles[1] == TileType.TILE_ROOM_WALL;
		boolean v = adjacentTiles[2] == TileType.TILE_ROOM_WALL || adjacentTiles[3] == TileType.TILE_ROOM_WALL;

		if (h && !v) {
			drawTile(batch, wallH, x, y);
		} else if (!h && v) {
			drawTile(batch, wallV, x, y);
		} else {
			if (adjacentTiles[2] == TileType.TILE_ROOM_WALL) {
				drawTile(batch, wallCT, x, y);
			} else {
				drawTile(batch, wallCB, x, y);
			}
		}

		drawLight(batch, d, x, y);
	}
}
