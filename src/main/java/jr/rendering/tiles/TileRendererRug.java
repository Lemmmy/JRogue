package jr.rendering.tiles;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import jr.dungeon.Dungeon;
import jr.dungeon.tiles.TileType;
import jr.rendering.assets.Assets;

import static jr.rendering.assets.Textures.blobFile;
import static jr.rendering.assets.Textures.tileFile;

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
		super(null);
		
		this.connectToOthers = connectToOthers;
		this.self = self;
		
		this.rugFileName = rugFileName;
		this.floorFileName = floorFileName;
	}
	
	@Override
	public void onLoad(Assets assets) {
		super.onLoad(assets);
		
		assets.textures.loadPacked(tileFile(rugFileName), t -> rug = t);
		assets.textures.loadPacked(tileFile(floorFileName), t -> floor = t);
		
		assets.textures.loadPacked(blobFile("rug_overlay"), t -> loadBlob(t, overlayImages));
		assets.textures.loadPacked(blobFile("rug_cutout"), t -> loadBlob(t, cutoutImages));
	}
	
	@Override
	public void onLoaded(Assets assets) {
		super.onLoaded(assets);
		
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
