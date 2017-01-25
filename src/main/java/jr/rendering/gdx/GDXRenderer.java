package jr.rendering.gdx;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import jr.Settings;
import jr.rendering.Renderer;
import jr.rendering.gdx.entities.EntityMap;
import jr.rendering.gdx.hud.Minimap;
import jr.rendering.gdx.hud.windows.*;
import jr.rendering.gdx.tiles.TileMap;
import jr.rendering.gdx.tiles.TilePooledEffect;
import jr.rendering.gdx.utils.FontLoader;
import jr.rendering.gdx.utils.ImageLoader;
import jr.utils.Gradient;
import jr.utils.Path;
import org.apache.logging.log4j.LogManager;
import jr.ErrorHandler;
import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.Entity;
import jr.rendering.gdx.entities.EntityPooledEffect;
import jr.rendering.gdx.entities.EntityRenderer;
import jr.rendering.gdx.hud.HUD;
import jr.rendering.gdx.tiles.TileRenderer;
import jr.rendering.gdx.utils.ShaderLoader;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class GDXRenderer extends ApplicationAdapter implements Renderer, Dungeon.Listener {
	private static final String WINDOW_TITLE = "JRogue";
	
	private static final Gradient PATH_GRADIENT = Gradient.getGradient(
		Color.GREEN,
		Color.RED
	);
	
	private Lwjgl3Application application;
	
	private HUD hud;
	private Minimap minimap;
	
	private SpriteBatch batch;
	private ShapeRenderer lightBatch;
	private SpriteBatch lightSpriteBatch;
	
	private OrthographicCamera camera;
	
	private Dungeon dungeon;
	private Settings settings;
	
	private List<Runnable> nextFrameDeferred = new ArrayList<>();
	
	private List<TilePooledEffect> tilePooledEffects = new ArrayList<>();
	private List<EntityPooledEffect> entityPooledEffects = new ArrayList<>();
	
	private List<PopupWindow> windows = new ArrayList<>();
	
	private Path lastPath = null;
	private TextureRegion pathSpot, pathH, pathV, pathUR, pathUL, pathBR, pathBL, pathR, pathL, pathU, pathB;
	
	private float zoom = 1.0f;
	
	private boolean dontSave = false;
	
	public GDXRenderer(Settings settings, Dungeon dungeon) {
		this.settings = settings;
		
		this.dungeon = dungeon;
		this.dungeon.addListener(this);
		
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setResizable(true);
		config.setWindowedMode(settings.getScreenWidth(), settings.getScreenHeight());
		
		new Lwjgl3Application(this, config);
	}
	
	@Override
	public void create() {
		application = (Lwjgl3Application) Gdx.app;
		
		Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
			ErrorHandler.error(null, throwable);
			Gdx.app.exit();
		});
		
		super.create();
		
		ErrorHandler.setGLString();
		
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
		
		loadPathSprites();
		
		hud = new HUD(this, settings, dungeon);
		hud.init();
		dungeon.addListener(hud);
		
		minimap = new Minimap(settings, dungeon);
		minimap.init();
		dungeon.addListener(minimap);
		
		InputMultiplexer inputMultiplexer = new InputMultiplexer();
		inputMultiplexer.addProcessor(new GameInputProcessor(dungeon, this));
		inputMultiplexer.addProcessor(hud.getStage());
		Gdx.input.setInputProcessor(inputMultiplexer);
		
		onLevelChange(dungeon.getLevel());
		hud.onLevelChange(dungeon.getLevel());
		minimap.onLevelChange(dungeon.getLevel());
		dungeon.start();
	}
	
	private void loadPathSprites() {
		pathSpot = ImageLoader.getImageFromSheet("hud.png", 6, 0);
		pathH = ImageLoader.getImageFromSheet("hud.png", 7, 0);
		pathV = ImageLoader.getImageFromSheet("hud.png", 8, 0);
		pathUR = ImageLoader.getImageFromSheet("hud.png", 9, 0);
		pathUL = ImageLoader.getImageFromSheet("hud.png", 10, 0);
		pathBR = ImageLoader.getImageFromSheet("hud.png", 11, 0);
		pathBL = ImageLoader.getImageFromSheet("hud.png", 12, 0);
		pathR = ImageLoader.getImageFromSheet("hud.png", 13, 0);
		pathL = ImageLoader.getImageFromSheet("hud.png", 14, 0);
		pathU = ImageLoader.getImageFromSheet("hud.png", 15, 0);
		pathB = ImageLoader.getImageFromSheet("hud.png", 16, 0);
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
		entityPooledEffects.clear();
		findTilePooledParticles();
		lastPath = null;
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
					x * TileMap.TILE_WIDTH + renderer.getParticleXOffset(),
					y * TileMap.TILE_HEIGHT + renderer.getParticleYOffset()
				);
				
				TilePooledEffect tilePooledEffect = new TilePooledEffect(x, y, effect);
				tilePooledEffects.add(tilePooledEffect);
			}
		}
	}
	
	@Override
	public void onTurn(long turn) {
		updateWindowTitle();
		lastPath = null;
	}
	
	@Override
	public void onContainerShow(Entity containerEntity) {
		nextFrameDeferred
			.add(() -> new ContainerWindow(GDXRenderer.this, hud.getStage(), hud.getSkin(), containerEntity)
				.show());
	}
	
	@Override
	public void onPathShow(Path path) {
		lastPath = path;
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
			entity.getX() * TileMap.TILE_WIDTH + renderer.getParticleXOffset(entity),
			entity.getY() * TileMap.TILE_HEIGHT + renderer.getParticleYOffset(entity)
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
					entity.getX() * TileMap.TILE_WIDTH + renderer.getParticleXOffset(entity),
					entity.getY() * TileMap.TILE_HEIGHT + renderer.getParticleYOffset(entity)
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
		dontSave = true;
		
		application.exit();
	}
	
	@Override
	public void onSaveAndQuit() {
		application.exit();
	}
	
	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		
		camera.setToOrtho(true, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		camera.viewportWidth = Math.round(zoom);
		camera.viewportHeight = Math.round(zoom * height / width);
		
		hud.updateViewport(width, height);
		
		minimap.resize();
	}
	
	public void updateCamera() {
		if (dungeon.getPlayer() != null) {
			camera.position.x = (dungeon.getPlayer().getX() + 0.5f) * TileMap.TILE_WIDTH;
			camera.position.y = dungeon.getPlayer().getY() * TileMap.TILE_HEIGHT;
		}
		
		camera.update();
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
		
		updateCamera();
		
		batch.setProjectionMatrix(camera.combined);
		lightBatch.setProjectionMatrix(camera.combined);
		lightSpriteBatch.setProjectionMatrix(camera.combined);
		
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		batch.begin();
		batch.enableBlending();
		
		drawMap();
		drawTileParticles(delta);
		drawLastPath();
		drawEntityParticles(delta, false);
		drawEntities(false);
		drawEntityParticles(delta, true);
		
		batch.end();
		
		drawLights();
		
		hud.updateAndDraw(delta);
		
		minimap.render();
	}
	
	private void drawMap() {
		drawMap(false, false);
		drawMap(false, true);
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
	
	private void drawLastPath() {
		if (lastPath == null) {
			return;
		}
		
		Color oldColour = batch.getColor();
		
		Path path = lastPath;
		AtomicInteger i = new AtomicInteger(0);
		
		path.forEach(step -> {
			i.incrementAndGet();
			
			TextureRegion image;
			
			boolean[] a = path.getAdjacentSteps(step.getX(), step.getY());

			/*
				 3
				1 0
				 2
			 */
			
			if (a[0] && !a[1] && !a[2] && !a[3]) {
				image = pathR;
			} else if (!a[0] && a[1] && !a[2] && !a[3]) {
				image = pathL;
			} else if (!a[0] && !a[1] && !a[2] && a[3]) {
				image = pathU;
			} else if (!a[0] && !a[1] && a[2] && !a[3]) {
				image = pathB;
			} else if (a[0] && a[1] && !a[2] && !a[3]) {
				image = pathH;
			} else if (!a[0] && !a[1] && a[2]) {
				image = pathV;
			} else if (!a[0] && a[1] && !a[2]) {
				image = pathUL;
			} else if (a[0] && !a[1] && !a[2]) {
				image = pathUR;
			} else if (!a[0] && a[1] && !a[3]) {
				image = pathBL;
			} else if (a[0] && !a[1] && !a[3]) {
				image = pathBR;
			} else {
				image = pathSpot;
			}
			
			float point = (float) (i.get() - 1) / (float) (path.getLength() - 1);
			
			batch.setColor(PATH_GRADIENT.getColourAtPoint(point));
			batch.draw(image, step.getX() * TileMap.TILE_WIDTH + 0.01f, step.getY() * TileMap.TILE_HEIGHT + 0.01f);
		});
		
		batch.setColor(oldColour);
	}
	
	private void drawEntityParticles(float delta, boolean over) {
		for (Iterator<EntityPooledEffect> iterator = entityPooledEffects.iterator(); iterator.hasNext(); ) {
			EntityPooledEffect effect = iterator.next();
			
			boolean shouldDrawParticles = effect.getRenderer().shouldDrawParticles(
				dungeon,
				effect.getEntity(),
				effect.getEntity().getX(),
				effect.getEntity().getY()
			);
			
			if (!shouldDrawParticles) {
				effect.getPooledEffect().free();
				continue;
			}
			
			if (effect.shouldDrawOver() != over) { continue; }
			
			float deltaMultiplier = effect.getRenderer().getParticleDeltaMultiplier(
				dungeon,
				effect.getEntity(),
				effect.getEntity().getX(),
				effect.getEntity().getY()
			);
			
			effect.getPooledEffect().update(delta * deltaMultiplier);
			
			if (dungeon.getLevel().isTileInvisible(effect.getEntity().getX(), effect.getEntity().getY())) {
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
				if (!allRevealed && !e.isStatic() && dungeon.getLevel().isTileInvisible(e.getX(), e.getY())) {
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
	
	@Override
	public void dispose() {
		super.dispose();
		
		if (settings.shouldAutosave() && !dontSave && dungeon.getPlayer().isAlive()) {
			dungeon.save();
		}
		
		batch.dispose();
		lightBatch.dispose();
		lightSpriteBatch.dispose();
		
		hud.dispose();
		
		tilePooledEffects.forEach(e -> e.getPooledEffect().free());
		
		ImageLoader.disposeAll();
		FontLoader.disposeAll();
		ShaderLoader.disposeAll();
		LogManager.shutdown();
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
		
		Pixmap pixmap = ScreenUtils.getFrameBufferPixmap(0, 0, levelWidth, levelHeight);
		
		fbo.end();
		fbo.dispose();
		return pixmap;
	}
	
	public void showDebugWindow() {
		nextFrameDeferred
			.add(() -> new DebugWindow(GDXRenderer.this, hud.getStage(), hud.getSkin(), dungeon, dungeon.getLevel())
				.show());
	}
	
	public void showInventoryWindow() {
		nextFrameDeferred
			.add(() -> new PlayerWindow(GDXRenderer.this, hud.getStage(), hud.getSkin(), dungeon.getPlayer())
				.show());
	}
	
	public void showWishWindow() {
		nextFrameDeferred
			.add(() -> new WishWindow(GDXRenderer.this, hud.getStage(), hud.getSkin(), dungeon, dungeon.getLevel())
				.show());
	}
	
	public void showSpellWindow() {
		nextFrameDeferred
			.add(() -> new SpellWindow(GDXRenderer.this, hud.getStage(), hud.getSkin(), dungeon.getPlayer())
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
	
	@Override
	public void panic() {
		Gdx.app.exit();
	}
}
