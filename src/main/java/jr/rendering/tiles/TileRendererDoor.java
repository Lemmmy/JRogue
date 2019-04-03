package jr.rendering.tiles;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import jr.dungeon.Dungeon;
import jr.dungeon.tiles.TileType;
import jr.rendering.assets.Assets;

public class TileRendererDoor extends TileRenderer {
	private TextureRegion openH, openV;
	
	@Override
	public void onLoad(Assets assets) {
		super.onLoad(assets);
		
		assets.textures.load(tileFile("room_door_open_horizontal"), t -> openH = new TextureRegion(t));
		assets.textures.load(tileFile("room_door_open_vertical"), t -> openV = new TextureRegion(t));
	}
	
	@Override
	public TextureRegion getTextureRegion(Dungeon dungeon, int x, int y) {
		TileType[] adjacentTiles = dungeon.getLevel().tileStore.getAdjacentTileTypes(x, y);
		boolean h = adjacentTiles[0].isWallTile() || adjacentTiles[1].isWallTile();
		
		return h ? openH : openV;
	}
	
	@Override
	public void draw(SpriteBatch batch, Dungeon dungeon, int x, int y) {
		drawTile(batch, getTextureRegion(dungeon, x, y), x, y);
	}
}
