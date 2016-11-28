package pw.lemmmy.jrogue.rendering.gdx;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.apache.commons.lang3.StringUtils;
import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.Prompt;
import pw.lemmmy.jrogue.dungeon.entities.Entity;
import pw.lemmmy.jrogue.dungeon.entities.Player;
import pw.lemmmy.jrogue.dungeon.tiles.TileType;
import pw.lemmmy.jrogue.rendering.Renderer;
import pw.lemmmy.jrogue.rendering.gdx.entities.EntityMap;
import pw.lemmmy.jrogue.rendering.gdx.tiles.TileMap;
import pw.lemmmy.jrogue.rendering.gdx.tiles.TilePooledEffect;
import pw.lemmmy.jrogue.rendering.gdx.tiles.TileRenderer;
import pw.lemmmy.jrogue.rendering.gdx.utils.FontLoader;
import pw.lemmmy.jrogue.rendering.gdx.utils.ImageLoader;
import pw.lemmmy.jrogue.rendering.gdx.windows.DebugWindow;
import pw.lemmmy.jrogue.rendering.gdx.windows.InventoryWindow;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class GDXRenderer extends ApplicationAdapter implements Renderer, Dungeon.Listener {
	private static final String WINDOW_TITLE = "JRogue";

	private SpriteBatch batch;
	private ShapeRenderer lightBatch;
	private SpriteBatch lightSpriteBatch;

	private OrthographicCamera camera;

	private Skin hudSkin;
	private Stage hudStage;
	private Table hudTable;
	private Label hudPlayerLabel;
	private Table hudAttributes;
	private Label hudEffectsLabel;
	private HorizontalGroup hudBrightness;
	private Table hudLog;
	private Label hudPromptLabel;

	private Dungeon dungeon;

	private List<String> log = new ArrayList<>();

	private List<TilePooledEffect> pooledEffects = new ArrayList<>();

	private float zoom = 1.0f;

	public GDXRenderer(Dungeon dungeon, int width, int height) {
		this.dungeon = dungeon;
		this.dungeon.addListener(this);

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = width;
		config.height = height;
		config.forceExit = false;
		new LwjglApplication(this, config);
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

		setupHUD();

		onLevelChange(dungeon.getLevel());
		dungeon.start();
	}

	protected void setupHUD() {
		hudStage = new Stage(new ScreenViewport());
		hudSkin = new HUDSkin();

		hudTable = new Table();
		hudTable.setFillParent(true);

		hudPlayerLabel = new Label(null, hudSkin, "large");
		hudPlayerLabel.setAlignment(Align.left);
		hudTable.add(hudPlayerLabel).top().growX().pad(0, 2, 0, 2);
		hudTable.row();

		hudLog = new Table();
		hudLog.left();
		hudTable.setFillParent(true);
		hudTable.add(hudLog).growX().left().pad(0, 1, 0, 1);
		hudTable.row();

		hudPromptLabel = new Label(null, hudSkin);
		hudTable.add(hudPromptLabel).growX().left().pad(0, 1, 0, 1);
		hudTable.row();

		hudTable.add(new Container()).expand();
		hudTable.row();

		hudEffectsLabel = new Label(null, hudSkin);
		hudTable.add(hudEffectsLabel).growX().left().pad(0, 1, 0, 1);
		hudTable.row();

		setupHUDAttributes(hudTable);

		hudBrightness = new HorizontalGroup();
		hudTable.add(hudBrightness).pad(0, 2, -2, 8).right();

		hudTable.add(new Image(ImageLoader.getImageFromSheet("hud.png", 7, 2, 16, 16, false)));
		Label nutritionLabel = new Label("HNG: Not hungry", hudSkin);
		nutritionLabel.setName("attributeNutrition");
		hudTable.add(nutritionLabel).pad(0, 2, 0, 2).right();

		hudTable.row();

		hudTable.top().pad(2);
		hudStage.addActor(hudTable);

		InputMultiplexer inputMultiplexer = new InputMultiplexer();
		inputMultiplexer.addProcessor(new GameInputProcessor(dungeon, this));
		inputMultiplexer.addProcessor(hudStage);
		Gdx.input.setInputProcessor(inputMultiplexer);
	}

	private void setupHUDAttributes(Table hudTable) {
		hudAttributes = new Table();

		hudAttributes.add(new Image(ImageLoader.getImageFromSheet("hud.png", 0, 2, 16, 16, false)));
		Label strengthLabel = new Label("STR: 0", hudSkin);
		strengthLabel.setName("attributeStrength");
		hudAttributes.add(strengthLabel).pad(0, 2, 0, 8);

		hudAttributes.add(new Image(ImageLoader.getImageFromSheet("hud.png", 1, 2, 16, 16, false)));
		Label agilityLabel = new Label("AGI: 0", hudSkin);
		agilityLabel.setName("attributeAgility");
		hudAttributes.add(agilityLabel).pad(0, 2, 0, 8);

		hudAttributes.add(new Image(ImageLoader.getImageFromSheet("hud.png", 2, 2, 16, 16, false)));
		Label dexterityLabel = new Label("DXT: 0", hudSkin);
		dexterityLabel.setName("attributeDexterity");
		hudAttributes.add(dexterityLabel).pad(0, 2, 0, 8);

		hudAttributes.add(new Image(ImageLoader.getImageFromSheet("hud.png", 3, 2, 16, 16, false)));
		Label constitutionLabel = new Label("CON: 0", hudSkin);
		constitutionLabel.setName("attributeConstitution");
		hudAttributes.add(constitutionLabel).pad(0, 2, 0, 8);

		hudAttributes.add(new Image(ImageLoader.getImageFromSheet("hud.png", 4, 2, 16, 16, false)));
		Label intelligenceLabel = new Label("INT: 0", hudSkin);
		intelligenceLabel.setName("attributeIntelligence");
		hudAttributes.add(intelligenceLabel).pad(0, 2, 0, 8);

		hudAttributes.add(new Image(ImageLoader.getImageFromSheet("hud.png", 5, 2, 16, 16, false)));
		Label wisdomLabel = new Label("WIS: 0", hudSkin);
		wisdomLabel.setName("attributeWisdom");
		hudAttributes.add(wisdomLabel).pad(0, 2, 0, 8);

		hudAttributes.add(new Image(ImageLoader.getImageFromSheet("hud.png", 6, 2, 16, 16, false)));
		Label charismaLabel = new Label("CHA: 0", hudSkin);
		charismaLabel.setName("attributeCharisma");
		hudAttributes.add(charismaLabel).pad(0, 2, 0, 8);

		hudTable.add(hudAttributes).left().pad(0, 1, 0, 1);
	}

	@Override
	public void render() {
		super.render();

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
		drawParticles(delta);
		drawEntities();

		batch.end();

		drawLights();

		hudStage.act(delta);
		hudStage.draw();
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
		drawEntities();

		batch.end();

		Pixmap pmap = ScreenUtils.getFrameBufferPixmap(0, 0, levelWidth, levelHeight);

		fbo.end();
		fbo.dispose();
		return pmap;
	}

	private void drawParticles(float delta) {
		for (Iterator<TilePooledEffect> iterator = pooledEffects.iterator(); iterator.hasNext(); ) {
			TilePooledEffect effect = iterator.next();

			if (!dungeon.getLevel().isTileDiscovered(effect.getX(), effect.getY())) {
				continue;
			}

			effect.getPooledEffect().draw(batch, delta * 0.25f);

			if (effect.getPooledEffect().isComplete()) {
				effect.getPooledEffect().free();
				iterator.remove();
			}
		}
	}

	private void drawEntities() {
		dungeon.getLevel().getEntities().stream()
			.sorted(Comparator.comparingInt(Entity::getDepth))
			.forEach(e -> {
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

	private String replaceMarkupString(String s) {
		s = s.replace("[GREEN]", "[P_GREEN_3]");
		s = s.replace("[CYAN]", "[P_CYAN_1]");
		s = s.replace("[BLUE]", "[P_BLUE_1]");
		s = s.replace("[YELLOW]", "[P_YELLOW]");

		return s;
	}

	public void showDebugWindow() {
		if (dungeon.getPlayer().getName(false).equalsIgnoreCase("debugger")) {
			new DebugWindow(this, hudStage, hudSkin, dungeon, dungeon.getLevel()).show();
		}
	}

	public void showInventoryWindow() {
		new InventoryWindow(this, hudStage, hudSkin, dungeon, dungeon.getLevel()).show();
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);

		camera.setToOrtho(true, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		camera.viewportWidth = Math.round(zoom);
		camera.viewportHeight = Math.round(zoom * height / width);

		hudStage.getViewport().update(width, height, true);
	}

	@Override
	public void onLevelChange(Level level) {
		findPooledParticles();
	}

	private void findPooledParticles() {
		pooledEffects.forEach(e -> e.getPooledEffect().free());
		pooledEffects.clear();

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
				pooledEffects.add(tilePooledEffect);
			}
		}
	}

	@Override
	public void onBeforeTurn(long turn) {

	}

	private void updateHUDPlayerLabel(Player player) {
		hudPlayerLabel.setText(String.format(
			"[P_YELLOW]%s[] the [P_BLUE_2]%s[] - HP [%s]%,d[]/%,d",
			player.getName(true),
			player.getRole().getName(),
			getHealthColour(player.getHealth(), player.getMaxHealth()),
			player.getHealth(),
			player.getMaxHealth()
		));
	}

	private void updateHUDAttributes(Player player) {
		((Label) hudAttributes.findActor("attributeStrength")).setText("STR: " + player.getStrength());
		((Label) hudAttributes.findActor("attributeAgility")).setText("AGI: " + player.getAgility());
		((Label) hudAttributes.findActor("attributeDexterity")).setText("DXT: " + player.getDexterity());
		((Label) hudAttributes.findActor("attributeConstitution")).setText("CON: " + player.getConstitution());
		((Label) hudAttributes.findActor("attributeIntelligence")).setText("INT: " + player.getIntelligence());
		((Label) hudAttributes.findActor("attributeWisdom")).setText("WIS: " + player.getWisdom());
		((Label) hudAttributes.findActor("attributeCharisma")).setText("CHA: " + player.getCharisma());
	}

	private void updateHUDBrightness(Player player) {
		hudBrightness.clearChildren();

		if (player.getLevel().getTileType(player.getX(), player.getY()) == TileType.TILE_CORRIDOR) {
			hudBrightness.addActor(new Image(ImageLoader.getImageFromSheet("hud.png", 9, 2, 16, 16, false)));
		} else {
			hudBrightness.addActor(new Image(ImageLoader.getImageFromSheet("hud.png", 8, 2, 16, 16, false)));
		}

		hudBrightness.addActor(new Label("BRI: " + player.getLightLevel(), hudSkin));
	}

	private void updateHUDNutrition(Player player) {
		((Label) hudTable.findActor("attributeNutrition")).setText(player.getNutritionState().toString());

		switch (player.getNutritionState().getImportance()) {
			case 1:
				hudTable.findActor("attributeNutrition").setColor(Colors.get("P_YELLOW"));
				break;
			case 2:
				hudTable.findActor("attributeNutrition").setColor(Colors.get("P_RED"));
				break;
			default:
				hudTable.findActor("attributeNutrition").setColor(Color.WHITE);
				break;
		}
	}

	private void updateHUDStatusEffects(Player player) {
		if (player.getStatusEffects().size() > 0) {
			List<String> effects = player.getStatusEffects().stream()
				.map(e -> {
					switch (e.getSeverity()) {
						case MINOR:
							return "[P_YELLOW]" + e.getName() + "[]";
						case MAJOR:
							return "[P_ORANGE_2]" + e.getName() + "[]";
						case CRITICAL:
							return "[P_RED]" + e.getName() + "[]";
						default:
							return "";
					}
				})
				.collect(Collectors.toList());

			hudEffectsLabel.setText(StringUtils.join(effects, " "));
		} else {
			hudEffectsLabel.setText("");
		}
	}

	@Override
	public void onTurn(long turn) {
		updateWindowTitle();

		Player player = dungeon.getPlayer();
		updateHUDPlayerLabel(player);
		updateHUDAttributes(player);
		updateHUDBrightness(player);
		updateHUDNutrition(player);
		updateHUDStatusEffects(player);
	}

	private String getHealthColour(int health, int maxHealth) {
		if (health <= maxHealth / 5) {
			return "P_RED";
		} else if (health <= maxHealth / 3) {
			return "P_ORANGE_3";
		} else if (health <= maxHealth / 2) {
			return "P_YELLOW";
		} else {
			return "P_GREEN_3";
		}
	}

	@Override
	public void onLog(String entry) {
		entry = replaceMarkupString(entry);

		log.add(entry);

		hudLog.clearChildren();

		int logSize = Math.min(7, log.size());

		for (int i = 0; i < logSize; i++) {
			String s = log.get(log.size() - (logSize - i));

			if (i < logSize - 1) {
				s = "[#CCCCCCEE]" + s;
			}

			Label newEntry = new Label(s, hudSkin, "default");
			hudLog.add(newEntry).left().growX();
			hudLog.row();
		}
	}

	@Override
	public void onPrompt(Prompt prompt) {
		if (prompt == null) {
			hudPromptLabel.setText("");
		} else {
			if (prompt.getOptions() == null) {
				hudPromptLabel.setText(String.format(
					"[P_BLUE_1]%s[]",
					prompt.getMessage()
				));
			} else {
				hudPromptLabel.setText(String.format(
					"[P_BLUE_1]%s[] [[[P_YELLOW]%s[]]",
					prompt.getMessage(),
					new String(prompt.getOptions())
				));
			}
		}
	}

	@Override
	public void dispose() {
		super.dispose();

		batch.dispose();
		lightBatch.dispose();
		lightSpriteBatch.dispose();

		hudStage.dispose();
		hudSkin.dispose();

		pooledEffects.forEach(e -> e.getPooledEffect().free());

		ImageLoader.disposeAll();
		FontLoader.disposeAll();
	}
}
