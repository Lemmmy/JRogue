package pw.lemmmy.jrogue.rendering.gdx.tiles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.tiles.TileType;

public class TileRendererCorridor extends TileRendererBlob8 {
	private TextureRegion corridor;
	private TextureRegion empty;
	
	public TileRendererCorridor() {
		super(1, 0);
		
		corridor = getImageFromSheet("tiles.png", 0, 1);
		empty = getImageFromSheet("tiles.png", 1, 1);
	}
	
	@Override
	boolean isJoinedTile(TileType tile) {
		return tile == TileType.TILE_CORRIDOR || tile.isWallTile();
	}
	
	@Override
	public void draw(SpriteBatch batch, Dungeon dungeon, int x, int y) {
		TextureRegion blobImage = getImageFromMask(getPositionMask(dungeon.getLevel(), x, y));
		
		drawTile(batch, corridor, x, y);
		batch.flush();
		
		Gdx.gl.glColorMask(false, false, false, true);
		batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ZERO);
		drawTile(batch, blobImage, x, y);
		batch.flush();
		
		Gdx.gl.glColorMask(true, true, true, true);
		batch.setBlendFunction(GL20.GL_DST_ALPHA, GL20.GL_ONE_MINUS_DST_ALPHA);
		drawTile(batch, empty, x, y);
		batch.flush();
		
		batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
	}
}
