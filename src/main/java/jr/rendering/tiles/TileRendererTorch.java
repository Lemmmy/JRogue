package jr.rendering.tiles;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import jr.dungeon.tiles.Tile;
import jr.dungeon.tiles.states.TileStateTorch;
import jr.rendering.assets.Assets;
import jr.rendering.tiles.walls.TileRendererWall;
import jr.utils.Colour;
import jr.utils.Point;

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
	public TextureRegion getTextureRegionExtra(Tile tile, Point p) {
		// TODO: this is slightly different than what it used to be. is this still correct?
		return isTopHorizontal(tile, p) ? torch : null;
	}
	
	@Override
	public TextureRegion getTextureRegion(Tile tile, Point p) {
		return getImageFromMask(getPositionMask(tile, p));
	}
	
	@Override
	public void draw(SpriteBatch batch, Tile tile, Point p) {
		drawTile(batch, getTextureRegion(tile, p), p);
	}
	
	@Override
	public void drawExtra(SpriteBatch batch, Tile tile, Point p) {
		TextureRegion t = getTextureRegionExtra(tile, p);
		if (t == null) return;
		
		drawTile(batch, t, p);
		
		if (tile.hasState() && tile.getState() instanceof TileStateTorch) {
			oldColour.set(batch.getColor());
			batch.setColor(Colour.colourToGdx(tile.getLightColour(), 0));
			drawTile(batch, torchGlow, p);
			batch.setColor(oldColour);
		} else {
			drawTile(batch, torchGlow, p);
		}
	}
	
	@Override
	public int getParticleYOffset() {
		return super.getParticleYOffset() + 3;
	}
	
	@Override
	public boolean shouldDrawParticles(Tile tile, Point p) {
		return isTopHorizontal(tile, p);
	}
	
	@Override
	public void applyParticleChanges(Tile tile, Point p, ParticleEffectPool.PooledEffect effect) {
		super.applyParticleChanges(tile, p, effect);
		
		if (tile.hasState() && tile.getState() instanceof TileStateTorch) {
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
