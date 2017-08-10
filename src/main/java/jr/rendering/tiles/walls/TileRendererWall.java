package jr.rendering.tiles.walls;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.tiles.TileType;
import jr.rendering.tiles.TileRenderer;
import jr.utils.WeightedCollection;

import java.util.Random;

public class TileRendererWall extends TileRenderer {
	protected static final int BLOB_SHEET_WIDTH = 4;
	protected static final int BLOB_SHEET_HEIGHT = 4;
	
	private static final int PROBABILITY_GRATE = 6;
	private static final int PROBABILITY_COBWEB = 22;
	
	protected final WeightedCollection<WallDecoration> wallDecoration = new WeightedCollection<>();
	
	{
		wallDecoration.add(100, new WallDecoration()); // no decoration
		wallDecoration.add(30, new WallDecorationCobweb());
		wallDecoration.add(10, new WallDecorationGrate());
	}
	
	private static TextureRegion[] images = new TextureRegion[BLOB_SHEET_WIDTH * BLOB_SHEET_HEIGHT];
	private static TextureRegion wallHPillar, wallHPillarExtra;
	
	private static final int[] MAP = new int[] {
		12, 8, 13, 9, 0, 4, 1, 5, 15, 11, 14, 10, 3, 7, 2, 6
	};
	
	private Random rand = new Random();
	
	public TileRendererWall() {
		loadBlob(images, 0, 3);
		wallHPillar = getImageFromSheet("textures/tiles.png", 11, 1);
		wallHPillarExtra = getImageFromSheet("textures/tiles.png", 12, 1);
	}
	
	protected void loadBlob(TextureRegion[] set, int blobStartX, int blobStartY) {
		for (int i = 0; i < BLOB_SHEET_WIDTH * BLOB_SHEET_HEIGHT; i++) {
			int sheetX = i % BLOB_SHEET_WIDTH + blobStartX;
			int sheetY = (int) Math.floor(i / BLOB_SHEET_WIDTH) + blobStartY;
			
			set[i] = getImageFromSheet("textures/tiles.png", sheetX, sheetY);
		}
	}
	
	@Override
	public TextureRegion getTextureRegion(Dungeon dungeon, int x, int y) {
		TileType[] adjacentTiles = dungeon.getLevel().tileStore.getAdjacentTileTypes(x, y);
		boolean top = adjacentTiles[2].isInnerRoomTile();
		
		if (top && x % 2 == 0) {
			return wallHPillar;
		} else {
			return getImageFromMask(getPositionMask(dungeon.getLevel(), x, y));
		}
	}
	
	@Override
	public TextureRegion getTextureRegionExtra(Dungeon dungeon, int x, int y) {
		TileType[] adjacentTiles = dungeon.getLevel().tileStore.getAdjacentTileTypes(x, y);
		
		boolean h = adjacentTiles[0].isWall() || adjacentTiles[1].isWall();
		boolean top = adjacentTiles[2].isInnerRoomTile();
		
		if (h && top && x % 2 == 0) {
			return wallHPillarExtra;
		}
		
		return null;
	}
	
	protected TextureRegion getImageFromMask(int mask) {
		return getImageFromMask(images, mask);
	}
	
	protected TextureRegion getImageFromMask(TextureRegion[] set, int mask) {
		return set[MAP[mask]];
	}
	
	protected int getPositionMask(Level level, int x, int y) {
		int n = isJoinedTile(level.tileStore.getTileType(x, y - 1)) ? 1 : 0;
		int s = isJoinedTile(level.tileStore.getTileType(x, y + 1)) ? 1 : 0;
		int w = isJoinedTile(level.tileStore.getTileType(x - 1, y)) ? 1 : 0;
		int e = isJoinedTile(level.tileStore.getTileType(x + 1, y)) ? 1 : 0;
		
		return n + 2 * e + 4 * s + 8 * w ;
	}
	
	protected boolean isJoinedTile(TileType type) {
		return type.isWall();
	}
	
	@Override
	public void draw(SpriteBatch batch, Dungeon dungeon, int x, int y) {
		TileType[] adjacentTiles = dungeon.getLevel().tileStore.getAdjacentTileTypes(x, y);
		
		boolean h = adjacentTiles[0].isWall() || adjacentTiles[1].isWall();
		boolean top = adjacentTiles[2].isInnerRoomTile();
		
		drawTile(batch, getTextureRegion(dungeon, x, y), x, y);
		
		if (h && top && x % 2 != 0) {
			rand.setSeed(y * dungeon.getLevel().getWidth() + x);
			
			WallDecoration decoration = wallDecoration.next(rand);
			decoration.draw(this, batch, dungeon, x, y, rand);
		}
	}
	
	@Override
	public void drawExtra(SpriteBatch batch, Dungeon dungeon, int x, int y) {
		TileType[] adjacentTiles = dungeon.getLevel().tileStore.getAdjacentTileTypes(x, y);
		
		boolean h = adjacentTiles[0].isWall() || adjacentTiles[1].isWall();
		boolean top = adjacentTiles[2].isInnerRoomTile();
		
		TextureRegion t = getTextureRegionExtra(dungeon, x, y);
		
		if (t != null) {
			drawTile(batch, t, x, y + 1);
		}
		
		if (h && top && x % 2 != 0) {
			rand.setSeed(y * dungeon.getLevel().getWidth() + x);
			
			WallDecoration decoration = wallDecoration.next(rand);
			decoration.drawExtra(this, batch, dungeon, x, y, rand);
		}
	}
}
