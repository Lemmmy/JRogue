package pw.lemmmy.jrogue.rendering.gdx;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.rendering.Renderer;
import pw.lemmmy.jrogue.rendering.gdx.tiles.TileMap;
import pw.lemmmy.jrogue.rendering.gdx.tiles.TileRenderer;
import pw.lemmmy.jrogue.rendering.gdx.utils.FontLoader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GDXRenderer extends ApplicationAdapter implements Renderer, Dungeon.Listener {
	private static final String WINDOW_TITLE = "JRogue";

	private LwjglApplication application;
	private SpriteBatch batch;
	private ShapeRenderer lightBatch;
	private SpriteBatch hudBatch;
	private OrthographicCamera camera;

	private Dungeon dungeon;

	private boolean drawLights = true;

	private List<ParticleEffectPool.PooledEffect> pooledEffects = new ArrayList<>();

	public GDXRenderer(Dungeon dungeon) {
		this.dungeon = dungeon;
		this.dungeon.addListener(this);

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		application = new LwjglApplication(this, config);
	}

	private void updateWindowTitle() {
		Gdx.graphics.setTitle(WINDOW_TITLE + " - " + dungeon.getName());
	}

	@Override
	public void create() {
		super.create();

		updateWindowTitle();

		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.setToOrtho(true, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		camera.update();

		batch = new SpriteBatch();
		lightBatch = new ShapeRenderer();
		hudBatch = new SpriteBatch();

		onLevelChange(dungeon.getLevel());
	}

	@Override
	public void render() {
		super.render();

		float delta = Gdx.graphics.getDeltaTime();

		handleInput();

		camera.update();

		batch.setProjectionMatrix(camera.combined);
		lightBatch.setProjectionMatrix(camera.combined);
		hudBatch.setProjectionMatrix(camera.combined);

		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.begin();
		batch.enableBlending();

		for (int y = 0; y < dungeon.getLevel().getHeight(); y++) {
			for (int x = 0; x < dungeon.getLevel().getWidth(); x++) {
				TileMap tm = TileMap.valueOf(dungeon.getLevel().getTile(x, y).name());

				if (tm.getRenderer() != null) {
					tm.getRenderer().draw(batch, dungeon, x, y);
				}
			}
		}

		for (Iterator<ParticleEffectPool.PooledEffect> iterator = pooledEffects.iterator(); iterator.hasNext(); ) {
			ParticleEffectPool.PooledEffect effect = iterator.next();

			effect.draw(batch, delta * 0.25f);

			if (effect.isComplete()) {
				effect.free();
				iterator.remove();
			}
		}

		batch.end();

		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_DST_COLOR, GL20.GL_ONE_MINUS_SRC_ALPHA);

		if (drawLights) {
			lightBatch.begin(ShapeRenderer.ShapeType.Filled);

			for (int y = 0; y < dungeon.getLevel().getHeight(); y++) {
				for (int x = 0; x < dungeon.getLevel().getWidth(); x++) {
					TileMap tm = TileMap.valueOf(dungeon.getLevel().getTile(x, y).name());

					if (tm.getRenderer() != null) {
						tm.getRenderer().drawLight(lightBatch, dungeon, x, y);
					}
				}
			}

			lightBatch.end();

			Gdx.gl.glDisable(GL20.GL_BLEND);
		}

		hudBatch.begin();

		FontLoader.getFont("PixelOperator.ttf", 16).draw(hudBatch, pooledEffects.size() + " pooled particles", 2, 4);

		hudBatch.end();
	}

	private void handleInput() {
		if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
			dungeon.generateLevel();
		}

		if (Gdx.input.isKeyJustPressed(Input.Keys.N)) {
			dungeon.rerollName();
			updateWindowTitle();
		}

		if (Gdx.input.isKeyJustPressed(Input.Keys.L)) {
			drawLights = !drawLights;
		}
	}

	@Override
	public void dispose() {
		super.dispose();
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);

		camera.setToOrtho(true, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

	@Override
	public void onLevelChange(Level level) {
		for (ParticleEffectPool.PooledEffect effect : pooledEffects) {
			effect.free();
		}

		pooledEffects.clear();

		for (int y = 0; y < dungeon.getLevel().getHeight(); y++) {
			for (int x = 0; x < dungeon.getLevel().getWidth(); x++) {
				TileMap tm = TileMap.valueOf(dungeon.getLevel().getTile(x, y).name());

				if (tm.getRenderer() == null) {
					continue;
				}

				TileRenderer renderer = tm.getRenderer();

				if (renderer.getParticleEffectPool() == null) {
					continue;
				}

				ParticleEffectPool.PooledEffect effect = renderer.getParticleEffectPool().obtain();

				effect.setPosition(
					(x * TileMap.TILE_WIDTH) + renderer.getParticleXOffset(),
					(y * TileMap.TILE_HEIGHT) + renderer.getParticleYOffset()
				);

				pooledEffects.add(effect);
			}
		}
	}

	@Override
	public void onTurn() {

	}

	@Override
	public void onLog(String log) {

	}
}
