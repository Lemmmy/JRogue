package jr.rendering.tiles;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.TimeUtils;
import jr.JRogue;
import jr.dungeon.Level;
import jr.dungeon.entities.Entity;
import jr.dungeon.generators.Climate;
import jr.dungeon.tiles.Tile;
import jr.dungeon.tiles.TileFlag;
import jr.dungeon.tiles.TileType;
import jr.rendering.assets.Assets;
import jr.rendering.entities.EntityMap;
import jr.rendering.entities.EntityRenderer;
import jr.rendering.entities.animations.AnimationProvider;
import jr.rendering.screens.GameScreen;
import jr.utils.Point;
import lombok.NonNull;

import java.util.Comparator;

import static jr.rendering.assets.Shaders.shaderFile;

public class TileRendererReflective extends TileRendererBasic {
	private static ShaderProgram shader;
	private boolean shaderLoaded;
	
	private final ReflectionSettings settings;
	
	private static Vector3 tps1 = new Vector3(), tps2 = new Vector3();
	
	public TileRendererReflective(String fileName, @NonNull ReflectionSettings settings) {
		super(fileName);
		this.settings = settings;
	}
	
	@Override
	public void onLoad(Assets assets) {
		super.onLoad(assets);
		
		if (!shaderLoaded) {
			assets.shaders.load(shaderFile("reflection"), s -> shader = s);
			shaderLoaded = true;
		}
	}
	
	@Override
	public void draw(SpriteBatch batch, Tile tile, Point p) {
		super.draw(batch, tile, p);
		drawReflection(batch, renderer, tile, p, settings);
	}
	
	public static void drawReflection(SpriteBatch batch, GameScreen renderer, Tile tile, Point p, @NonNull ReflectionSettings s) {
		Level level = tile.getLevel();
		if (p.y + 1 >= level.getHeight()) return;
		
		ShaderProgram oldShader = batch.getShader();
		
		batch.setShader(shader);
		shader.setUniformf("u_waveAmplitude", 0.0f);
		shader.setUniformf("u_waveFrequency", 0.0f);
		shader.setUniformf("u_timeScale", 0.0f);
		shader.setUniformf("u_fadeAmplitude", s.getFadeAmplitude());
		shader.setUniformf("u_fadeBase", s.getFadeBase());
		
		renderer.getCamera().project(tps1.set(p.x * TileMap.TILE_WIDTH, p.y * TileMap.TILE_HEIGHT, 0.0f));
		renderer.getCamera().project(tps2.set((p.x + 1) * TileMap.TILE_WIDTH, (p.y - 1) * TileMap.TILE_HEIGHT, 0.0f));
		shader.setUniformf("u_tilePositionScreen", tps1.x, tps1.y);
		shader.setUniformf("u_tileSizeScreen", tps2.x - tps1.x, tps2.y - tps1.y);
		
		shader.setUniformf("u_time", 0.0f);
		
		final Point above = p.add(0, 1);
		final Tile tileAbove = level.tileStore.getTile(above);
		final TileType tileAboveType = tileAbove.getType();
		
		final boolean doReflect = (tileAboveType.getFlags() & TileFlag.DONT_REFLECT) != TileFlag.DONT_REFLECT;
		final boolean isWall = (tileAboveType.getFlags() & TileFlag.WALL) == TileFlag.WALL;
		
		if (doReflect && isWall) {
			TileRenderer r = TileMap.valueOf(tileAboveType.name()).getRenderer();
			
			if (r != null) {
				r.setDrawingReflection(true);
				r.draw(batch, tileAbove, above);
				r.setDrawingReflection(false);
			}
		}
		
		batch.setShader(shader);
		shader.setUniformf("u_waveAmplitude", s.getWaveAmplitude());
		shader.setUniformf("u_waveFrequency", s.getWaveFrequency());
		shader.setUniformf("u_timeScale", s.getWaveTimeScale());
		
		final float time = TimeUtils.timeSinceMillis(JRogue.START_TIME) / 1000.0f;
		shader.setUniformf("u_time", time);
		
		AnimationProvider animationProvider = renderer.getEntityComponent().getAnimationProvider();
		
		if (level.getClimate() != Climate.__) {
			level.entityStore.getEntitiesAt(above)
				.filter(e -> EntityMap.getRenderer(e.getAppearance()) != null)
				.filter(e -> EntityMap.getRenderer(e.getAppearance()).shouldBeReflected(e))
				.filter(e -> e.isStatic() || !e.getLevel().visibilityStore.isTileInvisible(e.getPosition()))
				.sorted(Comparator.comparingInt(Entity::getDepth))
				.forEach(e -> {
					EntityRenderer r = EntityMap.getRenderer(e.getAppearance());
					r.setDrawingReflection(true);
					r.draw(batch, e, animationProvider.getEntityAnimationData(e), true);
					r.setDrawingReflection(false);
				});
		}
		
		batch.setShader(oldShader);
	}
}
