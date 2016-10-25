package pw.lemmmy.jrogue.rendering.gdx.tiles;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.TileType;

public class TileRendererWater extends TileRendererBlob {
	private TextureRegion water;
	private TextureRegion floor;

	private TextureRegion[] overlayImages = new TextureRegion[BLOB_SHEET_WIDTH * BLOB_SHEET_HEIGHT];

	private boolean connectToOthers;
	private TileType self;

	public TileRendererWater(int sheetX, int sheetY, int floorSheetX, int floorSheetY) {
		this(sheetX, sheetY, floorSheetX, floorSheetY, true, null);
	}

	public TileRendererWater(int sheetX, int sheetY, int floorSheetX, int floorSheetY, boolean connectToOthers, TileType self) {
		super(1, 0);

		this.connectToOthers = connectToOthers;
		this.self = self;

		water = getImageFromSheet("tiles.png", sheetX, sheetY);
		water = getImageFromSheet("tiles.png", floorSheetX, floorSheetY);

		loadBlob(overlayImages, 2, 0);
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
	public void draw(SpriteBatch batch, Dungeon dungeon, int x, int y) {
		// TODO
	}
}
