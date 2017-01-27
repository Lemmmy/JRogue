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
import com.badlogic.gdx.math.Matrix4;
import jr.ErrorHandler;
import jr.Settings;
import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.Entity;
import jr.rendering.Renderer;
import jr.rendering.gdx.components.LevelComponent;
import jr.rendering.gdx.components.LightingComponent;
import jr.rendering.gdx.components.MinimapComponent;
import jr.rendering.gdx.components.RendererComponent;
import jr.rendering.gdx.components.hud.HUDComponent;
import jr.rendering.gdx.entities.EntityMap;
import jr.rendering.gdx.entities.EntityPooledEffect;
import jr.rendering.gdx.entities.EntityRenderer;
import jr.rendering.gdx.tiles.TileMap;
import jr.rendering.gdx.utils.FontLoader;
import jr.rendering.gdx.utils.ImageLoader;
import jr.rendering.gdx.utils.ShaderLoader;
import jr.utils.Gradient;
import jr.utils.Path;
import org.apache.logging.log4j.LogManager;

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
	
	private Dungeon dungeon;
	private Settings settings;
	
	private SpriteBatch mainBatch;
	
	private OrthographicCamera camera;
	
	private List<RendererComponent> rendererComponents = new ArrayList<>();
	
	private HUDComponent hudComponent;
	private MinimapComponent minimapComponent;
	private LevelComponent levelComponent;
	private LightingComponent lightingComponent;
	
	private List<EntityPooledEffect> entityPooledEffects = new ArrayList<>();
	
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
		
		initialiseCamera();
		
		mainBatch = new SpriteBatch();
		
		loadPathSprites();
		
		initialiseRendererComponents();
		initialiseInputMultiplexer();
		
		onLevelChange(dungeon.getLevel());
		rendererComponents.forEach(r -> r.onLevelChange(dungeon.getLevel()));
		dungeon.start();
	}
	
	private void initialiseCamera() {
		zoom = 24 * TileMap.TILE_WIDTH;
		
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.setToOrtho(true, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		camera.viewportWidth = Math.round(zoom);
		camera.viewportHeight = Math.round(zoom * Gdx.graphics.getHeight() / Gdx.graphics.getWidth());
		
		camera.update();
	}
	
	private void initialiseRendererComponents() {
		hudComponent = new HUDComponent(this, dungeon, settings);
		minimapComponent = new MinimapComponent(this, dungeon, settings);
		lightingComponent = new LightingComponent(this, dungeon, settings);
		levelComponent = new LevelComponent(this, dungeon, settings);
		
		rendererComponents.add(hudComponent);
		rendererComponents.add(minimapComponent);
		rendererComponents.add(lightingComponent);
		rendererComponents.add(levelComponent);
		
		// add mod components
		
		rendererComponents.sort(Comparator.comparingInt(RendererComponent::getZIndex));
		
		rendererComponents.forEach(r -> r.setCamera(camera));
		rendererComponents.forEach(RendererComponent::initialise);
	}
	
	private void initialiseInputMultiplexer() {
		InputMultiplexer inputMultiplexer = new InputMultiplexer();
		inputMultiplexer.addProcessor(new GameInputProcessor(dungeon, this));
		inputMultiplexer.addProcessor(hudComponent.getStage());
		Gdx.input.setInputProcessor(inputMultiplexer);
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
		lastPath = null;
		rendererComponents.forEach(r -> r.onLevelChange(level));
	}
	
	@Override
	public void onTurn(long turn) {
		updateWindowTitle();
		lastPath = null;
		rendererComponents.forEach(r -> r.onTurn(turn));
	}
	
	@Override
	public void onPathShow(Path path) {
		lastPath = path;
		rendererComponents.forEach(r -> r.onPathShow(path));
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

		rendererComponents.forEach(r -> r.onEntityAdded(entity));
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

		rendererComponents.forEach(r -> r.onEntityMoved(entity, lastX, lastY, newX, newY));
	}
	
	@Override
	public void onEntityRemoved(Entity entity) {
		entityPooledEffects.removeIf(e -> e.getEntity().equals(entity));
		rendererComponents.forEach(r -> r.onEntityRemoved(entity));
	}
	
	@Override
	public void onQuit() {
		dontSave = true;
		rendererComponents.forEach(Dungeon.Listener::onQuit);
		application.exit();
	}
	
	@Override
	public void onSaveAndQuit() {
		rendererComponents.forEach(Dungeon.Listener::onSaveAndQuit);
		application.exit();
	}
	
	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		
		camera.setToOrtho(true, width, height);
		
		camera.viewportWidth = Math.round(zoom);
		camera.viewportHeight = Math.round(zoom * height / width);
		
		rendererComponents.forEach(r -> r.resize(width, height));
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
		
		float delta = Gdx.graphics.getDeltaTime();
		
		updateCamera();
		
		rendererComponents.forEach(r -> r.update(delta));
		
		mainBatch.setProjectionMatrix(camera.combined);
		
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		mainBatch.begin();
		mainBatch.enableBlending();
		
		rendererComponents.stream()
			.filter(RendererComponent::useMainBatch)
			.forEach(r -> r.render(delta));
		
		drawLastPath();
		drawEntityParticles(delta, false);
		drawEntities();
		drawEntityParticles(delta, true);
		
		mainBatch.end();
		
		rendererComponents.stream()
			.filter(r -> !r.useMainBatch())
			.forEach(r -> r.render(delta));
	}
	
	private void drawLastPath() {
		if (lastPath == null) {
			return;
		}
		
		Color oldColour = mainBatch.getColor();
		
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
			
			mainBatch.setColor(PATH_GRADIENT.getColourAtPoint(point));
			mainBatch.draw(image, step.getX() * TileMap.TILE_WIDTH + 0.01f, step.getY() * TileMap.TILE_HEIGHT + 0.01f);
		});
		
		mainBatch.setColor(oldColour);
	}

	public Matrix4 getCombinedTransform() {
		return camera.combined;
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
			
			if (dungeon.getLevel().getVisibilityStore().isTileInvisible(effect.getEntity().getX(), effect.getEntity().getY())) {
				continue;
			}
			
			effect.getPooledEffect().draw(mainBatch);
			
			if (effect.getPooledEffect().isComplete()) {
				effect.getPooledEffect().free();
				iterator.remove();
			}
		}
	}
	
	private void drawEntities() {
		dungeon.getLevel().getEntityStore().getEntities().stream()
			.sorted(Comparator.comparingInt(Entity::getDepth))
			.forEach(e -> {
				if (!e.isStatic() && dungeon.getLevel().getVisibilityStore().isTileInvisible(e.getX(), e.getY())) {
					return;
				}
				
				EntityMap em = EntityMap.valueOf(e.getAppearance().name());
				
				if (em.getRenderer() != null) {
					em.getRenderer().draw(mainBatch, dungeon, e);
				}
			});
	}
	
	@Override
	public void dispose() {
		super.dispose();
		
		if (settings.shouldAutosave() && !dontSave && dungeon.getPlayer().isAlive()) {
			dungeon.save();
		}
		
		mainBatch.dispose();

		rendererComponents.forEach(RendererComponent::dispose);
		
		ImageLoader.disposeAll();
		FontLoader.disposeAll();
		ShaderLoader.disposeAll();
		LogManager.shutdown();
	}
	
	public Pixmap takeLevelSnapshot() {
//		int levelWidth = dungeon.getLevel().getWidth() * TileMap.TILE_WIDTH;
//		int levelHeight = dungeon.getLevel().getHeight() * TileMap.TILE_HEIGHT;
//
//		FrameBuffer fbo = new FrameBuffer(Pixmap.Format.RGBA8888, levelWidth, levelHeight, false, false);
//		Camera fullCamera = new OrthographicCamera(levelWidth, levelHeight);
//		fullCamera.position.set(levelWidth / 2.0f, levelHeight / 2.0f, 0.0f);
//		fullCamera.update();
//
//		fbo.begin();
//		mainBatch.setProjectionMatrix(fullCamera.combined);
//		mainBatch.enableBlending();
//		mainBatch.begin();
//
//		drawMap(true, false);
//		drawMap(true, true);
//		drawEntities(true);
//
//		mainBatch.end();
//
//		Pixmap pixmap = ScreenUtils.getFrameBufferPixmap(0, 0, levelWidth, levelHeight);
//
//		fbo.end();
//		fbo.dispose();
//		return pixmap;
		
		Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
		pixmap.setColor(Color.WHITE);
		pixmap.fill();
		return pixmap;
	}
	
	public HUDComponent getHUDComponent() {
		return hudComponent;
	}
	
	public MinimapComponent getMinimapComponent() {
		return minimapComponent;
	}
	
	public LightingComponent getLightingComponent() {
		return lightingComponent;
	}
	
	public LevelComponent getLevelComponent() {
		return levelComponent;
	}
	
	public OrthographicCamera getCamera() {
		return camera;
	}
	
	public SpriteBatch getMainBatch() {
		return mainBatch;
	}
	
	@Override
	public void panic() {
		Gdx.app.exit();
	}
}
