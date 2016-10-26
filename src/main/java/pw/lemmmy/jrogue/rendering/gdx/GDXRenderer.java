package pw.lemmmy.jrogue.rendering.gdx;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import org.apache.commons.lang3.StringUtils;
import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.entities.Entity;
import pw.lemmmy.jrogue.dungeon.entities.Player;
import pw.lemmmy.jrogue.rendering.Renderer;
import pw.lemmmy.jrogue.rendering.gdx.entities.EntityMap;
import pw.lemmmy.jrogue.rendering.gdx.tiles.TileMap;
import pw.lemmmy.jrogue.rendering.gdx.tiles.TileRenderer;
import pw.lemmmy.jrogue.rendering.gdx.utils.FontLoader;

import java.util.*;

public class GDXRenderer extends ApplicationAdapter implements Renderer, Dungeon.Listener {
	private static final Map<Integer, Integer[]> MOVEMENT_KEYS = new HashMap<>();

	static {
		MOVEMENT_KEYS.put(Input.Keys.NUMPAD_1, new Integer[] {-1, 1});
		MOVEMENT_KEYS.put(Input.Keys.NUMPAD_2, new Integer[] {0, 1});
		MOVEMENT_KEYS.put(Input.Keys.NUMPAD_3, new Integer[] {1, 1});

		MOVEMENT_KEYS.put(Input.Keys.NUMPAD_4, new Integer[] {-1, 0});
		MOVEMENT_KEYS.put(Input.Keys.NUMPAD_6, new Integer[] {1, 0});

		MOVEMENT_KEYS.put(Input.Keys.NUMPAD_7, new Integer[] {-1, -1});
		MOVEMENT_KEYS.put(Input.Keys.NUMPAD_8, new Integer[] {0, -1});
		MOVEMENT_KEYS.put(Input.Keys.NUMPAD_9, new Integer[] {1, -1});
	}

	private static final String WINDOW_TITLE = "JRogue";

	private LwjglApplication application;
	private SpriteBatch batch;
	private ShapeRenderer lightBatch;
	private SpriteBatch hudBatch;

	private OrthographicCamera camera;
	private OrthographicCamera hudCamera;

	private Dungeon dungeon;

	private List<String> log = new ArrayList<>();

	private boolean drawLights = true;

	private List<ParticleEffectPool.PooledEffect> pooledEffects = new ArrayList<>();

	private float zoom = 1.0f;

	public GDXRenderer(Dungeon dungeon) {
		this.dungeon = dungeon;
		this.dungeon.addListener(this);

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 800;
		config.height = 640;
		application = new LwjglApplication(this, config);

		addColours();
	}

	private void addColours() {
		Colors.put("P_GREY_0", new Color(0x2e2e2eff));
		Colors.put("P_GREY_1", new Color(0x4d4d4dff));
		Colors.put("P_GREY_2", new Color(0x777777ff));
		Colors.put("P_GREY_3", new Color(0xacacacff));
		Colors.put("P_GREY_4", new Color(0xd4d4d4ff));

		Colors.put("P_RED", new Color(0xc91616ff));
		Colors.put("P_ORANGE_0", new Color(0xd0391bff));
		Colors.put("P_ORANGE_1", new Color(0xe0762fff));
		Colors.put("P_ORANGE_2", new Color(0xf8981bff));
		Colors.put("P_ORANGE_3", new Color(0xf8bc1bff));
		Colors.put("P_YELLOW", new Color(0xf8eb1bff));

		Colors.put("P_GREEN_0", new Color(0x1d7907ff));
		Colors.put("P_GREEN_1", new Color(0x2b9f10ff));
		Colors.put("P_GREEN_2", new Color(0x3bba1eff));
		Colors.put("P_GREEN_3", new Color(0x52d234ff));
		Colors.put("P_GREEN_4", new Color(0x85ed6dff));

		Colors.put("P_CYAN_0", new Color(0x047ca4ff));
		Colors.put("P_CYAN_1", new Color(0x28b5e3ff));

		Colors.put("P_BLUE_0", new Color(0x0b1b93ff));
		Colors.put("P_BLUE_1", new Color(0x0b4fb5ff));
		Colors.put("P_BLUE_2", new Color(0x3177e0ff));

		Colors.put("P_PURPLE_0", new Color(0x560670ff));
		Colors.put("P_PURPLE_1", new Color(0x720d93ff));
		Colors.put("P_PURPLE_2", new Color(0x8e25b1ff));
		Colors.put("P_PURPLE_3", new Color(0xae3fd2ff));

		Colors.put("P_PINK_0", new Color(0x77026dff));
		Colors.put("P_PINK_1", new Color(0x980c8cff));
		Colors.put("P_PINK_2", new Color(0xb81eabff));
		Colors.put("P_PINK_3", new Color(0xe13ed4ff));
		Colors.put("P_PINK_4", new Color(0xf356e6ff));
	}

	private void updateWindowTitle() {
		Gdx.graphics.setTitle(WINDOW_TITLE + " - " + dungeon.getName());
	}

	@Override
	public void create() {
		super.create();

		updateWindowTitle();

		zoom = 24 * TileMap.TILE_WIDTH;

		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.setToOrtho(true, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		camera.viewportWidth = zoom;
		camera.viewportHeight = zoom * Gdx.graphics.getHeight() / Gdx.graphics.getWidth();

		camera.update();

		hudCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		hudCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		hudCamera.update();

		batch = new SpriteBatch();
		lightBatch = new ShapeRenderer();
		hudBatch = new SpriteBatch();

		onLevelChange(dungeon.getLevel());
		dungeon.start();
	}

	@Override
	public void render() {
		super.render();

		float delta = Gdx.graphics.getDeltaTime();

		handleInput();

		if (dungeon.getPlayer() != null) {
			camera.position.x = (dungeon.getPlayer().getX() * TileMap.TILE_WIDTH) + (TileMap.TILE_WIDTH / 2);
			camera.position.y = dungeon.getPlayer().getY() * TileMap.TILE_HEIGHT;
		}

		camera.update();
		hudCamera.update();

		batch.setProjectionMatrix(camera.combined);
		lightBatch.setProjectionMatrix(camera.combined);
		hudBatch.setProjectionMatrix(hudCamera.combined);

		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.begin();
		batch.enableBlending();

		drawMap();
		drawParticles(delta);
		drawEntities();

		batch.end();

		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_DST_COLOR, GL20.GL_ONE_MINUS_SRC_ALPHA);

		if (drawLights) {
			drawLights();
		}

		hudBatch.begin();

		drawHUD();

		hudBatch.end();
	}

	private void drawMap() {
		for (int y = 0; y < dungeon.getLevel().getHeight(); y++) {
			for (int x = 0; x < dungeon.getLevel().getWidth(); x++) {
				TileMap tm = TileMap.valueOf(dungeon.getLevel().getTile(x, y).name());

				if (tm.getRenderer() != null) {
					tm.getRenderer().draw(batch, dungeon, x, y);
				}
			}
		}
	}

	private void drawParticles(float delta) {
		for (Iterator<ParticleEffectPool.PooledEffect> iterator = pooledEffects.iterator(); iterator.hasNext(); ) {
			ParticleEffectPool.PooledEffect effect = iterator.next();

			effect.draw(batch, delta * 0.25f);

			if (effect.isComplete()) {
				effect.free();
				iterator.remove();
			}
		}
	}

	private void drawEntities() {
		for (Entity entity : dungeon.getLevel().getEntities()) {
			EntityMap em = EntityMap.valueOf(entity.getAppearance().name());

			if (em.getRenderer() != null) {
				em.getRenderer().draw(batch, dungeon, entity);
			}
		}
	}

	private void drawLights() {
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

	private void drawHUD() {
		Player player = dungeon.getPlayer();

		drawHUDString(
			String.format(
				"[P_YELLOW]%s[] the [P_BLUE_2]%s[] - HP [%s]%,d[]/%,d",
				StringUtils.capitalize(player.getName()),
				"Wizard",
				"P_GREEN_3",
				player.getHealth(),
				player.getMaxHealth()
			),
			6, 7,
			Color.WHITE,
			32
		);

		int logSize = Math.min(5, log.size());

		for (int i = 0; i < logSize; i++) {
			String entry = log.get(log.size() - (logSize - i));

			if (i < logSize - 1) {
				entry = "[#CCCCCCEE]" + entry;
			}

			drawHUDString(entry, 6, 34 + (16 * i), Color.WHITE, 16);
		}
	}

	private void drawHUDString(String text, int x, int y) {
		drawHUDString(text, x, y, Color.WHITE, 16);
	}

	private void drawHUDString(String text, int x, int y, Color colour, int size) {
		FontLoader.getFont("PixelOperator.ttf", size, true).draw(hudBatch, text, x, getHudY(y));
	}

	private int getHudY(int y) {
		return Gdx.graphics.getHeight() - y;
	}

	private void handleInput() {
		if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT)) {
			handleRendererCommands();
		} else {
			handleMovementCommands();
		}
	}

	private void handleMovementCommands() {
		for (Integer key : MOVEMENT_KEYS.keySet()) {
			if (Gdx.input.isKeyJustPressed(key)) {
				Integer[] d = MOVEMENT_KEYS.get(key);

				dungeon.getPlayer().walk(d[0], d[1]);
			}
		}
	}

	private void handleRendererCommands() {
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

		camera.viewportWidth = zoom;
		camera.viewportHeight = zoom * height / width;

		hudCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

	@Override
	public void onLevelChange(Level level) {
		findPooledParticles();
	}

	private void findPooledParticles() {
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

				if (renderer.getParticleEffectPool() == null || !renderer.shouldDrawParticles(dungeon, x, y)) {
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
	public void onBeforeTurn(long turn) {

	}

	@Override
	public void onTurn(long turn) {

	}

	@Override
	public void onLog(String entry) {
		entry = entry.replace("[GREEN]", "[P_GREEN_3]");
		entry = entry.replace("[CYAN]", "[P_CYAN_1]");
		entry = entry.replace("[BLUE]", "[P_BLUE_1]");
		entry = entry.replace("[YELLOW]", "[P_YELlOW]");
		// TODO: Add more replacements

		log.add(entry);
	}
}
