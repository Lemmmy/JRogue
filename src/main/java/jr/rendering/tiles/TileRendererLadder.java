package jr.rendering.tiles;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import jr.dungeon.tiles.Tile;
import jr.dungeon.tiles.TileType;
import jr.utils.Point;

import java.util.Arrays;

public class TileRendererLadder extends TileRendererBasic {
	public TileRendererLadder(String fileName) {
		super(fileName);
	}
	
	@Override
	public TextureRegion getTextureRegion(Tile tile, Point p) {
		return getFloorImage(tile, tile.position);
	}
	
	@Override
	public TextureRegion getTextureRegionExtra(Tile tile, Point p) {
		return image;
	}
	
	@Override
	public void draw(SpriteBatch batch, Tile tile, Point p) {
		getFloorRenderer(tile, p).draw(batch, tile, p);
	}
	
	@Override
	public void drawExtra(SpriteBatch batch, Tile tile, Point p) {
		TextureRegion t = getTextureRegionExtra(tile, p);
		drawTile(batch, t, p);
	}
	
	public TileRenderer getFloorRenderer(Tile tile, Point p) {
		TileType t = Arrays.stream(tile.getLevel().tileStore.getAdjacentTileTypes(p))
			.filter(TileType::isFloor)
			.findAny()
			.orElse(TileType.TILE_ROOM_FLOOR);
		
		return TileMap.valueOf(t.name()).getRenderer();
	}
	
	public TextureRegion getFloorImage(Tile tile, Point p) {
		return getFloorRenderer(tile, p).getTextureRegion(tile, p);
	}
	
	protected enum StairDirection {
		UP,
		DOWN
	}
}
