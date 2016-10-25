package pw.lemmmy.jrogue.rendering.gdx.tiles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.TileType;

public class TileRendererTorch extends TileRenderer {
	private TextureRegion wallH;
	private TextureRegion wallV;
	private TextureRegion wallCT;
	private TextureRegion wallCB;

	private TextureRegion torch;

	public TileRendererTorch(int sheetX, int sheetY, String particleName) {
		wallH = getImageFromSheet("tiles.png", 1, 0);
		wallV = getImageFromSheet("tiles.png", 0, 0);
		wallCT = getImageFromSheet("tiles.png", 2, 0);
		wallCB = getImageFromSheet("tiles.png", 3, 0);

		torch = getImageFromSheet("tiles.png", sheetX, sheetY);

		ParticleEffect torchEffect = new ParticleEffect();
		torchEffect.load(Gdx.files.internal(particleName + ".particle"), Gdx.files.internal(""));

		effectPool = new ParticleEffectPool(torchEffect, 50, 500);
	}

	@Override
	public void draw(SpriteBatch batch, Dungeon dungeon, int x, int y) {
		TileType[] adjacentTiles = dungeon.getLevel().getAdjacentTiles(x, y);

		boolean h = adjacentTiles[0] == TileType.TILE_ROOM_WALL || adjacentTiles[1] == TileType.TILE_ROOM_WALL;
		boolean v = adjacentTiles[2] == TileType.TILE_ROOM_WALL || adjacentTiles[3] == TileType.TILE_ROOM_WALL;

		if (h && !v) {
			drawTile(batch, wallH, x, y);
		} else if (!h && v) {
			drawTile(batch, wallV, x, y);
		} else {
			if (adjacentTiles[2] == TileType.TILE_ROOM_WALL) {
				drawTile(batch, wallCT, x, y);
			} else {
				drawTile(batch, wallCB, x, y);
			}
		}

		drawTile(batch, torch, x, y);
	}

	@Override
	public int getParticleYOffset() {
		return super.getParticleYOffset() - 3;
	}
}
