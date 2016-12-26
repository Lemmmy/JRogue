package pw.lemmmy.jrogue.rendering.gdx.hud;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.apache.commons.lang3.StringUtils;
import pw.lemmmy.jrogue.Settings;
import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.Prompt;
import pw.lemmmy.jrogue.dungeon.entities.Entity;
import pw.lemmmy.jrogue.dungeon.entities.player.Attribute;
import pw.lemmmy.jrogue.dungeon.entities.player.Player;
import pw.lemmmy.jrogue.dungeon.tiles.TileType;
import pw.lemmmy.jrogue.rendering.gdx.utils.HUDUtils;
import pw.lemmmy.jrogue.rendering.gdx.utils.ImageLoader;
import pw.lemmmy.jrogue.utils.Path;

import java.util.ArrayList;
import java.util.Arrays;
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
	private Table attributeTable;
	private Table topStats;
	private Label effectsLabel;
	private HorizontalGroup brightness;
	
	private int healthLastTurn;
	private int energyLastTurn;
	
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
		
		root.add(new Container<>()).expand().row();
		
		initEffectsLine(root);
		initAttributes(root);
		
		root.top();
		stage.addActor(root);
	}
	
	private void initPlayerLine(Table container) {
		Table topPlayerLine = new Table();
		playerLabel = new Label(null, skin, "large");
		topPlayerLine.add(playerLabel).padRight(16).left();
		
		topStats = new Table();
		
		topStats.add(new Image(ImageLoader.getImageFromSheet("hud.png", 3, 2, 16, 16, false)));
		Label hpLabel = new Label("Health: 0 / 0", skin);
		hpLabel.setName("health");
		topStats.add(hpLabel).pad(0, 2, 0, 8).left();
		
		topStats.add(new Image(ImageLoader.getImageFromSheet("hud.png", 7, 2, 16, 16, false)));
		Label nutritionLabel = new Label("Not hungry", skin);
		nutritionLabel.setName("nutrition");
		topStats.add(nutritionLabel).pad(0, 2, 0, 8).left();
		
		topStats.add(new Image(ImageLoader.getImageFromSheet("hud.png", 12, 2, 16, 16, false)));
		Label expLabel = new Label("Level: 1", skin);
		expLabel.setName("exp");
		topStats.add(expLabel).pad(0, 2, 0, 8).left().row();
		
		topStats.add(new Image(ImageLoader.getImageFromSheet("hud.png", 14, 2, 16, 16, false)));
		Label energyLabel = new Label("Energy: 0 / 0", skin);
		energyLabel.setName("energy");
		topStats.add(energyLabel).pad(0, 2, 0, 8).left();
		
		topStats.add(new Image(ImageLoader.getImageFromSheet("hud.png", 11, 2, 16, 16, false)));
		Label goldLabel = new Label("Gold: 0", skin);
		goldLabel.setName("gold");
		topStats.add(goldLabel).pad(0, 2, 0, 8).left();
		
		topStats.add(new Image(ImageLoader.getImageFromSheet("hud.png", 13, 2, 16, 16, false)));
		Label depthLabel = new Label("Depth: 1", skin);
		depthLabel.setName("depth");
		topStats.add(depthLabel).pad(0, 2, 0, 8).left();
		
		topPlayerLine.add(topStats).left().row();
		
		container.add(topPlayerLine).left().pad(2, 4, 0, 4);
		container.row();
		
		gameLog = new Table();
		gameLog.left();
		container.add(gameLog).growX().left().pad(0, 3, 0, 3);
		container.row();
		
		promptLabel = new Label(null, skin);
		container.add(promptLabel).growX().left().pad(0, 3, 2, 3);
		container.row();
	}
	
	private void initEffectsLine(Table container) {
		effectsLabel = new Label(null, skin);
		container.add(effectsLabel).growX().left().pad(0, 1, 0, 1);
		container.row();
	}
	
	private void initAttributes(Table container) {
		attributeTable = new Table();
		
		Arrays.stream(Attribute.values()).forEach(attribute -> {
			String actorName = "attribute" + attribute.getName();
			String text = String.format("%s: 0", attribute.getName());
			int sheetX = attribute.ordinal();
			
			attributeTable.add(new Image(ImageLoader.getImageFromSheet("hud.png", sheetX, 2, 16, 16, false)));
			Label attributeLabel = new Label(text, skin);
			attributeLabel.setName(actorName);
			attributeTable.add(attributeLabel).pad(0, 2, 0, 8);
		});
		
		attributeTable.add(new Container<>()).expand();
		
		brightness = new HorizontalGroup();
		attributeTable.add(brightness).pad(0, 2, -2, 2).right();
		
		container.add(attributeTable).left().fillX().pad(0, 1, 0, 1);
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
		if (dungeon.getPlayer() != null) {
			healthLastTurn = dungeon.getPlayer().getHealth();
			updatePlayerLine(dungeon.getPlayer());
		}
	}
	
	@Override
	public void onBeforeTurn(long turn) {}
	
	@Override
	public void onTurn(long turn) {
		Player player = dungeon.getPlayer();
		
		updatePlayerLine(player);
		updateAttributes(player);
		updateBrightness(player);
		updateStatusEffects(player);
		
		healthLastTurn = player.getHealth();
		// energyLastTurn = player.getEnergy();
	}
	
	private void updatePlayerLine(Player player) {
		playerLabel.setText(String.format(
			"[P_YELLOW]%s[] the [P_BLUE_2]%s[]",
			player.getName(true),
			player.getRole().getName()
		));
		
		if (player.getHealth() != healthLastTurn) {
			((Label) topStats.findActor("health")).setStyle(getSkin().get("health", Label.LabelStyle.class));
			((Label) topStats.findActor("health")).setText(String.format(
				"Health: %,d / %,d",
				player.getHealth(),
				player.getMaxHealth()
			));
		} else {
			((Label) topStats.findActor("health")).setStyle(getSkin().get("default", Label.LabelStyle.class));
			((Label) topStats.findActor("health")).setText(String.format(
				"Health: [%s]%,d[] / [P_GREEN_3]%,d[]",
				HUDUtils.getHealthColour(player.getHealth(), player.getMaxHealth()),
				player.getHealth(),
				player.getMaxHealth()
			));
		}
		
		((Label) topStats.findActor("gold")).setText(String.format("Gold: %,d", player.getGold()));
		((Label) topStats.findActor("exp")).setText(String.format("Level: %,d", player.getExperienceLevel()));
		((Label) topStats.findActor("depth")).setText(String.format("Depth: %,d", player.getLevel().getDepth()));
		((Label) topStats.findActor("nutrition")).setText(player.getNutritionState().toString());
		topStats.findActor("nutrition").setColor(HUDUtils.getNutritionColour(player.getNutritionState()));
	}
	
	private void updateAttributes(Player player) {
		player.getAttributes().getAttributeMap().forEach((attribute, level) -> {
			String actorName = "attribute" + attribute.getName();
			String text = String.format("%s: %,d", attribute.getName(), level);
			
			((Label) attributeTable.findActor(actorName)).setText(text);
		});
	}
	
	private void updateBrightness(Player player) {
		brightness.clearChildren();
		
		int sheetX = player.getLevel().getTileType(player.getX(), player.getY()) == TileType.TILE_CORRIDOR ? 9 : 8;
		
		brightness.addActor(new Image(ImageLoader.getImageFromSheet("hud.png", sheetX, 2, 16, 16, false)));
		brightness.addActor(new Label("Brightness: " + player.getLightLevel(), skin));
	}
	
	private void updateStatusEffects(Player player) {
		if (player.getStatusEffects().size() > 0) {
			java.util.List<String> effects = player.getStatusEffects().stream()
				.map(e -> String.format("[%s]%s[]", HUDUtils.getStatusEffectColour(e.getSeverity()), e.getName()))
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
	public void onContainerShow(Entity containerEntity) {}
	
	@Override
	public void onPathShow(Path path) {}
	
	@Override
	public void onEntityAdded(Entity entity) {}
	
	@Override
	public void onEntityMoved(Entity entity, int lastX, int lastY, int newX, int newY) {}
	
	@Override
	public void onEntityRemoved(Entity entity) {}
	
	@Override
	public void onQuit() {}
	
	@Override
	public void onSaveAndQuit() {}
	
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
