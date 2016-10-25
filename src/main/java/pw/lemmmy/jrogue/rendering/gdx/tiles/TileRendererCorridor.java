package pw.lemmmy.jrogue.rendering.gdx.tiles;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.TileType;

public class TileRendererCorridor extends TileRendererBlob {
	private TextureRegion corridor;
	private TextureRegion empty;

	public TileRendererCorridor() {
		super(1, 0);

		corridor = getImageFromSheet("tiles.png", 0, 1);
		empty = getImageFromSheet("tiles.png", 1, 1);
	}

	@Override
	boolean isJoinedTile(TileType tile) {
		return tile == TileType.TILE_CORRIDOR || tile == TileType.TILE_ROOM_DOOR || tile == TileType.TILE_ROOM_WALL;
	}

	@Override
	public void draw(SpriteBatch batch, Dungeon dungeon, int x, int y) {
		// TODO
	}
}
