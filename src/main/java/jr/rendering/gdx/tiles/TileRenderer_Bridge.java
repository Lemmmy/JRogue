package jr.rendering.gdx.tiles;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import jr.dungeon.Dungeon;
import jr.dungeon.tiles.TileType;

public class TileRenderer_Bridge extends TileRendererBlob8 {
	private TextureRegion bridge;
	
	public TileRenderer_Bridge(int sheetX, int sheetY) {
		super(1, 1);
		
		bridge = getImageFromSheet("textures/tiles.png", sheetX, sheetY);
	}
	
	@Override
	boolean isJoinedTile(TileType tile) {
		return tile == TileType.TILE__BRIDGE;
	}
	
	@Override
	public TextureRegion getTextureRegion(Dungeon dungeon, int x, int y) {
		return bridge;
	}
	
	@Override
	public void draw(SpriteBatch batch, Dungeon dungeon, int x, int y) {
		TextureRegion blobImage = getImageFromMask(getPositionMask(dungeon.getLevel(), x, y));
		
		drawTile(batch, bridge, x, y);
		drawTile(batch, blobImage, x, y);
	}
}
