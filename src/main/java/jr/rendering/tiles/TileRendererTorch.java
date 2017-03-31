package jr.rendering.tiles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import jr.dungeon.Dungeon;
import jr.dungeon.tiles.TileType;

public class TileRendererTorch extends TileRenderer {
	private TextureRegion wallH;
	private TextureRegion wallV;
	private TextureRegion wallCT;
	private TextureRegion wallCB;
	
	private TextureRegion torch;
	
	public TileRendererTorch(int sheetX, int sheetY, String particleName) {
		wallH = getImageFromSheet("textures/tiles.png", 1, 0);
		wallV = getImageFromSheet("textures/tiles.png", 0, 0);
		wallCT = getImageFromSheet("textures/tiles.png", 2, 0);
		wallCB = getImageFromSheet("textures/tiles.png", 3, 0);
		
		torch = getImageFromSheet("textures/tiles.png", sheetX, sheetY);
		
		ParticleEffect torchEffect = new ParticleEffect();
		torchEffect.load(Gdx.files.internal("particles/" + particleName + ".particle"), Gdx.files.internal("textures"));
		
		effectPool = new ParticleEffectPool(torchEffect, 50, 500);
	}
	
	@Override
	public TextureRegion getTextureRegion(Dungeon dungeon, int x, int y) {
		TileType[] adjacentTiles = dungeon.getLevel().getTileStore().getAdjacentTileTypes(x, y);
		
		boolean h = adjacentTiles[0].isWall() || adjacentTiles[1].isWall();
		boolean v = adjacentTiles[2].isWall() || adjacentTiles[3].isWall();
		
		if (h && !v) {
			return wallH;
		} else if (!h && v) {
			return wallV;
		} else {
			return adjacentTiles[2].isWall() ? wallCT : wallCB;
		}
	}
	
	@Override
	public TextureRegion getTextureRegionExtra(Dungeon dungeon, int x, int y) {
		TileType[] adjacentTiles = dungeon.getLevel().getTileStore().getAdjacentTileTypes(x, y);
		
		boolean h = adjacentTiles[0].isWall() || adjacentTiles[1].isWall();
		boolean v = adjacentTiles[2].isWall() || adjacentTiles[3].isWall();
		
		if (h && !v && adjacentTiles[2].isInnerRoomTile()) {
			return torch;
		}
		
		return null;
	}
	
	@Override
	public void draw(SpriteBatch batch, Dungeon dungeon, int x, int y) {
		drawTile(batch, getTextureRegion(dungeon, x, y), x, y);
	}
	
	@Override
	public void drawExtra(SpriteBatch batch, Dungeon dungeon, int x, int y) {
		TextureRegion t = getTextureRegionExtra(dungeon, x, y);
		
		if (t != null) {
			drawTile(batch, t, x, y);
		}
	}
	
	@Override
	public int getParticleYOffset() {
		return super.getParticleYOffset() - 3;
	}
	
	@Override
	public boolean shouldDrawParticles(Dungeon dungeon, int x, int y) {
		TileType[] adjacentTiles = dungeon.getLevel().getTileStore().getAdjacentTileTypes(x, y);
		
		boolean h = adjacentTiles[0].isWall() || adjacentTiles[1].isWall();
		boolean v = adjacentTiles[2].isWall() || adjacentTiles[3].isWall();
		
		return h && !v && adjacentTiles[2].isInnerRoomTile();
	}
}
