package jr.rendering.tiles;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import jr.dungeon.Dungeon;
import jr.dungeon.tiles.Tile;
import jr.dungeon.tiles.states.TileStateTorch;
import jr.rendering.assets.Assets;
import jr.rendering.tiles.walls.TileRendererWall;
import jr.utils.Colour;
import jr.utils.Utils;

import java.util.Arrays;

import static jr.rendering.assets.Particles.particleFile;
import static jr.rendering.assets.Textures.tileFile;

public class TileRendererTorch extends TileRendererWall {
	private TextureRegion torch;
	private TextureRegion torchGlow;
	
	private String particleName;
	
	private Color oldColour = new Color();
	
	public TileRendererTorch(String particleName) {
		super();
		this.particleName = particleName;
	}
	
	@Override
	public void onLoad(Assets assets) {
		super.onLoad(assets);
		
		assets.textures.loadPacked(tileFile("torch"), t -> torch = t);
		assets.textures.loadPacked(tileFile("torch_glow"), t -> torchGlow = t);
		
		assets.particles.load(particleFile(particleName), p -> effectPool = new ParticleEffectPool(p, 50, 500));
	}
	
	@Override
	public TextureRegion getTextureRegionExtra(Dungeon dungeon, int x, int y) {
		// TODO: this is slightly different than what it used to be. is this still correct?
		return isTopHorizontal(dungeon, x, y) ? torch : null;
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
				oldColour.set(batch.getColor());
				batch.setColor(Utils.colourToGdx(tile.getLightColour(), 0));
				drawTile(batch, torchGlow, x, y);
				batch.setColor(oldColour);
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
		return isTopHorizontal(dungeon, x, y);
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
