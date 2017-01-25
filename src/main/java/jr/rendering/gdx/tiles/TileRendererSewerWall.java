package jr.rendering.gdx.tiles;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import jr.dungeon.Dungeon;
import jr.dungeon.tiles.TileType;

public class TileRendererSewerWall extends TileRenderer {
	private static TextureRegion wallH;
	private static TextureRegion wallV;
	private static TextureRegion wallCT;
	private static TextureRegion wallCB;
	
	private static TextureRegion[] mosses;
	
	public TileRendererSewerWall() {
		if (wallH == null || wallV == null || wallCT == null || wallCB == null) {
			wallH = getImageFromSheet("tiles.png", 1, 0);
			wallV = getImageFromSheet("tiles.png", 0, 0);
			wallCT = getImageFromSheet("tiles.png", 2, 0);
			wallCB = getImageFromSheet("tiles.png", 3, 0);
			
			mosses = new TextureRegion[3];
			
			mosses[0] = getImageFromSheet("tiles.png", 1, 2);
			mosses[1] = getImageFromSheet("tiles.png", 2, 2);
			mosses[2] = getImageFromSheet("tiles.png", 3, 2);
		}
	}
	
	@Override
	public void draw(SpriteBatch batch, Dungeon dungeon, int x, int y) {
		TileType[] adjacentTiles = dungeon.getLevel().tileStore.getAdjacentTileTypes(x, y);
		
		boolean h = adjacentTiles[0].isWallTile() || adjacentTiles[1].isWallTile();
		boolean v = adjacentTiles[2].isWallTile() || adjacentTiles[3].isWallTile();
		boolean top = adjacentTiles[2].isInnerRoomTile();
		
		boolean shouldMoss = (x + y) % 3 == 0;
		
		if (h && !v) {
			drawTile(batch, wallH, x, y);
			
			if (top && shouldMoss) {
				int mossNumber = (x + y) % mosses.length;
				drawTile(batch, mosses[mossNumber], x, y);
			}
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
