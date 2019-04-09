package jr.rendering.tiles;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import jr.dungeon.tiles.Tile;
import jr.dungeon.tiles.TileType;
import jr.rendering.assets.Assets;
import jr.utils.Point;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static jr.rendering.assets.Textures.tileFile;

public class TileRendererConnecting extends TileRendererBlob8 {
	private String fileName, bgFileName;
	private TextureRegion fg, bg;
	
	private List<TileType> connecting;
	
	private boolean exclusive;
	
	private String atlasName;

	public TileRendererConnecting(String fileName, String bgFileName, String atlasName, boolean exclusive, TileType... connecting) {
		super("connecting");
		
		this.fileName = fileName;
		this.bgFileName = bgFileName;
		
		this.atlasName = atlasName;
		
		this.exclusive = exclusive;
		this.connecting = new ArrayList<>(Arrays.asList(connecting));
	}
	
	@Override
	public void onLoad(Assets assets) {
		super.onLoad(assets);
		
		assets.textures.loadPacked(tileFile(fileName), t -> fg = t);
		assets.textures.loadPacked(tileFile(bgFileName), t -> bg = t);
	}
	
	@Override
	public void onLoaded(Assets assets) {
		super.onLoaded(assets);
		
		bakeBlobs(images, atlasName, fg, bg);
	}
	
	@Override
	boolean isJoinedTile(TileType tile) {
		return exclusive != connecting.contains(tile);
	}
	
	@Override
	public TextureRegion getTextureRegion(Tile tile, Point p) {
		return fg;
	}

	@Override
	public void draw(SpriteBatch batch, Tile tile, Point p) {
		drawBakedBlob(batch, tile, p, atlasName);
	}
}
