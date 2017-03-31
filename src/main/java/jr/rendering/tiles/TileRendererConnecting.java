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
		drawGenericBlob(batch, dungeon, x, y, fg, bg);
	}
}
