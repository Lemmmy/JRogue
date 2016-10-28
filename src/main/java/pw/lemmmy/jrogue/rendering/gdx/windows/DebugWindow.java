package pw.lemmmy.jrogue.rendering.gdx.windows;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.Level;

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

		final TextField turnField = new TextField("", getSkin());

		getWindow().getContentTable().add(new Label("Turn: ", getSkin(), "windowStyle"));

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
	}
}
