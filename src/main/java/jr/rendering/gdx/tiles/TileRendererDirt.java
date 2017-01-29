package jr.rendering.gdx.tiles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import jr.dungeon.Dungeon;
import jr.dungeon.tiles.TileType;

public class TileRendererDirt extends TileRendererBlob8 {
	private TextureRegion dirt;
	private TextureRegion floor;

	private TileType self;

	public TileRendererDirt(int sheetX,
							int sheetY,
							int floorSheetX,
							int floorSheetY,
							TileType self) {
		super(1, 0);
		
		this.self = self;
		
		dirt = getImageFromSheet("textures/tiles.png", sheetX, sheetY);
		floor = getImageFromSheet("textures/tiles.png", floorSheetX, floorSheetY);
	}
	
	@Override
	boolean isJoinedTile(TileType tile) {
		return tile == self;
	}
	
	@Override
	public void draw(SpriteBatch batch, Dungeon dungeon, int x, int y) {
		TextureRegion blobImage = getImageFromMask(getPositionMask(dungeon.getLevel(), x, y));
		
		drawTile(batch, dirt, x, y);
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
	}
}
