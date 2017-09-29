package jr.rendering.gdx2d.tiles;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import jr.dungeon.Dungeon;
import jr.dungeon.tiles.TileType;

public class TileRendererCorridor extends TileRendererBlob8 {
	private TextureRegion corridor;
	private TextureRegion empty;
	
	public TileRendererCorridor() {
		super(1, 0);
		
		corridor = getImageFromSheet("textures/tiles.png", 0, 1);
		empty = getImageFromSheet("textures/tiles.png", 1, 1);
		
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