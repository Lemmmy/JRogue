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
			wallH = getImageFromSheet("textures/tiles.png", 1, 0);
			wallV = getImageFromSheet("textures/tiles.png", 0, 0);
			wallCT = getImageFromSheet("textures/tiles.png", 2, 0);
			wallCB = getImageFromSheet("textures/tiles.png", 3, 0);
			
			mosses = new TextureRegion[3];
			
			mosses[0] = getImageFromSheet("textures/tiles.png", 1, 2);
			mosses[1] = getImageFromSheet("textures/tiles.png", 2, 2);
			mosses[2] = getImageFromSheet("textures/tiles.png", 3, 2);
		}
	}
	
	@Override
	public TextureRegion getTextureRegion(Dungeon dungeon, int x, int y) {
		TileType[] adjacentTiles = dungeon.getLevel().getTileStore().getAdjacentTileTypes(x, y);
		
		boolean h = adjacentTiles[0].isWall() || adjacentTiles[1].isWall();
		boolean v = adjacentTiles[2].isWall() || adjacentTiles[3].isWall();
		
		if (h && !v) {
			return wallH;
		} else if (!h && v) {
			return wallV;
		} else {
			return adjacentTiles[2].isWall() ? wallCT : wallCB;
		}
	}
	
	@Override
	public TextureRegion getTextureRegionExtra(Dungeon dungeon, int x, int y) {
		TileType[] adjacentTiles = dungeon.getLevel().getTileStore().getAdjacentTileTypes(x, y);
		
		boolean h = adjacentTiles[0].isWall() || adjacentTiles[1].isWall();
		boolean v = adjacentTiles[2].isWall() || adjacentTiles[3].isWall();
		boolean top = adjacentTiles[2].isInnerRoomTile();
		
		boolean shouldMoss = (x + y) % 3 == 0;
		
		if (h && !v && top && shouldMoss) {
			int mossNumber = (x + y) % mosses.length;
			return mosses[mossNumber];
		}
		
		return null;
	}
	
	@Override
	public void draw(SpriteBatch batch, Dungeon dungeon, int x, int y) {
		drawTile(batch, getTextureRegion(dungeon, x, y), x, y);
	}
	
	@Override
	public void drawExtra(SpriteBatch batch, Dungeon dungeon, int x, int y) {
		TextureRegion t = getTextureRegionExtra(dungeon, x, y);
		
		if (t != null) {
			drawTile(batch, t, x, y);
		}
	}
}
