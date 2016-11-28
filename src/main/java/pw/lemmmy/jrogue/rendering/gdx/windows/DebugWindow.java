package pw.lemmmy.jrogue.rendering.gdx.windows;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.entities.DamageSource;
import pw.lemmmy.jrogue.dungeon.entities.Entity;
import pw.lemmmy.jrogue.dungeon.entities.LivingEntity;
import pw.lemmmy.jrogue.dungeon.entities.Player;
import pw.lemmmy.jrogue.dungeon.entities.effects.InjuredFoot;
import pw.lemmmy.jrogue.dungeon.entities.effects.StrainedLeg;
import pw.lemmmy.jrogue.dungeon.entities.monsters.MonsterFish;
import pw.lemmmy.jrogue.dungeon.entities.monsters.MonsterJackal;
import pw.lemmmy.jrogue.dungeon.entities.monsters.MonsterPufferfish;
import pw.lemmmy.jrogue.rendering.gdx.GDXRenderer;
import pw.lemmmy.jrogue.utils.Utils;

import java.nio.file.Paths;

public class DebugWindow extends PopupWindow {
	public DebugWindow(GDXRenderer renderer, Stage stage, Skin skin, Dungeon dungeon, Level level) {
		super(renderer, stage, skin, dungeon, level);
	}

	@Override
	public String getTitle() {
		return "Debug Tools";
	}

	@Override
	public void populateWindow() {
		getWindow().setWidth(350f);
		getWindow().setHeight(250f);

		getWindow().getContentTable().add(new Label(String.format("Nutrition: %,d", getDungeon().getPlayer().getNutrition()), getSkin(), "windowStyle"));
		getWindow().getContentTable().row();

		getWindow().getContentTable().add(new Label("Turn: ", getSkin(), "windowStyle"));

		final TextField turnField = new TextField("", getSkin());
		getWindow().getContentTable().add(turnField).left().width(100f);

		Button turnSetButton = new TextButton("Set", getSkin());
		turnSetButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				try {
					long turn = Long.parseLong(turnField.getText());

					getDungeon().setTurn(turn);
					getDungeon().turn();

					getWindow().hide();
				} catch (NumberFormatException e) {
					new MessageWindow(getRenderer(), getStage(), getSkin(), "Error", "Invalid turn number.").show();
				}
			}
		});
		getWindow().getContentTable().add(turnSetButton).width(50f);
		getWindow().getContentTable().row();

		// ------------

		getWindow().getContentTable().add(new Label("Effect: ", getSkin(), "windowStyle"));

		final SelectBox effectSelectBox = new SelectBox(getSkin());
		effectSelectBox.setItems("Injured Foot", "Strained Leg");
		effectSelectBox.setMaxListCount(4);
		getWindow().getContentTable().add(effectSelectBox).left().width(100f);

		final TextField effectLengthField = new TextField("", getSkin());
		getWindow().getContentTable().add(effectLengthField).left().width(50f);

		Button effectAddButton = new TextButton("Add", getSkin());
		effectAddButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				try {
					int length = Integer.parseInt(effectLengthField.getText());

					switch (effectSelectBox.getSelectedIndex()) {
						case 0:
							getDungeon().getPlayer().addStatusEffect(
								new InjuredFoot(getDungeon(), getDungeon().getPlayer(), length)
							);
							break;
						case 1:
							getDungeon().getPlayer().addStatusEffect(
								new StrainedLeg(getDungeon(), getDungeon().getPlayer(), length)
							);
							break;
					}

					getDungeon().turn();

					getWindow().hide();
				} catch (NumberFormatException e) {
					new MessageWindow(getRenderer(), getStage(), getSkin(), "Error", "Invalid length.").show();
				}
			}
		});
		getWindow().getContentTable().add(effectAddButton).width(50f);
		getWindow().getContentTable().row();


		// ------------

		getWindow().getContentTable().add(new Label("Entity: ", getSkin(), "windowStyle"));

		final SelectBox entitySelectBox = new SelectBox(getSkin());
		entitySelectBox.setItems("Jackal", "Fish", "Pufferfish");
		entitySelectBox.setMaxListCount(4);
		getWindow().getContentTable().add(entitySelectBox).left().width(100f);

		Button entitySpawnButton = new TextButton("Spawn", getSkin());
		entitySpawnButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				switch (entitySelectBox.getSelectedIndex()) {
					case 0:
						MonsterJackal jackal = new MonsterJackal(
							getLevel().getDungeon(), getLevel(),
							getDungeon().getPlayer().getX(), getDungeon().getPlayer().getY()
						);

						getLevel().addEntity(jackal);
						break;
					case 1:
						MonsterFish fish = new MonsterFish(
							getLevel().getDungeon(), getLevel(),
							getDungeon().getPlayer().getX(), getDungeon().getPlayer().getY(),
							Utils.randomFrom(MonsterFish.FishColour.values())
						);

						getLevel().addEntity(fish);
						break;
					case 2:
						MonsterPufferfish pufferfish = new MonsterPufferfish(
							getLevel().getDungeon(), getLevel(),
							getDungeon().getPlayer().getX(), getDungeon().getPlayer().getY()
						);

						getLevel().addEntity(pufferfish);
						break;
				}

				getDungeon().turn();

				getWindow().hide();
			}
		});
		getWindow().getContentTable().add(entitySpawnButton).width(50f);
		getWindow().getContentTable().row();

		// ------------

		getWindow().getContentTable().add(new Label("Damage: ", getSkin(), "windowStyle"));

		final TextField damageAmountField = new TextField("", getSkin());
		getWindow().getContentTable().add(damageAmountField).left().width(100f);

		Button damageButton = new TextButton("Damage", getSkin());
		damageButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				try {
					int amount = Integer.parseInt(damageAmountField.getText());

					Player player = getDungeon().getPlayer();

					java.util.List<Entity> ents = getLevel().getEntitiesAt(player.getX(), player.getY());

					ents.stream()
						.filter(e -> e instanceof LivingEntity && !(e instanceof Player))
						.forEach(e -> ((LivingEntity) e).damage(DamageSource.UNKNOWN, amount, player, true));

					getDungeon().turn();
					getWindow().hide();
				} catch (NumberFormatException e) {
					new MessageWindow(getRenderer(), getStage(), getSkin(), "Error", "Invalid amount.").show();
				}
			}
		});
		getWindow().getContentTable().add(damageButton).width(50f);
		getWindow().getContentTable().row();

		Button seeAllButton = new TextButton("See all", getSkin());
		seeAllButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				getLevel().seeAll();
			}
		});
		getWindow().getContentTable().add(seeAllButton).width(50f);

		Button takeSnap = new TextButton("Take Level Snapshot", getSkin());
		takeSnap.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Pixmap snapshot = getRenderer().takeLevelSnapshot();
				String path = Paths.get(System.getProperty("java.io.tmpdir")).resolve("jrogue_level_snap.png").toString();
				PixmapIO.writePNG(Gdx.files.absolute(path), snapshot);
				snapshot.dispose();
				new MessageWindow(getRenderer(), getStage(), getSkin(), "Screenshot", "Saved to [BLUE]" + path + "[]").show();
			}
		});
		getWindow().getContentTable().add(takeSnap).width(125f);
		getWindow().getContentTable().row();
	}
}
