package jr.rendering.tiles;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import jr.dungeon.Dungeon;
import jr.dungeon.tiles.TileType;
import jr.rendering.assets.Assets;

import static jr.rendering.assets.Textures.tileFile;

public class TileRendererCorridor extends TileRendererBlob8 {
	private TextureRegion corridor;
	private TextureRegion empty;
	
	public TileRendererCorridor() {
		super("connecting");
	}
	
	@Override
	public void onLoad(Assets assets) {
		super.onLoad(assets);
		
		assets.textures.loadPacked(tileFile("corridor"), t -> corridor = t);
		assets.textures.loadPacked(tileFile("ground"), t -> empty = t);
	}
	
	@Override
	public void onLoaded(Assets assets) {
		super.onLoaded(assets);
	
		bakeBlobs(images, "corridor", corridor, empty);
	}
	
	@Override
	boolean isJoinedTile(TileType tile) {
		return tile == TileType.TILE_CORRIDOR || tile.isWallTile();
	}
	
	@Override
	public TextureRegion getTextureRegion(Dungeon dungeon, int x, int y) {
		return corridor;
	}

	@Override
	public void draw(SpriteBatch batch, Dungeon dungeon, int x, int y) {
		drawBakedBlob(batch, dungeon, x, y, "corridor");
	}
	
	@Override
	public boolean canDrawBasic() {
		return true;
	}
	
	@Override
	public void drawBasic(SpriteBatch batch, Dungeon dungeon, int x, int y) {
		drawTile(batch, corridor, x, y);
	}
}
