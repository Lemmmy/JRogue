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
	public TextureRegion getTextureRegion(Dungeon dungeon, int x, int y) {
		switch (state) {
			case OPEN:
				TileType[] adjacentTiles = dungeon.getLevel().getTileStore().getAdjacentTileTypes(x, y);
				boolean h = adjacentTiles[0].isWall() || adjacentTiles[1].isWall();
				
				return h ? openH : openV;
			
			case BROKEN:
				return broken;
				
			default:
				return closed;
		}
	}
	
	@Override
	public void draw(SpriteBatch batch, Dungeon dungeon, int x, int y) {
		drawTile(batch, getTextureRegion(dungeon, x, y), x, y);
	}
	
	protected enum DoorState {
		CLOSED,
		LOCKED,
		OPEN,
		BROKEN
	}
}
