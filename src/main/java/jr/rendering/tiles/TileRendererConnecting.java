package jr.rendering.tiles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import jr.dungeon.Dungeon;
import jr.dungeon.tiles.TileType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TileRendererConnecting extends TileRendererBlob8 {
	private TextureRegion fg;
	private TextureRegion bg;

	private List<TileType> connecting;
	
	private boolean exclusive;

	public TileRendererConnecting(int sheetX,
								  int sheetY,
								  int bgSheetX,
								  int bgSheetY,
								  boolean exclusive,
								  TileType... connecting) {
		super(1, 0);
		
		this.exclusive = exclusive;
		this.connecting = new ArrayList<>(Arrays.asList(connecting));
		
		fg = getImageFromSheet("textures/tiles.png", sheetX, sheetY);
		bg = getImageFromSheet("textures/tiles.png", bgSheetX, bgSheetY);
	}
	
	@Override
	boolean isJoinedTile(TileType tile) {
		return exclusive != connecting.contains(tile);
	}
	
	@Override
	public TextureRegion getTextureRegion(Dungeon dungeon, int x, int y) {
		return fg;
	}
	
	@Override
	public void draw(SpriteBatch batch, Dungeon dungeon, int x, int y) {
		TextureRegion blobImage = getImageFromMask(getPositionMask(dungeon.getLevel(), x, y));
		
		drawTile(batch, fg, x, y);
		batch.flush();
		
		Gdx.gl.glColorMask(false, false, false, true);
		batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ZERO);
		drawTile(batch, blobImage, x, y);
		batch.flush();
		
		Gdx.gl.glColorMask(true, true, true, true);
		batch.setBlendFunction(GL20.GL_DST_ALPHA, GL20.GL_ONE_MINUS_DST_ALPHA);
		drawTile(batch, bg, x, y);
		batch.flush();
		
		batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
	}
}
