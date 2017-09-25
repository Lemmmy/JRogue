package jr.rendering.tiles;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import jr.dungeon.Dungeon;
import jr.dungeon.tiles.TileType;

public class TileRendererRug extends TileRendererBlob8 {
	private TextureRegion rug;
	private TextureRegion floor;

	private TextureRegion[] overlayImages = new TextureRegion[BLOB_SHEET_WIDTH * BLOB_SHEET_HEIGHT];
	private TextureRegion[] cutoutImages = new TextureRegion[BLOB_SHEET_WIDTH * BLOB_SHEET_HEIGHT];

	private boolean connectToOthers;
	private TileType self;

	public TileRendererRug(int sheetX, int sheetY, int floorSheetX, int floorSheetY) {
		this(sheetX, sheetY, floorSheetX, floorSheetY, false, null);
	}

	public TileRendererRug(int sheetX,
						   int sheetY,
						   int floorSheetX,
						   int floorSheetY,
						   boolean connectToOthers,
						   TileType self) {
		super(1, 0);
		
		this.connectToOthers = connectToOthers;
		this.self = self;
		
		rug = getImageFromSheet("textures/tiles.png", sheetX, sheetY);
		floor = getImageFromSheet("textures/tiles.png", floorSheetX, floorSheetY);

		loadBlob(overlayImages, 3, 0);
		loadBlob(cutoutImages, 0, 1);
		
		bakeBlobs(cutoutImages, "rug", rug, floor);
	}
	
	@Override
	boolean isJoinedTile(TileType tile) {
		if (connectToOthers) {
			return tile == null /* so the water looks like its going offscreen */ ||
				tile == TileType.TILE_ROOM_WATER ||
				tile == TileType.TILE_GROUND_WATER;
		} else {
			return tile == self;
		}
	}
	
	@Override
	public TextureRegion getTextureRegion(Dungeon dungeon, int x, int y) {
		return rug;
	}
	
	@Override
	public void draw(SpriteBatch batch, Dungeon dungeon, int x, int y) {
		TextureRegion overlayImage = getImageFromMask(overlayImages, getPositionMask(dungeon.getLevel(), x, y));
		
		drawBakedBlob(batch, dungeon, x, y, "rug");
		drawTile(batch, overlayImage, x, y);
	}
}
