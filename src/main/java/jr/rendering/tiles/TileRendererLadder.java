package jr.rendering.tiles;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import jr.dungeon.Dungeon;
import jr.dungeon.tiles.TileType;

import java.util.Arrays;

public class TileRendererLadder extends TileRendererBasic {
	public TileRendererLadder(int sheetX, int sheetY) {
		super("textures/tiles.png", sheetX, sheetY);
	}
	
	@Override
	public TextureRegion getTextureRegion(Dungeon dungeon, int x, int y) {
		return getFloorImage(dungeon, x, y);
	}
	
	@Override
	public TextureRegion getTextureRegionExtra(Dungeon dungeon, int x, int y) {
		return image;
	}
	
	@Override
	public void draw(SpriteBatch batch, Dungeon dungeon, int x, int y) {
		getFloorRenderer(dungeon, x, y).draw(batch, dungeon, x, y);
	}
	
	@Override
	public void drawExtra(SpriteBatch batch, Dungeon dungeon, int x, int y) {
		TextureRegion t = getTextureRegionExtra(dungeon, x, y);
		
		if (t != null) {
			drawTile(batch, t, x, y);
		}
	}
	
	public TileRenderer getFloorRenderer(Dungeon dungeon, int x, int y) {
		TileType t = Arrays.stream(dungeon.getLevel().tileStore.getAdjacentTileTypes(x, y))
			.filter(TileType::isFloor)
			.findAny()
			.orElse(TileType.TILE_ROOM_FLOOR);
		
		return TileMap.valueOf(t.name()).getRenderer();
	}
	
	public TextureRegion getFloorImage(Dungeon dungeon, int x, int y) {
		return getFloorRenderer(dungeon, x, y).getTextureRegion(dungeon, x, y);
	}
	
	protected enum StairDirection {
		UP,
		DOWN
	}
}
