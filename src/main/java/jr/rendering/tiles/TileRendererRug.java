package jr.rendering.tiles;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import jr.dungeon.Dungeon;
import jr.dungeon.tiles.TileType;
import jr.rendering.assets.Assets;

public class TileRendererRug extends TileRendererBlob8 {
	private TextureRegion rug; private String rugFileName;
	private TextureRegion floor; private String floorFileName;

	private TextureRegion[] overlayImages = new TextureRegion[BLOB_SHEET_WIDTH * BLOB_SHEET_HEIGHT];
	private TextureRegion[] cutoutImages = new TextureRegion[BLOB_SHEET_WIDTH * BLOB_SHEET_HEIGHT];

	private boolean connectToOthers;
	private TileType self;

	public TileRendererRug(String rugFileName, String floorFileName) {
		this(rugFileName, floorFileName, false, null);
	}

	public TileRendererRug(String rugFileName, String floorFileName, boolean connectToOthers, TileType self) {
		super("rug_overlay");
		
		this.connectToOthers = connectToOthers;
		this.self = self;
		
		rug = getImageFromSheet("textures/tiles.png", sheetX, sheetY);
		floor = getImageFromSheet("textures/tiles.png", floorSheetX, floorSheetY);

		loadBlob(overlayImages, 3, 0);
		loadBlob(cutoutImages, 0, 1);
		
		bakeBlobs(cutoutImages, "rug", rug, floor);
	}
	
	@Override
	public void onLoad(Assets assets) {
		super.onLoad(assets);
		
		assets.textures.load("blobs/" + fileName + ".png", t -> loadBlob(new TextureRegion(t), images));
		assets.textures.load("blobs/" + fileName + ".png", t -> loadBlob(new TextureRegion(t), images));
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
