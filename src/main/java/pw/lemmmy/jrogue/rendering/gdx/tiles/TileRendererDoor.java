package pw.lemmmy.jrogue.rendering.gdx.tiles;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.TileType;

public class TileRendererDoor extends TileRenderer {
	private DoorState state;

	private static TextureRegion closed;
	private static TextureRegion openH;
	private static TextureRegion openV;
	private static TextureRegion broken;

	public TileRendererDoor(DoorState state) {
		if (closed == null || openH == null || openV == null || broken == null) {
			closed = getImageFromSheet("tiles.png", 4, 0);
			openH = getImageFromSheet("tiles.png", 6, 0);
			openV = getImageFromSheet("tiles.png", 5, 0);
			broken = getImageFromSheet("tiles.png", 7, 0);
		}

		this.state = state;
	}

	@Override
	public void draw(SpriteBatch batch, Dungeon dungeon, int x, int y) {
		switch (state) {
			case CLOSED:
				drawTile(batch, closed, x, y);
				break;

			case OPEN:
				TileType[] adjacentTiles = dungeon.getLevel().getAdjacentTiles(x, y);
				boolean h = adjacentTiles[0].isWallType() || adjacentTiles[1].isWallType();

				drawTile(batch, h ? openH : openV, x, y);
				break;

			case BROKEN:
				drawTile(batch, broken, x, y);
				break;
		}
	}

	protected enum DoorState {
		CLOSED,
		OPEN,
		BROKEN
	}
}
