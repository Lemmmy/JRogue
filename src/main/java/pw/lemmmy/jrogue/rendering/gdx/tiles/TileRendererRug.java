package pw.lemmmy.jrogue.rendering.gdx.tiles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.tiles.TileType;

public class TileRendererRug extends TileRendererBlob {
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
		
		rug = getImageFromSheet("tiles.png", sheetX, sheetY);
		floor = getImageFromSheet("tiles.png", floorSheetX, floorSheetY);

		loadBlob(overlayImages, 3, 0);
		loadBlob(cutoutImages, 0, 1);
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
		TextureRegion blobImage = getImageFromMask(cutoutImages, getPositionMask(dungeon.getLevel(), x, y));
		TextureRegion overlayImage = getImageFromMask(overlayImages, getPositionMask(dungeon.getLevel(), x, y));
		
		Color colourOld = batch.getColor();
		
		drawTile(batch, rug, x, y);
		batch.flush();
		
		Gdx.gl.glColorMask(false, false, false, true);
		batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ZERO);
		drawTile(batch, blobImage, x, y);
		batch.flush();
		
		Gdx.gl.glColorMask(true, true, true, true);
		batch.setBlendFunction(GL20.GL_DST_ALPHA, GL20.GL_ONE_MINUS_DST_ALPHA);
		drawTile(batch, floor, x, y);
		batch.flush();
		
		batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		drawTile(batch, overlayImage, x, y);
		batch.flush();
		
		batch.setColor(colourOld);
	}
}
