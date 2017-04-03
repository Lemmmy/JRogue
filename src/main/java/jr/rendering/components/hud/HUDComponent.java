package jr.rendering.components.hud;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import jr.Settings;
import jr.dungeon.Dungeon;
import jr.dungeon.Prompt;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.monsters.Monster;
import jr.dungeon.entities.player.Attribute;
import jr.dungeon.entities.player.Player;
import jr.dungeon.events.*;
import jr.dungeon.tiles.TileType;
import jr.rendering.components.RendererComponent;
import jr.rendering.screens.GameScreen;
import jr.rendering.tiles.TileMap;
import jr.rendering.ui.UISkin;
import jr.rendering.ui.windows.*;
import jr.rendering.utils.HUDUtils;
import jr.rendering.utils.ImageLoader;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class HUDComponent extends RendererComponent {
	@Getter private Skin skin;
	@Getter private Stage stage;
	private Label playerLabel;
	private Table gameLog;
	private Label promptLabel;
	private Table attributeTable;
	private Table topStats;
	private Label effectsLabel;
	private HorizontalGroup brightness;
	
	private int healthLastTurn;
	private int energyLastTurn;
	
	private Player player;
	
	private List<LogEntry> log = new ArrayList<>();
	@Getter	private List<PopupWindow> windows = new ArrayList<>();
	
	@Getter	private List<Actor> singleTurnActors = new ArrayList<>();
	private List<Runnable> nextFrameDeferred = new ArrayList<>();
	
	public HUDComponent(GameScreen renderer, Dungeon dungeon, Settings settings) {
		super(renderer, dungeon, settings);
	}
	
	private void initPlayerLine(Table container) {
		Table topPlayerLine = new Table();
		playerLabel = new Label(null, skin, "large");
		topPlayerLine.add(playerLabel).padRight(16).left();
		
		topStats = new Table();
		
		topStats.add(new Image(ImageLoader.getImageFromSheet("textures/hud.png", 3, 10, 16, 16, false)));
		Label hpLabel = new Label("Health: 0 / 0", skin);
		hpLabel.setName("health");
		topStats.add(hpLabel).pad(0, 2, 0, 8).left();
		
		topStats.add(new Image(ImageLoader.getImageFromSheet("textures/hud.png", 7, 10, 16, 16, false)));
		Label nutritionLabel = new Label("Not hungry", skin);
		nutritionLabel.setName("nutrition");
		topStats.add(nutritionLabel).pad(0, 2, 0, 8).left();
		
		topStats.add(new Image(ImageLoader.getImageFromSheet("textures/hud.png", 12, 10, 16, 16, false)));
		Label expLabel = new Label("Level: 1", skin);
		expLabel.setName("exp");
		topStats.add(expLabel).pad(0, 2, 0, 8).left().row();
		
		topStats.add(new Image(ImageLoader.getImageFromSheet("textures/hud.png", 14, 10, 16, 16, false)));
		Label energyLabel = new Label("Energy: 0 / 0", skin);
		energyLabel.setName("energy");
		topStats.add(energyLabel).pad(0, 2, 0, 8).left();
		
		topStats.add(new Image(ImageLoader.getImageFromSheet("textures/hud.png", 11, 10, 16, 16, false)));
		Label goldLabel = new Label("Gold: 0", skin);
		goldLabel.setName("gold");
		topStats.add(goldLabel).pad(0, 2, 0, 8).left();
		
		topStats.add(new Image(ImageLoader.getImageFromSheet("textures/hud.png", 13, 10, 16, 16, false)));
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
			
			attributeTable.add(new Image(ImageLoader.getImageFromSheet("textures/hud.png", sheetX, 10, 16, 16,
				false)));
			Label attributeLabel = new Label(text, skin);
			attributeLabel.setName(actorName);
			attributeTable.add(attributeLabel).pad(0, 2, 0, 8);
		});
		
		attributeTable.add(new Container<>()).expand();
		
		brightness = new HorizontalGroup();
		attributeTable.add(brightness).pad(0, 2, -2, 2).right();
		
		container.add(attributeTable).left().fillX().pad(0, 1, 0, 1);
	}
	
	@Override
	public void initialise() {
		ScreenViewport stageViewport = new ScreenViewport();
		stageViewport.setUnitsPerPixel(1f / settings.getHudScale());
		stage = new Stage(stageViewport);
		skin = new UISkin();
		
		Table root = new Table();
		root.setFillParent(true);
		
		Table hudTopContainer = new Table(skin);
		hudTopContainer.setBackground("blackTransparent");
		initPlayerLine(hudTopContainer);
		root.add(hudTopContainer).left().fillX().row();
		
		root.add(new Container<>()).expand().row();
		
		initEffectsLine(root);
		initAttributes(root);
		
		root.top();
		stage.addActor(root);
		
		dungeon.getEventSystem().addListener(new TextPopups(this));
	}
	
	@Override
	public void render(float dt) {
		for (Iterator<Runnable> iterator = nextFrameDeferred.iterator(); iterator.hasNext(); ) {
			Runnable r = iterator.next();
			r.run();
			iterator.remove();
		}
		
		stage.draw();
	}
	
	@Override
	public void update(float dt) {
		stage.act(dt);
	}
	
	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}
	
	@Override
	public int getZIndex() {
		return 100;
	}
	
	@Override
	public void dispose() {
		stage.dispose();
		skin.dispose();
	}
	
	@EventHandler
	private void onLevelChange(LevelChangeEvent e) {
		if (dungeon.getPlayer() != null) {
			player = dungeon.getPlayer();
			
			healthLastTurn = player.getHealth();
			energyLastTurn = player.getEnergy();
			updatePlayerLine(player);
		}
	}
	
	@EventHandler
	private void onBeforeTurn(BeforeTurnEvent e) {
		singleTurnActors.forEach(Actor::remove);
		singleTurnActors.clear();
	}
	
	@EventHandler
	private void onTurn(TurnEvent e) {
		updatePlayerLine(player);
		updateAttributes(player);
		updateBrightness(player);
		updateStatusEffects(player);
		
		if (settings.isShowAIDebug()) {
			showEntityAIStates();
		}
		
		healthLastTurn = player.getHealth();
		energyLastTurn = player.getEnergy();
	}
	
	private void showEntityAIStates() {
		if (!player.isDebugger()) {
			return;
		}
		
		dungeon.getLevel().getEntityStore().getEntities().stream()
			.filter(Monster.class::isInstance)
			.map(e -> (Monster) e)
			.filter(m -> m.getAI() != null)
			.filter(m -> m.getAI().toString() != null)
			.filter(m -> !m.getAI().toString().isEmpty())
			.forEach(m -> {
				int x = m.getX();
				int y = m.getY();
				
				renderer.updateCamera();
				
				Vector3 pos = renderer.getCamera().project(
					new Vector3((x + 0.5f) * TileMap.TILE_WIDTH, y * TileMap.TILE_HEIGHT, 0)
				);
				
				Table stateTable = new Table(skin);
				stateTable.setBackground("blackTransparent");
				
				stateTable.add(new Label(m.getAI().toString(), skin));
				
				stage.getRoot().addActor(stateTable);
				stateTable.pad(4);
				stateTable.pack();
				stateTable.setPosition((int) pos.x - (int) (stateTable.getWidth() / 2), (int) pos.y);
				singleTurnActors.add(stateTable);
			});
	}
	
	private void updatePlayerLine(Player player) {
		playerLabel.setText(String.format(
			"[P_YELLOW]%s[] the [P_BLUE_2]%s[]",
			player.getName(player, true),
			player.getRole().getName()
		));
		
		updateHealth(player);
		updateEnergy(player);
		
		((Label) topStats.findActor("gold")).setText(String.format("Gold: %,d", player.getGold()));
		((Label) topStats.findActor("exp")).setText(String.format("Level: %,d", player.getExperienceLevel()));
		((Label) topStats.findActor("depth")).setText(String.format("Depth: %,d", player.getLevel().getDepth()));
		((Label) topStats.findActor("nutrition")).setText(player.getNutritionState().toString());
		topStats.findActor("nutrition").setColor(HUDUtils.getNutritionColour(player.getNutritionState()));
	}
	
	private void updateHealth(Player player) {
		if (player.getHealth() != healthLastTurn) {
			String bg = healthLastTurn > player.getHealth() ? "redBackground" : "greenBackground";
			
			((Label) topStats.findActor("health")).setStyle(skin.get(bg, Label.LabelStyle.class));
			((Label) topStats.findActor("health")).setText(String.format(
				"Health: %,d / %,d",
				player.getHealth(),
				player.getMaxHealth()
			));
		} else {
			((Label) topStats.findActor("health")).setStyle(skin.get("default", Label.LabelStyle.class));
			((Label) topStats.findActor("health")).setText(String.format(
				"Health: [%s]%,d[] / [P_GREEN_3]%,d[]",
				HUDUtils.getHealthColour(player.getHealth(), player.getMaxHealth()),
				player.getHealth(),
				player.getMaxHealth()
			));
		}
	}
	
	private void updateEnergy(Player player) {
		if (player.getEnergy() != energyLastTurn) {
			String bg = energyLastTurn > player.getEnergy() ? "redBackground" : "greenBackground";
			
			((Label) topStats.findActor("energy")).setStyle(skin.get(bg, Label.LabelStyle.class));
			((Label) topStats.findActor("energy")).setText(String.format(
				"Energy: %,d / %,d",
				player.getEnergy(),
				player.getMaxEnergy()
			));
		} else {
			((Label) topStats.findActor("energy")).setStyle(skin.get("default", Label.LabelStyle.class));
			((Label) topStats.findActor("energy")).setText(String.format(
				"Energy: [%s]%,d[] / [P_GREEN_3]%,d[]",
				HUDUtils.getHealthColour(player.getEnergy(), player.getMaxEnergy()),
				player.getEnergy(),
				player.getMaxEnergy()
			));
		}
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
		
		int sheetX = player.getLevel().getTileStore().getTileType(player.getX(), player.getY()) == TileType.TILE_CORRIDOR ? 9 : 8;
		
		brightness.addActor(new Image(ImageLoader.getImageFromSheet("textures/hud.png", sheetX, 10, 16, 16,
			false)));
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
	
	@EventHandler
	private void onLog(LogEvent event) {
		String entry = event.getEntry();
		entry = HUDUtils.replaceMarkupString(entry);
		
		log.add(new LogEntry(dungeon.getTurn(), entry));
		
		gameLog.clearChildren();
		
		int logSize = Math.min(settings.getLogSize(), log.size());
		
		for (int i = 0; i < logSize; i++) {
			LogEntry logEntry = log.get(log.size() - (logSize - i));
			String text = logEntry.getTurn() != dungeon.getTurn() ? "[#CCCCCCEE]" + logEntry.getText() : logEntry.getText();
			
			Label newEntry = new Label(text, skin, "default");
			gameLog.add(newEntry).left().growX();
			gameLog.row();
		}
	}
	
	@EventHandler
	private void onPrompt(PromptEvent e) {
		Prompt prompt = e.getPrompt();
		
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
	
	public void addWindow(PopupWindow window) {
		windows.add(window);
	}
	
	public void removeWindow(PopupWindow window) {
		windows.remove(window);
	}
	
	@EventHandler
	private void onContainerShow(ContainerShowEvent e) {
		Entity containerEntity = e.getContainerEntity();
		
		nextFrameDeferred
			.add(() -> new ContainerWindow(renderer, stage, skin, containerEntity)
				.show());
	}
	
	public void showDebugWindow() {
		nextFrameDeferred
			.add(() -> new DebugWindow(renderer, stage, skin, dungeon, dungeon.getLevel())
				.show());
	}
	
	public void showInventoryWindow() {
		nextFrameDeferred
			.add(() -> new PlayerWindow(renderer, stage, skin, player)
				.show());
	}
	
	public void showWishWindow() {
		nextFrameDeferred
			.add(() -> new WishWindow(renderer, stage, skin, dungeon, dungeon.getLevel())
				.show());
	}
	
	public void showSpellWindow() {
		nextFrameDeferred
			.add(() -> new SpellWindow(renderer, stage, skin, player)
				.show());
	}
	
	@Getter
	@AllArgsConstructor
	private class LogEntry {
		private long turn;
		private String text;
	}
}
