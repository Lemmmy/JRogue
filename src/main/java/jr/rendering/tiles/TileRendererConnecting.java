package jr.rendering.tiles;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import jr.dungeon.Dungeon;
import jr.dungeon.tiles.TileType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TileRendererConnecting extends TileRendererBlob8 {
	private TextureRegion fg;
	
	private List<TileType> connecting;
	
	private boolean exclusive;
	
	private String name;

	public TileRendererConnecting(int sheetX,
								  int sheetY,
								  int bgSheetX,
								  int bgSheetY,
								  boolean exclusive,
								  String name,
								  TileType... connecting) {
		super(1, 0);
		
		this.exclusive = exclusive;
		this.connecting = new ArrayList<>(Arrays.asList(connecting));
		
		fg = getImageFromSheet("textures/tiles.png", sheetX, sheetY);
		TextureRegion bg = getImageFromSheet("textures/tiles.png", bgSheetX, bgSheetY);
		
		this.name = name;
		
		bakeBlobs(images, name, fg, bg);
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
		drawBakedBlob(batch, dungeon, x, y, name);
	}
}
