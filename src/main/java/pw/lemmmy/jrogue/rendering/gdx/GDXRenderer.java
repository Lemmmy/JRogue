package pw.lemmmy.jrogue.rendering.gdx;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import pw.lemmmy.jrogue.Settings;
import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.Prompt;
import pw.lemmmy.jrogue.dungeon.entities.Entity;
import pw.lemmmy.jrogue.rendering.Renderer;
import pw.lemmmy.jrogue.rendering.gdx.entities.EntityMap;
import pw.lemmmy.jrogue.rendering.gdx.entities.EntityPooledEffect;
import pw.lemmmy.jrogue.rendering.gdx.entities.EntityRenderer;
import pw.lemmmy.jrogue.rendering.gdx.tiles.TileMap;
import pw.lemmmy.jrogue.rendering.gdx.tiles.TilePooledEffect;
import pw.lemmmy.jrogue.rendering.gdx.tiles.TileRenderer;
import pw.lemmmy.jrogue.rendering.gdx.utils.FontLoader;
import pw.lemmmy.jrogue.rendering.gdx.utils.ImageLoader;
import pw.lemmmy.jrogue.rendering.gdx.windows.DebugWindow;
import pw.lemmmy.jrogue.rendering.gdx.windows.InventoryWindow;
import pw.lemmmy.jrogue.rendering.gdx.windows.PopupWindow;
import pw.lemmmy.jrogue.rendering.gdx.windows.WishWindow;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class GDXRenderer extends ApplicationAdapter implements Renderer, Dungeon.Listener {
	private static final String WINDOW_TITLE = "JRogue";

	private LwjglApplication application;

	private HUD hud;

	private SpriteBatch batch;
	private ShapeRenderer lightBatch;
	private SpriteBatch lightSpriteBatch;

	private OrthographicCamera camera;

	private Dungeon dungeon;
	private Settings settings;

	private List<Runnable> nextFrameDeferred = new ArrayList<>();

	private List<TilePooledEffect> tilePooledEffects = new ArrayList<>();
	private List<EntityPooledEffect> entityPooledEffects = new ArrayList<>(); // TODO: Below and above

	private List<PopupWindow> windows = new ArrayList<>();

	private float zoom = 1.0f;

	public GDXRenderer(Settings settings, Dungeon dungeon) {
		this.settings = settings;

		this.dungeon = dungeon;
		this.dungeon.addListener(this);

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = settings.getScreenWidth();
		config.height = settings.getScreenHeight();
		config.forceExit = false;
		application = new LwjglApplication(this, config);
	}

	@Override
	public void create() {
		super.create();

		updateWindowTitle();

		zoom = 24 * TileMap.TILE_WIDTH;

		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.setToOrtho(true, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		camera.viewportWidth = Math.round(zoom);
		camera.viewportHeight = Math.round(zoom * Gdx.graphics.getHeight() / Gdx.graphics.getWidth());

		camera.update();

		batch = new SpriteBatch();
		lightBatch = new ShapeRenderer();
		lightSpriteBatch = new SpriteBatch();

		hud = new HUD(settings, dungeon);
		hud.init();
		dungeon.addListener(hud);

		InputMultiplexer inputMultiplexer = new InputMultiplexer();
		inputMultiplexer.addProcessor(new GameInputProcessor(dungeon, this));
		inputMultiplexer.addProcessor(hud.getStage());
		Gdx.input.setInputProcessor(inputMultiplexer);

		onLevelChange(dungeon.getLevel());
		dungeon.start();
	}

	private void updateWindowTitle() {
		Gdx.graphics.setTitle(String.format(
			"%s - %s - Turn %,d",
			WINDOW_TITLE,
			dungeon.getName(),
			dungeon.getTurn()
		));
	}

	@Override
	public void onLevelChange(Level level) {
		findTilePooledParticles();
	}

	private void findTilePooledParticles() {
		tilePooledEffects.forEach(e -> e.getPooledEffect().free());
		tilePooledEffects.clear();

		for (int y = 0; y < dungeon.getLevel().getHeight(); y++) {
			for (int x = 0; x < dungeon.getLevel().getWidth(); x++) {
				TileMap tm = TileMap.valueOf(dungeon.getLevel().getTileType(x, y).name());

				if (tm.getRenderer() == null) {
					continue;
				}

				TileRenderer renderer = tm.getRenderer();

				if (renderer.getParticleEffectPool() == null || !renderer.shouldDrawParticles(dungeon, x, y)) {
					continue;
				}

				ParticleEffectPool.PooledEffect effect = renderer.getParticleEffectPool().obtain();

				effect.setPosition(
					(x * TileMap.TILE_WIDTH) + renderer.getParticleXOffset(),
					(y * TileMap.TILE_HEIGHT) + renderer.getParticleYOffset()
				);

				TilePooledEffect tilePooledEffect = new TilePooledEffect(x, y, effect);
				tilePooledEffects.add(tilePooledEffect);
			}
		}
	}

	@Override
	public void onBeforeTurn(long turn) {
	}

	@Override
	public void onTurn(long turn) {
		updateWindowTitle();
	}

	@Override
	public void onLog(String entry) {
	}

	@Override
	public void onPrompt(Prompt prompt) {
	}

	@Override
	public void onEntityAdded(Entity entity) {
		EntityMap em = EntityMap.valueOf(entity.getAppearance().name());

		if (em.getRenderer() == null) {
			return;
		}

		EntityRenderer renderer = em.getRenderer();

		if (renderer.getParticleEffectPool(entity) == null) {
			return;
		}

		ParticleEffectPool.PooledEffect effect = renderer.getParticleEffectPool(entity).obtain();

		effect.setPosition(
			(entity.getX() * TileMap.TILE_WIDTH) + renderer.getParticleXOffset(entity),
			(entity.getY() * TileMap.TILE_HEIGHT) + renderer.getParticleYOffset(entity)
		);

		boolean over = renderer.shouldDrawParticlesOver(dungeon, entity, entity.getX(), entity.getY());

		EntityPooledEffect entityPooledEffect = new EntityPooledEffect(
			entity,
			renderer,
			entity.getX(),
			entity.getY(),
			over,
			effect
		);
		entityPooledEffects.add(entityPooledEffect);
	}

	@Override
	public void onEntityMoved(Entity entity, int lastX, int lastY, int newX, int newY) {
		for (EntityPooledEffect e : entityPooledEffects) {
			if (e.getEntity() == entity) {
				EntityMap em = EntityMap.valueOf(entity.getAppearance().name());

				if (em.getRenderer() == null) {
					return;
				}

				EntityRenderer renderer = em.getRenderer();

				if (renderer.getParticleEffectPool(entity) == null) {
					return;
				}

				e.getPooledEffect().setPosition(
					(entity.getX() * TileMap.TILE_WIDTH) + renderer.getParticleXOffset(entity),
					(entity.getY() * TileMap.TILE_HEIGHT) + renderer.getParticleYOffset(entity)
				);
			}
		}
	}

	@Override
	public void onEntityRemoved(Entity entity) {
		entityPooledEffects.removeIf(e -> e.getEntity().equals(entity));
	}

	@Override
	public void onQuit() {
		application.exit();
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);

		camera.setToOrtho(true, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		camera.viewportWidth = Math.round(zoom);
		camera.viewportHeight = Math.round(zoom * height / width);

		hud.updateViewport(width, height);
	}

	@Override
	public void render() {
		super.render();

		for (Iterator<Runnable> iterator = nextFrameDeferred.iterator(); iterator.hasNext(); ) {
			Runnable r = iterator.next();
			r.run();
			iterator.remove();
		}

		float delta = Gdx.graphics.getDeltaTime();

		if (dungeon.getPlayer() != null) {
			camera.position.x = (dungeon.getPlayer().getX() * TileMap.TILE_WIDTH) + (TileMap.TILE_WIDTH / 2);
			camera.position.y = dungeon.getPlayer().getY() * TileMap.TILE_HEIGHT;
		}

		camera.update();

		batch.setProjectionMatrix(camera.combined);
		lightBatch.setProjectionMatrix(camera.combined);
		lightSpriteBatch.setProjectionMatrix(camera.combined);

		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.begin();
		batch.enableBlending();

		drawMap();
		drawTileParticles(delta);
		drawEntityParticles(delta, false);
		drawEntities(false);
		drawEntityParticles(delta, true);

		batch.end();

		drawLights();

		hud.updateAndDraw(delta);
	}

	private void drawMap() {
		drawMap(false, false);
		drawMap(false, true);
	}

	private void drawTileParticles(float delta) {
		for (Iterator<TilePooledEffect> iterator = tilePooledEffects.iterator(); iterator.hasNext(); ) {
			TilePooledEffect effect = iterator.next();

			effect.getPooledEffect().update(delta * 0.25f);

			if (!dungeon.getLevel().isTileDiscovered(effect.getX(), effect.getY())) {
				continue;
			}

			effect.getPooledEffect().draw(batch);

			if (effect.getPooledEffect().isComplete()) {
				effect.getPooledEffect().free();
				iterator.remove();
			}
		}
	}

	private void drawEntityParticles(float delta, boolean over) {
		for (Iterator<EntityPooledEffect> iterator = entityPooledEffects.iterator(); iterator.hasNext(); ) {
			EntityPooledEffect effect = iterator.next();

			if (effect.shouldDrawOver() != over) { continue; }

			float deltaMultiplier = effect.getRenderer().getParticleDeltaMultiplier(
				dungeon,
				effect.getEntity(),
				effect.getEntity().getX(),
				effect.getEntity().getY()
			);

			effect.getPooledEffect().update(delta * deltaMultiplier);

			if (!dungeon.getLevel().isTileVisible(effect.getEntity().getX(), effect.getEntity().getY()) ||
				!effect.getRenderer().shouldDrawParticles(
					dungeon,
					effect.getEntity(),
					effect.getEntity().getX(),
					effect.getEntity().getY()
				)) {
				continue;
			}

			effect.getPooledEffect().draw(batch);

			if (effect.getPooledEffect().isComplete()) {
				effect.getPooledEffect().free();
				iterator.remove();
			}
		}
	}

	private void drawEntities(boolean allRevealed) {
		dungeon.getLevel().getEntities().stream()
			   .sorted(Comparator.comparingInt(Entity::getDepth))
			   .forEach(e -> {
				   if (!allRevealed && !dungeon.getLevel().isTileVisible(e.getX(), e.getY())) {
					   return;
				   }

				   EntityMap em = EntityMap.valueOf(e.getAppearance().name());

				   if (em.getRenderer() != null) {
					   em.getRenderer().draw(batch, dungeon, e);
				   }
			   });
	}

	private void drawLights() {
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_DST_COLOR, GL20.GL_ONE_MINUS_SRC_ALPHA);

		lightBatch.begin(ShapeRenderer.ShapeType.Filled);

		for (int y = 0; y < dungeon.getLevel().getHeight(); y++) {
			for (int x = 0; x < dungeon.getLevel().getWidth(); x++) {
				TileMap tm = TileMap.valueOf(dungeon.getLevel().getTileType(x, y).name());

				if (tm.getRenderer() != null) {
					tm.getRenderer().drawLight(lightBatch, dungeon, x, y);
				}
			}
		}

		// Due to the light being drawn offset, we need additional tiles on the level borders.

		for (int y = 0; y < dungeon.getLevel().getHeight(); y++) {
			TileMap.TILE_GROUND.getRenderer().drawLight(lightBatch, dungeon, -1, y);
			TileMap.TILE_GROUND.getRenderer().drawLight(lightBatch, dungeon, dungeon.getLevel().getWidth() + 1, y);
		}

		for (int x = 0; x < dungeon.getLevel().getWidth(); x++) {
			TileMap.TILE_GROUND.getRenderer().drawLight(lightBatch, dungeon, x, -1);
			TileMap.TILE_GROUND.getRenderer().drawLight(lightBatch, dungeon, x, dungeon.getLevel().getHeight() + 1);
		}

		TileMap.TILE_GROUND.getRenderer().drawLight(lightBatch, dungeon, -1, -1);

		lightBatch.end();

		lightSpriteBatch.begin();

		for (int y = 0; y < dungeon.getLevel().getHeight(); y++) {
			for (int x = 0; x < dungeon.getLevel().getWidth(); x++) {
				TileMap tm = TileMap.valueOf(dungeon.getLevel().getTileType(x, y).name());

				if (tm.getRenderer() != null) {
					tm.getRenderer().drawDim(lightSpriteBatch, dungeon, x, y);
				}
			}
		}

		lightSpriteBatch.end();

		Gdx.gl.glDisable(GL20.GL_BLEND);
	}

	private void drawMap(boolean allRevealed, boolean extra) {
		for (int y = 0; y < dungeon.getLevel().getHeight(); y++) {
			for (int x = 0; x < dungeon.getLevel().getWidth(); x++) {
				if (!allRevealed && !dungeon.getLevel().isTileDiscovered(x, y)) {
					TileMap.TILE_GROUND.getRenderer().draw(batch, dungeon, x, y);
					continue;
				}

				TileMap tm = TileMap.valueOf(dungeon.getLevel().getTileType(x, y).name());

				if (tm.getRenderer() != null) {
					if (extra) {
						tm.getRenderer().drawExtra(batch, dungeon, x, y);
					} else {
						tm.getRenderer().draw(batch, dungeon, x, y);
					}
				}
			}
		}
	}

	@Override
	public void dispose() {
		super.dispose();

		batch.dispose();
		lightBatch.dispose();
		lightSpriteBatch.dispose();

		hud.dispose();

		tilePooledEffects.forEach(e -> e.getPooledEffect().free());

		ImageLoader.disposeAll();
		FontLoader.disposeAll();
	}

	public Pixmap takeLevelSnapshot() {
		int levelWidth = dungeon.getLevel().getWidth() * TileMap.TILE_WIDTH;
		int levelHeight = dungeon.getLevel().getHeight() * TileMap.TILE_HEIGHT;

		FrameBuffer fbo = new FrameBuffer(Pixmap.Format.RGBA8888, levelWidth, levelHeight, false, false);
		Camera fullCamera = new OrthographicCamera(levelWidth, levelHeight);
		fullCamera.position.set(levelWidth / 2.0f, levelHeight / 2.0f, 0.0f);
		fullCamera.update();

		fbo.begin();
		batch.setProjectionMatrix(fullCamera.combined);
		batch.enableBlending();
		batch.begin();

		drawMap(true, false);
		drawMap(true, true);
		drawEntities(true);

		batch.end();

		Pixmap pmap = ScreenUtils.getFrameBufferPixmap(0, 0, levelWidth, levelHeight);

		fbo.end();
		fbo.dispose();
		return pmap;
	}

	public void showDebugWindow() {
		nextFrameDeferred
			.add(() -> new DebugWindow(GDXRenderer.this, hud.getStage(), hud.getSkin(), dungeon, dungeon.getLevel())
				.show());
	}

	public void showInventoryWindow() {
		nextFrameDeferred
			.add(() -> new InventoryWindow(GDXRenderer.this, hud.getStage(), hud.getSkin(), dungeon, dungeon.getLevel())
				.show());
	}

	public void showWishWindow() {
		nextFrameDeferred
			.add(() -> new WishWindow(GDXRenderer.this, hud.getStage(), hud.getSkin(), dungeon, dungeon.getLevel())
				.show());
	}

	public OrthographicCamera getCamera() {
		return camera;
	}

	public void addWindow(PopupWindow window) {
		windows.add(window);
	}

	public void removeWindow(PopupWindow window) {
		windows.remove(window);
	}

	public List<PopupWindow> getWindows() {
		return windows;
	}
}
