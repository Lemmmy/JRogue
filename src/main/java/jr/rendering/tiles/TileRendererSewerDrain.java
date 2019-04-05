package jr.rendering.tiles;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import jr.dungeon.Dungeon;
import jr.rendering.assets.Assets;

import static jr.rendering.assets.Textures.tileFile;

public class TileRendererSewerDrain extends TileRenderer {
	private static TextureRegion drain;
	private static TextureRegion water;
	
	@Override
	public void onLoad(Assets assets) {
		super.onLoad(assets);
		
		assets.textures.loadPacked(tileFile("sewer_drain"), t -> drain = t);
		assets.textures.loadPacked(tileFile("sewer_drain_water"), t -> water = t);
	}
	
	@Override
	public TextureRegion getTextureRegion(Dungeon dungeon, int x, int y) {
		return drain;
	}
	
	@Override
	public TextureRegion getTextureRegionExtra(Dungeon dungeon, int x, int y) {
		return water;
	}
	
	@Override
	public void draw(SpriteBatch batch, Dungeon dungeon, int x, int y) {
		drawTile(batch, getTextureRegion(dungeon, x, y), x, y);
	}
	
	@Override
	public void drawExtra(SpriteBatch batch, Dungeon dungeon, int x, int y) {
		super.drawExtra(batch, dungeon, x, y);
		
		if (dungeon.getLevel().tileStore.getTileType(x, y - 1).isWater()) {
			drawTile(batch, getTextureRegionExtra(dungeon, x, y), x, y - 1);
		}
	}
}
