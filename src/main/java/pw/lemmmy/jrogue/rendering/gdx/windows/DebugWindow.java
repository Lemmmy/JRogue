package pw.lemmmy.jrogue.rendering.gdx.windows;

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
import pw.lemmmy.jrogue.dungeon.entities.monsters.MonsterJackal;

public class DebugWindow extends PopupWindow {
	public DebugWindow(Stage stage, Skin skin, Dungeon dungeon, Level level) {
		super(stage, skin, dungeon, level);
	}

	@Override
	public String getTitle() {
		return "Debug Tools";
	}

	@Override
	public void populateWindow() {
		getWindow().setWidth(350f);

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
					new MessageWindow(getStage(), getSkin(), "Error", "Invalid turn number.").show();
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
					new MessageWindow(getStage(), getSkin(), "Error", "Invalid length.").show();
				}
			}
		});
		getWindow().getContentTable().add(effectAddButton).width(50f);
		getWindow().getContentTable().row();


		// ------------

		getWindow().getContentTable().add(new Label("Entity: ", getSkin(), "windowStyle"));

		final SelectBox entitySelectBox = new SelectBox(getSkin());
		entitySelectBox.setItems("Jackal");
		entitySelectBox.setMaxListCount(4);
		getWindow().getContentTable().add(entitySelectBox).left().width(100f);

		Button entitySpawnButton = new TextButton("Spawn", getSkin());
		entitySpawnButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				switch (entitySelectBox.getSelectedIndex()) {
					case 0:
						MonsterJackal monster = new MonsterJackal(
							getLevel().getDungeon(), getLevel(),
							getDungeon().getPlayer().getX(), getDungeon().getPlayer().getY()
						);

						getLevel().addEntity(monster);
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

					for (Entity entity : getLevel().getEntitiesAt(getDungeon().getPlayer().getX(), getDungeon().getPlayer().getY())) {
						if (entity instanceof LivingEntity && !(entity instanceof Player)) {
							((LivingEntity) entity).damage(DamageSource.UNKNOWN, amount, getDungeon().getPlayer(), true);
						}
					}

					getDungeon().turn();

					getWindow().hide();
				} catch (NumberFormatException e) {
					new MessageWindow(getStage(), getSkin(), "Error", "Invalid amount.").show();
				}
			}
		});
		getWindow().getContentTable().add(damageButton).width(50f);
		getWindow().getContentTable().row();
	}
}
