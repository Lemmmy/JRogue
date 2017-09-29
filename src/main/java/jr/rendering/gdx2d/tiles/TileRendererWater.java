package jr.rendering.gdx2d.tiles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import jr.dungeon.Dungeon;
import jr.dungeon.tiles.TileType;

public class TileRendererWater extends TileRendererBlob8 {
	private TextureRegion water;
	private TextureRegion floor;
	
	private TextureRegion[] overlayImages = new TextureRegion[BLOB_SHEET_WIDTH * BLOB_SHEET_HEIGHT];
	
	private boolean connectToOthers;
	private TileType self;
	
	private float waterTransparency;
	
	public TileRendererWater(int sheetX, int sheetY, int floorSheetX, int floorSheetY, float waterTransparency) {
		this(sheetX, sheetY, floorSheetX, floorSheetY, waterTransparency, true, null);
	}
	
	public TileRendererWater(int sheetX,
							 int sheetY,
							 int floorSheetX,
							 int floorSheetY,
							 float waterTransparency,
							 boolean connectToOthers,
							 TileType self) {
		super(1, 0);
		
		this.connectToOthers = connectToOthers;
		this.self = self;
		
		water = getImageFromSheet("textures/tiles.png", sheetX, sheetY);
		floor = getImageFromSheet("textures/tiles.png", floorSheetX, floorSheetY);
		
		this.waterTransparency = waterTransparency;
		
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
	public TextureRegion getTextureRegion(Dungeon dungeon, int x, int y) {
		return water;
	}
	
	@Override
	public void draw(SpriteBatch batch, Dungeon dungeon, int x, int y) {
		int positionMask = getPositionMask(dungeon.getLevel(), x, y);
		
		TextureRegion blobImage = getImageFromMask(positionMask);
		TextureRegion overlayImage = getImageFromMask(overlayImages, positionMask);
		
		Color colourOld = batch.getColor();
		batch.setColor(colourOld.r, colourOld.g, colourOld.b, 1.0f);
		
		if (waterTransparency < 1.0f) {
			drawTile(batch, floor, x, y);
			
			TileRendererReflective.drawReflection(batch, renderer, dungeon, x, y, ReflectionSettings.create(
				0.00125f,
				16.0f,
				2.0f,
				5.0f,
				0.0f
			));
		}
		
		batch.setColor(colourOld.r, colourOld.g, colourOld.b, waterTransparency);
		drawTile(batch, water, x, y);
		
		batch.setColor(colourOld.r, colourOld.g, colourOld.b, colourOld.a);
		
		batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ZERO);
		Gdx.gl.glColorMask(false, false, false, true);
		
		drawTile(batch, blobImage, x, y);
		
		batch.setBlendFunction(GL20.GL_DST_ALPHA, GL20.GL_ONE_MINUS_DST_ALPHA);
		Gdx.gl.glColorMask(true, true, true, true);
		drawTile(batch, floor, x, y);
		
		batch.setColor(colourOld.r, colourOld.g, colourOld.b, 0.5f);
		batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		drawTile(batch, overlayImage, x, y);
		
		batch.setColor(colourOld);
	}
	
	@Override
	public boolean canDrawBasic() {
		return true;
	}
	
	@Override
	public void drawBasic(SpriteBatch batch, Dungeon dungeon, int x, int y) {
		Color colourOld = batch.getColor();
		batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		
		batch.setColor(colourOld.r, colourOld.g, colourOld.b, waterTransparency);
		drawTile(batch, water, x, y);
		batch.setColor(colourOld);
	}
}