package jr.rendering.tiles.walls;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import jr.dungeon.Dungeon;
import jr.dungeon.tiles.TileType;
import jr.rendering.assets.Assets;
import jr.rendering.utils.ImageUtils;

import static jr.rendering.assets.Textures.tileFile;

public class TileRendererSewerWall extends TileRendererWall {
	protected static final int SHEET_WIDTH = 3;
	protected static final int SHEET_HEIGHT = 1;
	
	private static TextureRegion[] mosses = new TextureRegion[SHEET_WIDTH * SHEET_HEIGHT];
	
	@Override
	public void onLoad(Assets assets) {
		super.onLoad(assets);
		
		assets.textures.loadPacked(tileFile("sewer_moss"), t -> ImageUtils.loadSheet(t, mosses, SHEET_WIDTH, SHEET_HEIGHT));
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
