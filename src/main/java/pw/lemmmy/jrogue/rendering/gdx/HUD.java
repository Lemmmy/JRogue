package pw.lemmmy.jrogue.rendering.gdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.apache.commons.lang3.StringUtils;
import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.Prompt;
import pw.lemmmy.jrogue.dungeon.entities.Entity;
import pw.lemmmy.jrogue.dungeon.entities.Player;
import pw.lemmmy.jrogue.dungeon.tiles.TileType;
import pw.lemmmy.jrogue.rendering.gdx.utils.HUDUtils;
import pw.lemmmy.jrogue.rendering.gdx.utils.ImageLoader;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class HUD implements Dungeon.Listener {
	private Skin hudSkin;
	private Stage hudStage;
	private Table hudTable;
	private Label hudPlayerLabel;
	private Table hudInfoLine;
	private Table hudAttributes;
	private Label hudEffectsLabel;
	private HorizontalGroup hudBrightness;
	private Table hudLog;
	private Label hudPromptLabel;

	private Dungeon dungeon;
	private List<String> log = new ArrayList<>();

	public HUD(Dungeon dungeon) {
		this.dungeon = dungeon;
	}

	public void setupHUD() {
		hudStage = new Stage(new ScreenViewport());
		hudSkin = new HUDSkin();

		hudTable = new Table();
		hudTable.setFillParent(true);

		setupHUDPlayerLine(hudTable);

		hudTable.add(new Container()).expand();
		hudTable.row();

		setupHUDInfoLine(hudTable);
		setupHUDAttributes(hudTable);

		hudTable.top().pad(2);
		hudStage.addActor(hudTable);
	}

	private void setupHUDPlayerLine(Table hudTable) {
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
	}

	private void setupHUDInfoLine(Table hudTable) {
		hudEffectsLabel = new Label(null, hudSkin);
		hudTable.add(hudEffectsLabel).growX().left().pad(0, 1, 0, 1);
		hudTable.row();

		hudInfoLine = new Table();

		hudInfoLine.add(new Image(ImageLoader.getImageFromSheet("hud.png", 11, 2, 16, 16, false)));
		Label goldLabel = new Label("Gold: 0", hudSkin);
		goldLabel.setName("gold");
		hudInfoLine.add(goldLabel).pad(0, 2, 0, 8);

		hudTable.add(hudInfoLine).left().pad(0, 1, 0, 1);
		hudTable.row();
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

		hudAttributes.add(new Container()).expand();

		hudBrightness = new HorizontalGroup();
		hudAttributes.add(hudBrightness).pad(0, 2, -2, 8).right();

		hudAttributes.add(new Image(ImageLoader.getImageFromSheet("hud.png", 7, 2, 16, 16, false)));
		Label nutritionLabel = new Label("HNG: Not hungry", hudSkin);
		nutritionLabel.setName("attributeNutrition");
		hudAttributes.add(nutritionLabel).pad(0, 2, 0, 2).right();

		hudTable.add(hudAttributes).left().fillX().pad(0, 1, 0, 1);
	}

	public void updateAndDraw(float delta) {
		hudStage.act(delta);
		hudStage.draw();
	}

	public void updateViewport(int width, int height) {
		hudStage.getViewport().update(width, height, true);
	}

	public Stage getHUDStage() {
		return hudStage;
	}

	public Skin getHUDSkin() {
		return hudSkin;
	}

	private void updateHUDPlayerLabel(Player player) {
		hudPlayerLabel.setText(String.format(
			"[P_YELLOW]%s[] the [P_BLUE_2]%s[] - HP [%s]%,d[]/%,d",
			player.getName(true),
			player.getRole().getName(),
			HUDUtils.getHealthColour(player.getHealth(), player.getMaxHealth()),
			player.getHealth(),
			player.getMaxHealth()
		));
	}

	private void updateHUDInfoLine(Player player) {
		((Label) hudInfoLine.findActor("gold")).setText(String.format("Gold: %,d", player.getGold()));
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
			java.util.List<String> effects = player.getStatusEffects().stream()
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
	public void onLevelChange(Level level) {

	}

	@Override
	public void onBeforeTurn(long turn) {

	}

	@Override
	public void onTurn(long turn) {
		Player player = dungeon.getPlayer();
		updateHUDPlayerLabel(player);
		updateHUDInfoLine(player);
		updateHUDAttributes(player);
		updateHUDBrightness(player);
		updateHUDNutrition(player);
		updateHUDStatusEffects(player);
	}

	@Override
	public void onLog(String entry) {
		entry = HUDUtils.replaceMarkupString(entry);

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
					prompt.getOptionsString()
				));
			}
		}
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

	public void dispose() {
		hudStage.dispose();
		hudSkin.dispose();
	}
}
