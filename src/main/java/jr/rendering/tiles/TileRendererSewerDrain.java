package jr.rendering.tiles;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import jr.dungeon.tiles.Tile;
import jr.rendering.assets.Assets;
import jr.utils.Directions;
import jr.utils.Point;

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
	public TextureRegion getTextureRegion(Tile tile, Point p) {
		return drain;
	}
	
	@Override
	public TextureRegion getTextureRegionExtra(Tile tile, Point p) {
		return tile.getLevel().tileStore.getTileType(p.add(Directions.SOUTH)).isWater()
			   ? water : null;
	}
	
	@Override
	public void draw(SpriteBatch batch, Tile tile, Point p) {
		drawTile(batch, getTextureRegion(tile, p), p);
	}
	
	@Override
	public void drawExtra(SpriteBatch batch, Tile tile, Point p) {
		drawTile(batch, getTextureRegionExtra(tile, p), p.add(Directions.SOUTH));
	}
}
