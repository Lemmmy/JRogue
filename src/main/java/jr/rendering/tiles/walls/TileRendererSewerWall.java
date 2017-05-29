package jr.rendering.tiles.walls;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import jr.dungeon.Dungeon;
import jr.dungeon.tiles.TileType;
import jr.rendering.tiles.TileRenderer;

public class TileRendererSewerWall extends TileRendererWall {
	private static TextureRegion[] mosses;
	
	public TileRendererSewerWall() {
		mosses = new TextureRegion[3];
		
		for (int i = 0; i < mosses.length; i++) {
			mosses[i] = getImageFromSheet("textures/tiles.png", i + 1, 2);
		}
	}
	
	@Override
	public TextureRegion getTextureRegion(Dungeon dungeon, int x, int y) {
		return getImageFromMask(getPositionMask(dungeon.getLevel(), x, y));
	}
	
	@Override
	public TextureRegion getTextureRegionExtra(Dungeon dungeon, int x, int y) {
		TileType[] adjacentTiles = dungeon.getLevel().tileStore.getAdjacentTileTypes(x, y);
		
		boolean h = adjacentTiles[0].isWallTile() || adjacentTiles[1].isWallTile();
		boolean v = adjacentTiles[2].isWallTile() || adjacentTiles[3].isWallTile();
		boolean top = adjacentTiles[2].isInnerRoomTile();
		
		boolean shouldMoss = (x + y) % 3 == 0;
		
		if (h && !v && top && shouldMoss) {
			int mossNumber = (x + y) % mosses.length;
			return mosses[mossNumber];
		}
		
		return null;
	}
	
	@Override
	public void drawExtra(SpriteBatch batch, Dungeon dungeon, int x, int y) {
		TextureRegion t = getTextureRegionExtra(dungeon, x, y);
		
		if (t != null) {
			drawTile(batch, t, x, y);
		}
	}
}
