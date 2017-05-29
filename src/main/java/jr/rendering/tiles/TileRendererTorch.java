package jr.rendering.tiles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import jr.dungeon.Dungeon;
import jr.dungeon.tiles.Tile;
import jr.dungeon.tiles.TileType;
import jr.dungeon.tiles.states.TileStateTorch;
import jr.rendering.tiles.walls.TileRendererWall;
import jr.utils.Colour;
import jr.utils.Utils;

import java.util.Arrays;

public class TileRendererTorch extends TileRendererWall {
	private TextureRegion torch;
	private TextureRegion torchGlow;
	
	public TileRendererTorch(int sheetX, int sheetY, int glowX, int glowY, String particleName) {
		super();
		
		torch = getImageFromSheet("textures/tiles.png", sheetX, sheetY);
		torchGlow = getImageFromSheet("textures/tiles.png", glowX, glowY);
		
		ParticleEffect torchEffect = new ParticleEffect();
		torchEffect.load(Gdx.files.internal("particles/" + particleName + ".particle"), Gdx.files.internal("textures"));
		
		effectPool = new ParticleEffectPool(torchEffect, 50, 500);
	}
	
	@Override
	public TextureRegion getTextureRegionExtra(Dungeon dungeon, int x, int y) {
		TileType[] adjacentTiles = dungeon.getLevel().tileStore.getAdjacentTileTypes(x, y);
		
		boolean h = adjacentTiles[0].isWallTile() || adjacentTiles[1].isWallTile();
		boolean v = adjacentTiles[2].isWallTile() || adjacentTiles[3].isWallTile();
		
		if (h && !v && adjacentTiles[2].isInnerRoomTile()) {
			return torch;
		}
		
		return null;
	}
	
	@Override
	public TextureRegion getTextureRegion(Dungeon dungeon, int x, int y) {
		return getImageFromMask(getPositionMask(dungeon.getLevel(), x, y));
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
			
			Tile tile = dungeon.getLevel().tileStore.getTile(x, y);
			
			if (tile != null && tile.hasState() && tile.getState() instanceof TileStateTorch) {
				Color c = batch.getColor();
				batch.setColor(Utils.colourToGdx(tile.getLightColour(), 0));
				drawTile(batch, torchGlow, x, y);
				batch.setColor(c);
			} else {
				drawTile(batch, torchGlow, x, y);
			}
		}
	}
	
	@Override
	public int getParticleYOffset() {
		return super.getParticleYOffset() - 3;
	}
	
	@Override
	public boolean shouldDrawParticles(Dungeon dungeon, int x, int y) {
		TileType[] adjacentTiles = dungeon.getLevel().tileStore.getAdjacentTileTypes(x, y);
		
		boolean h = adjacentTiles[0].isWallTile() || adjacentTiles[1].isWallTile();
		boolean v = adjacentTiles[2].isWallTile() || adjacentTiles[3].isWallTile();
		
		return h && !v && adjacentTiles[2].isInnerRoomTile();
	}
	
	@Override
	public void applyParticleChanges(Dungeon dungeon, int x, int y, ParticleEffectPool.PooledEffect effect) {
		super.applyParticleChanges(dungeon, x, y, effect);
		
		Tile tile = dungeon.getLevel().tileStore.getTile(x, y);
		
		if (tile != null && tile.hasState() && tile.getState() instanceof TileStateTorch) {
			Colour c1 = tile.getLightColour();
			Colour c2 = ((TileStateTorch) tile.getState()).getParticleDarkColour();
			
			Arrays.stream(effect.getEmitters().items)
				.filter(e -> e.getName().equalsIgnoreCase("Fire"))
				.findFirst().ifPresent(e -> e.getTint().setColors(new float[] {
					c1.r, c1.g, c1.b,
					c2.r, c2.g, c2.b
				}));
		}
	}
}
