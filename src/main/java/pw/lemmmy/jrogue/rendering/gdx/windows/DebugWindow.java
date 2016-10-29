package pw.lemmmy.jrogue.rendering.gdx.windows;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.entities.effects.InjuredFoot;
import pw.lemmmy.jrogue.dungeon.entities.effects.StrainedLeg;

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
		getWindow().setWidth(300f);

		getWindow().getContentTable().add(new Label("Turn: ", getSkin(), "windowStyle"));

		final TextField turnField = new TextField("", getSkin());
		getWindow().getContentTable().add(turnField).top().left().width(100f);

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
		getWindow().getContentTable().add(turnSetButton).top().right();
		getWindow().getContentTable().row();

		// ------------

		getWindow().getContentTable().add(new Label("Effect: ", getSkin(), "windowStyle"));

		final SelectBox effectSelectBox = new SelectBox(getSkin());
		effectSelectBox.setItems("Injured Foot", "Strained Leg");
		effectSelectBox.setMaxListCount(4);
		getWindow().getContentTable().add(effectSelectBox).left().width(100f);

		final TextField effectLengthField = new TextField("", getSkin());
		getWindow().getContentTable().add(effectLengthField).left().width(30f);

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
		getWindow().getContentTable().add(effectAddButton).right();
		getWindow().getContentTable().row();
	}
}
