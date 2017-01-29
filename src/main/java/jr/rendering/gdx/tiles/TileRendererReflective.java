package jr.rendering.gdx.tiles;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.TimeUtils;
import jr.JRogue;
import jr.dungeon.Dungeon;
import jr.dungeon.entities.Entity;
import jr.rendering.gdx.entities.EntityMap;
import jr.rendering.gdx.entities.EntityRenderer;
import jr.rendering.gdx.utils.ShaderLoader;
import lombok.Getter;
import lombok.NonNull;

import java.util.Comparator;
import java.util.List;

public class TileRendererReflective extends TileRendererBasic {
	public static void drawReflection(SpriteBatch batch, Dungeon dungeon, int x, int y, @NonNull ReflectionSettings s) {
		if (y - 1 < 0) return;
		
		final ShaderProgram reflectionShader = ShaderLoader.getProgram("shaders/reflection");
		
		List<Entity> entities = dungeon.getLevel().getEntityStore().getEntitiesAt(x, y - 1);
		entities.stream()
			.sorted(Comparator.comparingInt(Entity::getDepth))
			.filter(e -> EntityMap.getRenderer(e.getAppearance()) != null)
			.filter(e -> EntityMap.getRenderer(e.getAppearance()).shouldBeReflected(e))
			.forEach(e -> {
				EntityRenderer renderer = EntityMap.getRenderer(e.getAppearance());
				ShaderProgram oldShader = batch.getShader();
				
				batch.setShader(reflectionShader);
				float time = TimeUtils.timeSinceMillis(JRogue.START_TIME) / 1000.0f;
				
				reflectionShader.setUniformf("u_time", time);
				reflectionShader.setUniformf("u_waveAmplitude", s.getWaveAmplitude());
				reflectionShader.setUniformf("u_waveFrequency", s.getWaveFrequency());
				reflectionShader.setUniformf("u_timeScale", s.getWaveTimeScale());
				reflectionShader.setUniformf("u_fadeAmplitude", s.getFadeAmplitude());
				reflectionShader.setUniformf("u_fadeBase", s.getFadeBase());
				
				renderer.setDrawingReflection(true);
				renderer.draw(batch, dungeon, e);
				renderer.setDrawingReflection(false);
				
				batch.setShader(oldShader);
			});
	}
	
	private final ReflectionSettings settings;
	
	public TileRendererReflective(int sheetX, int sheetY, @NonNull ReflectionSettings settings) {
		this("textures/tiles.png", sheetX, sheetY, settings);
	}
	
	public TileRendererReflective(@NonNull String sheetName, int sheetX, int sheetY, @NonNull ReflectionSettings settings) {
		super(sheetName, sheetX, sheetY);
		this.settings = settings;
	}
	
	@Override
	public void draw(SpriteBatch batch, Dungeon dungeon, int x, int y) {
		super.draw(batch, dungeon, x, y);
		drawReflection(batch, dungeon, x, y, settings);
	}
}
