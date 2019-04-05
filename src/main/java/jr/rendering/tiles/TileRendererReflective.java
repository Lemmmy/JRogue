package jr.rendering.tiles;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.TimeUtils;
import jr.JRogue;
import jr.dungeon.Dungeon;
import jr.dungeon.entities.Entity;
import jr.dungeon.generators.Climate;
import jr.dungeon.tiles.TileFlag;
import jr.dungeon.tiles.TileType;
import jr.rendering.entities.EntityMap;
import jr.rendering.entities.EntityRenderer;
import jr.rendering.entities.animations.AnimationProvider;
import jr.rendering.screens.GameScreen;
import jr.rendering.utils.ShaderLoader;
import lombok.NonNull;

import java.util.Comparator;
import java.util.List;

public class TileRendererReflective extends TileRendererBasic {
	public static void drawReflection(SpriteBatch batch, GameScreen renderer, Dungeon dungeon, int x, int y, @NonNull
		ReflectionSettings s) {
		if (y + 1 < 0) return;
		
		ShaderProgram oldShader = batch.getShader();
		final ShaderProgram reflectionShader = ShaderLoader.getProgram("shaders/reflection");
		
		batch.setShader(reflectionShader);
		reflectionShader.setUniformf("u_waveAmplitude", 0.0f);
		reflectionShader.setUniformf("u_waveFrequency", 0.0f);
		reflectionShader.setUniformf("u_timeScale", 0.0f);
		reflectionShader.setUniformf("u_fadeAmplitude", s.getFadeAmplitude());
		reflectionShader.setUniformf("u_fadeBase", s.getFadeBase());
		
		Vector3 tps1 = renderer.getCamera().project(new Vector3(x * TileMap.TILE_WIDTH, y * TileMap.TILE_HEIGHT, 0.0f));
		Vector3 tps2 = renderer.getCamera().project(new Vector3((x + 1) * TileMap.TILE_WIDTH, (y + 1) * TileMap.TILE_HEIGHT, 0.0f));
		reflectionShader.setUniformf("u_tilePositionScreen", tps1.x, tps1.y);
		reflectionShader.setUniformf("u_tileSizeScreen", tps2.x - tps1.x, tps2.y - tps1.y);
		
		reflectionShader.setUniformf("u_time", 0.0f);
		
		TileType tileAbove = dungeon.getLevel().tileStore.getTileType(x, y + 1);
		
		final boolean doReflect = (tileAbove.getFlags() & TileFlag.DONT_REFLECT) != TileFlag.DONT_REFLECT;
		final boolean isWall = (tileAbove.getFlags() & TileFlag.WALL) == TileFlag.WALL;
		
		if (doReflect && isWall) {
			TileRenderer r = TileMap.valueOf(tileAbove.name()).getRenderer();
			r.setDrawingReflection(true);
			r.draw(batch, dungeon, x, y + 1);
			r.setDrawingReflection(false);
		}
		
		batch.setShader(reflectionShader);
		reflectionShader.setUniformf("u_waveAmplitude", s.getWaveAmplitude());
		reflectionShader.setUniformf("u_waveFrequency", s.getWaveFrequency());
		reflectionShader.setUniformf("u_timeScale", s.getWaveTimeScale());
		
		final float time = TimeUtils.timeSinceMillis(JRogue.START_TIME) / 1000.0f;
		reflectionShader.setUniformf("u_time", time);
		
		AnimationProvider animationProvider = renderer.getEntityComponent().getAnimationProvider();
		
		List<Entity> entities = dungeon.getLevel().entityStore.getEntitiesAt(x, y + 1);
		entities.stream()
			.sorted(Comparator.comparingInt(Entity::getDepth))
			.filter(e -> EntityMap.getRenderer(e.getAppearance()) != null)
			.filter(e -> EntityMap.getRenderer(e.getAppearance()).shouldBeReflected(e))
			.forEach(e -> {
				if (
					!e.isStatic() &&
					e.getLevel().visibilityStore.isTileInvisible(e.getPosition()) &&
					!(e.getLevel().getClimate() == Climate.__)
				) {
					return;
				}
				
				EntityRenderer r = EntityMap.getRenderer(e.getAppearance());
				r.setDrawingReflection(true);
				r.draw(batch, dungeon, e, animationProvider.getEntityAnimationData(e), true);
				r.setDrawingReflection(false);
			});
		
		batch.setShader(oldShader);
	}
	
	private final ReflectionSettings settings;
	
	public TileRendererReflective(String fileName, @NonNull ReflectionSettings settings) {
		super(fileName);
		this.settings = settings;
	}
	
	@Override
	public void draw(SpriteBatch batch, Dungeon dungeon, int x, int y) {
		super.draw(batch, dungeon, x, y);
		drawReflection(batch, renderer, dungeon, x, y, settings);
	}
}
