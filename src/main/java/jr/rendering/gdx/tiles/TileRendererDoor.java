package jr.rendering.gdx.tiles;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import jr.dungeon.Dungeon;
import jr.dungeon.tiles.TileType;

public class TileRendererDoor extends TileRenderer {
	private static TextureRegion closed;
	private static TextureRegion openH;
	private static TextureRegion openV;
	private static TextureRegion broken;
	private DoorState state;
	
	public TileRendererDoor(DoorState state) {
		if (closed == null || openH == null || openV == null || broken == null) {
			closed = getImageFromSheet("textures/tiles.png", 4, 0);
			openH = getImageFromSheet("textures/tiles.png", 6, 0);
			openV = getImageFromSheet("textures/tiles.png", 5, 0);
			broken = getImageFromSheet("textures/tiles.png", 7, 0);
		}
		
		this.state = state;
	}
	
	@Override
	public void draw(SpriteBatch batch, Dungeon dungeon, int x, int y) {
		switch (state) {
			case LOCKED:
			case CLOSED:
				drawTile(batch, closed, x, y);
				break;
			
			case OPEN:
				TileType[] adjacentTiles = dungeon.getLevel().getTileStore().getAdjacentTileTypes(x, y);
				boolean h = adjacentTiles[0].isWallTile() || adjacentTiles[1].isWallTile();
				
				drawTile(batch, h ? openH : openV, x, y);
				break;
			
			case BROKEN:
				drawTile(batch, broken, x, y);
				break;
		}
	}
	
	protected enum DoorState {
		CLOSED,
		LOCKED,
		OPEN,
		BROKEN
	}
}
