package jr.rendering.tiles.walls;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.tiles.TileType;
import jr.rendering.assets.Assets;
import jr.rendering.tiles.TileRenderer;
import jr.rendering.utils.BlobUtils;
import jr.rendering.utils.ImageUtils;
import jr.utils.WeightedCollection;

import java.util.Random;

import static jr.rendering.assets.Textures.tileFile;

public class TileRendererWall extends TileRenderer {
	protected static final int SHEET_WIDTH = 4;
	protected static final int SHEET_HEIGHT = 4;
	
	protected final WeightedCollection<WallDecoration> wallDecoration = new WeightedCollection<>();
	{
		wallDecoration.add(100, null); // no decoration
		wallDecoration.add(30, new WallDecorationCobweb());
		wallDecoration.add(10, new WallDecorationGrate());
	}
	
	private static TextureRegion[] images = new TextureRegion[SHEET_WIDTH * SHEET_HEIGHT];
	private static TextureRegion wallHPillar, wallHPillarExtra;
	
	private static final int[] MAP = new int[] {
		12, 8, 13, 9, 0, 4, 1, 5, 15, 11, 14, 10, 3, 7, 2, 6
	};
	
	private Random rand = new Random();
	
	@Override
	public void onLoad(Assets assets) {
		super.onLoad(assets);
		
		assets.textures.loadPacked(tileFile("room_wall_pillar"), t -> wallHPillar = t);
		assets.textures.loadPacked(tileFile("room_wall_pillar_extra"), t -> wallHPillarExtra = t);
		
		assets.textures.loadPacked(tileFile("room_walls"), t -> ImageUtils.loadSheet(t, images, SHEET_WIDTH, SHEET_HEIGHT));
	}
	
	protected boolean isTopHorizontal(Dungeon dungeon, int x, int y) {
		TileType[] adjacentTiles = dungeon.getLevel().tileStore.getAdjacentTileTypes(x, y);
		
		boolean h = adjacentTiles[0].isWallTile() || adjacentTiles[1].isWallTile();
		boolean top = adjacentTiles[2].isInnerRoomTile();
		
		return h && top;
	}
	
	@Override
	public TextureRegion getTextureRegion(Dungeon dungeon, int x, int y) {
		if (isTopHorizontal(dungeon, x, y) && x % 2 == 0) {
			return wallHPillar;
		} else {
			return getImageFromMask(getPositionMask(dungeon.getLevel(), x, y));
		}
	}
	
	@Override
	public TextureRegion getTextureRegionExtra(Dungeon dungeon, int x, int y) {
		if (isTopHorizontal(dungeon, x, y) && x % 2 == 0) {
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
		return BlobUtils.getPositionMask4(this::isJoinedTile, level, x, y);
	}
	
	protected boolean isJoinedTile(TileType type) {
		return type.isWallTile();
	}
	
	@Override
	public void draw(SpriteBatch batch, Dungeon dungeon, int x, int y) {
		drawTile(batch, getTextureRegion(dungeon, x, y), x, y);
		
		if (isTopHorizontal(dungeon, x, y) && x % 2 != 0) {
			rand.setSeed(y * dungeon.getLevel().getWidth() + x);
			
			WallDecoration decoration = wallDecoration.next(rand);
			if (decoration != null)	decoration.draw(this, batch, dungeon, x, y, rand);
		}
	}
	
	@Override
	public void drawExtra(SpriteBatch batch, Dungeon dungeon, int x, int y) {
		TextureRegion t = getTextureRegionExtra(dungeon, x, y);
		
		if (t != null) {
			drawTile(batch, t, x, y + 1);
		}
		
		if (isTopHorizontal(dungeon, x, y) && x % 2 != 0) {
			rand.setSeed(y * dungeon.getLevel().getWidth() + x);
			
			WallDecoration decoration = wallDecoration.next(rand);
			if (decoration != null)	decoration.drawExtra(this, batch, dungeon, x, y, rand);
		}
	}
}
