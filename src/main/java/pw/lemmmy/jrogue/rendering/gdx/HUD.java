package pw.lemmmy.jrogue.rendering.gdx;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.apache.commons.lang3.StringUtils;
import pw.lemmmy.jrogue.Settings;
import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.Prompt;
import pw.lemmmy.jrogue.dungeon.entities.Entity;
import pw.lemmmy.jrogue.dungeon.entities.Path;
import pw.lemmmy.jrogue.dungeon.entities.Player;
import pw.lemmmy.jrogue.dungeon.tiles.TileType;
import pw.lemmmy.jrogue.rendering.gdx.utils.HUDUtils;
import pw.lemmmy.jrogue.rendering.gdx.utils.ImageLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HUD implements Dungeon.Listener {
	private Settings settings;

	private Skin skin;
	private Stage stage;
	private Table root;
	private Label playerLabel;
	private Table gameLog;
	private Label promptLabel;
	private Table infoLine;
	private Table attributes;
	private Label effectsLabel;
	private HorizontalGroup brightness;

	private Dungeon dungeon;
	private List<LogEntry> log = new ArrayList<>();

	public HUD(Settings settings, Dungeon dungeon) {
		this.settings = settings;

		this.dungeon = dungeon;
	}

	public void init() {
		ScreenViewport stageViewport = new ScreenViewport();
		stageViewport.setUnitsPerPixel(1f / settings.getHUDScale());
		stage = new Stage(stageViewport);
		skin = new HUDSkin();

		root = new Table();
		root.setFillParent(true);

		Table hudTopContainer = new Table();
		hudTopContainer.setBackground(skin.getDrawable("blackTransparent"));
		initPlayerLine(hudTopContainer);
		root.add(hudTopContainer).left().fillX().row();

		root.add(new Container()).expand().row();

		initInfoLine(root);
		initAttributes(root);

		root.top();
		stage.addActor(root);
	}

	private void initPlayerLine(Table container) {
		playerLabel = new Label(null, skin, "large");
		playerLabel.setAlignment(Align.left);
		container.add(playerLabel).top().growX().pad(2, 4, 0, 4);
		container.row();

		gameLog = new Table();
		gameLog.left();
		container.add(gameLog).growX().left().pad(0, 3, 0, 3);
		container.row();

		promptLabel = new Label(null, skin);
		container.add(promptLabel).growX().left().pad(0, 3, 2, 3);
		container.row();
	}

	private void initInfoLine(Table container) {
		effectsLabel = new Label(null, skin);
		container.add(effectsLabel).growX().left().pad(0, 1, 0, 1);
		container.row();

		infoLine = new Table();

		infoLine.add(new Image(ImageLoader.getImageFromSheet("hud.png", 11, 2, 16, 16, false)));
		Label goldLabel = new Label("Gold: 0", skin);
		goldLabel.setName("gold");
		infoLine.add(goldLabel).pad(0, 2, 0, 8);

		infoLine.add(new Image(ImageLoader.getImageFromSheet("hud.png", 12, 2, 16, 16, false)));
		Label expLabel = new Label("Level: 1", skin);
		expLabel.setName("exp");
		infoLine.add(expLabel).pad(0, 2, 0, 8);

		infoLine.add(new Image(ImageLoader.getImageFromSheet("hud.png", 13, 2, 16, 16, false)));
		Label depthLabel = new Label("Depth: 1", skin);
		depthLabel.setName("depth");
		infoLine.add(depthLabel).pad(0, 2, 0, 8);

		container.add(infoLine).left().pad(0, 1, 0, 1);
		container.row();
	}

	private void initAttributes(Table container) {
		attributes = new Table();

		attributes.add(new Image(ImageLoader.getImageFromSheet("hud.png", 0, 2, 16, 16, false)));
		Label strengthLabel = new Label("STR: 0", skin);
		strengthLabel.setName("attributeStrength");
		attributes.add(strengthLabel).pad(0, 2, 0, 8);

		attributes.add(new Image(ImageLoader.getImageFromSheet("hud.png", 1, 2, 16, 16, false)));
		Label agilityLabel = new Label("AGI: 0", skin);
		agilityLabel.setName("attributeAgility");
		attributes.add(agilityLabel).pad(0, 2, 0, 8);

		attributes.add(new Image(ImageLoader.getImageFromSheet("hud.png", 2, 2, 16, 16, false)));
		Label dexterityLabel = new Label("DXT: 0", skin);
		dexterityLabel.setName("attributeDexterity");
		attributes.add(dexterityLabel).pad(0, 2, 0, 8);

		attributes.add(new Image(ImageLoader.getImageFromSheet("hud.png", 3, 2, 16, 16, false)));
		Label constitutionLabel = new Label("CON: 0", skin);
		constitutionLabel.setName("attributeConstitution");
		attributes.add(constitutionLabel).pad(0, 2, 0, 8);

		attributes.add(new Image(ImageLoader.getImageFromSheet("hud.png", 4, 2, 16, 16, false)));
		Label intelligenceLabel = new Label("INT: 0", skin);
		intelligenceLabel.setName("attributeIntelligence");
		attributes.add(intelligenceLabel).pad(0, 2, 0, 8);

		attributes.add(new Image(ImageLoader.getImageFromSheet("hud.png", 5, 2, 16, 16, false)));
		Label wisdomLabel = new Label("WIS: 0", skin);
		wisdomLabel.setName("attributeWisdom");
		attributes.add(wisdomLabel).pad(0, 2, 0, 8);

		attributes.add(new Image(ImageLoader.getImageFromSheet("hud.png", 6, 2, 16, 16, false)));
		Label charismaLabel = new Label("CHA: 0", skin);
		charismaLabel.setName("attributeCharisma");
		attributes.add(charismaLabel).pad(0, 2, 0, 8);

		attributes.add(new Container()).expand();

		brightness = new HorizontalGroup();
		attributes.add(brightness).pad(0, 2, -2, 8).right();

		attributes.add(new Image(ImageLoader.getImageFromSheet("hud.png", 7, 2, 16, 16, false)));
		Label nutritionLabel = new Label("HNG: Not hungry", skin);
		nutritionLabel.setName("attributeNutrition");
		attributes.add(nutritionLabel).pad(0, 2, 0, 2).right();

		container.add(attributes).left().fillX().pad(0, 1, 0, 1);
	}

	public Stage getStage() {
		return stage;
	}

	public Skin getSkin() {
		return skin;
	}

	public void updateAndDraw(float delta) {
		stage.act(delta);
		stage.draw();
	}

	public void updateViewport(int width, int height) {
		stage.getViewport().update(width, height, true);
	}

	public void dispose() {
		stage.dispose();
		skin.dispose();
	}

	@Override
	public void onLevelChange(Level level) {

	}

	@Override
	public void onBeforeTurn(long turn) {

	}

	@Override
	public void onTurn(long turn) {
		Player player = dungeon.getPlayer();
		updatePlayerLabel(player);
		updateInfoLine(player);
		updateAttributes(player);
		updateBrightness(player);
		updateNutrition(player);
		updateStatusEffects(player);
	}

	private void updatePlayerLabel(Player player) {
		playerLabel.setText(String.format(
			"[P_YELLOW]%s[] the [P_BLUE_2]%s[] - HP [%s]%,d[]/%,d",
			player.getName(true),
			player.getRole().getName(),
			HUDUtils.getHealthColour(player.getHealth(), player.getMaxHealth()),
			player.getHealth(),
			player.getMaxHealth()
		));
	}

	private void updateInfoLine(Player player) {
		((Label) infoLine.findActor("gold")).setText(String.format("Gold: %,d", player.getGold()));
		((Label) infoLine.findActor("exp")).setText(String.format("Level: %,d", player.getExperienceLevel()));
		((Label) infoLine.findActor("depth")).setText(String.format("Depth: %,d", player.getLevel().getDepth()));
	}

	private void updateAttributes(Player player) {
		((Label) attributes.findActor("attributeStrength")).setText("STR: " + player.getStrength());
		((Label) attributes.findActor("attributeAgility")).setText("AGI: " + player.getAgility());
		((Label) attributes.findActor("attributeDexterity")).setText("DXT: " + player.getDexterity());
		((Label) attributes.findActor("attributeConstitution")).setText("CON: " + player.getConstitution());
		((Label) attributes.findActor("attributeIntelligence")).setText("INT: " + player.getIntelligence());
		((Label) attributes.findActor("attributeWisdom")).setText("WIS: " + player.getWisdom());
		((Label) attributes.findActor("attributeCharisma")).setText("CHA: " + player.getCharisma());
	}

	private void updateBrightness(Player player) {
		brightness.clearChildren();

		if (player.getLevel().getTileType(player.getX(), player.getY()) == TileType.TILE_CORRIDOR) {
			brightness.addActor(new Image(ImageLoader.getImageFromSheet("hud.png", 9, 2, 16, 16, false)));
		} else {
			brightness.addActor(new Image(ImageLoader.getImageFromSheet("hud.png", 8, 2, 16, 16, false)));
		}

		brightness.addActor(new Label("BRI: " + player.getLightLevel(), skin));
	}

	private void updateNutrition(Player player) {
		((Label) root.findActor("attributeNutrition")).setText(player.getNutritionState().toString());
		root.findActor("attributeNutrition").setColor(HUDUtils.getNutritionColour(player.getNutritionState()));
	}

	private void updateStatusEffects(Player player) {
		if (player.getStatusEffects().size() > 0) {
			java.util.List<String> effects = player.getStatusEffects().stream()
												   .map(e -> String.format("[%s]%s[]", HUDUtils.getStatusEffectColour
													   (e.getSeverity()), e.getName())
												   )
												   .collect(Collectors.toList());

			effectsLabel.setText(StringUtils.join(effects, " "));
		} else {
			effectsLabel.setText("");
		}
	}

	@Override
	public void onLog(String entry) {
		entry = HUDUtils.replaceMarkupString(entry);

		log.add(new LogEntry(dungeon.getTurn(), entry));

		gameLog.clearChildren();

		int logSize = Math.min(settings.getLogSize(), log.size());

		for (int i = 0; i < logSize; i++) {
			LogEntry e = log.get(log.size() - (logSize - i));
			String text = e.getTurn() != dungeon.getTurn() ? "[#CCCCCCEE]" + e.getText() : e.getText();

			Label newEntry = new Label(text, skin, "default");
			gameLog.add(newEntry).left().growX();
			gameLog.row();
		}
	}

	@Override
	public void onPrompt(Prompt prompt) {
		if (prompt == null) {
			promptLabel.setText("");
		} else {
			if (prompt.getOptions() == null) {
				promptLabel.setText(String.format(
					"[P_CYAN_1]%s[]",
					prompt.getMessage()
				));
			} else {
				promptLabel.setText(String.format(
					"[P_CYAN_1]%s[] [[[P_YELLOW]%s[]]",
					prompt.getMessage(),
					prompt.getOptionsString()
				));
			}
		}
	}

	@Override
	public void onContainerShow(Entity containerEntity) {

	}

	@Override
	public void onPathShow(Path path) {

	}

	@Override
	public void onEntityAdded(Entity entity) {

	}

	@Override
	public void onEntityMoved(Entity entity, int lastX, int lastY, int newX, int newY) {

	}

	@Override
	public void onEntityRemoved(Entity entity) {

	}

	@Override
	public void onQuit() {

	}

	@Override
	public void onSaveAndQuit() {

	}

	private class LogEntry {
		private long turn;
		private String text;

		public LogEntry(long turn, String text) {
			this.turn = turn;
			this.text = text;
		}

		public long getTurn() {
			return turn;
		}

		public String getText() {
			return text;
		}
	}
}
