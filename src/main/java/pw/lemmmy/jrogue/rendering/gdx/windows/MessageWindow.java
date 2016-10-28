package pw.lemmmy.jrogue.rendering.gdx.windows;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class MessageWindow extends PopupWindow {
	private String title;
	private String message;

	public MessageWindow(Stage stage, Skin skin, String title, String message) {
		super(stage, skin, null, null);

		this.title = title;
		this.message = message;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public void populateWindow() {
		getWindow().text(message, getSkin().get("windowStyle", Label.LabelStyle.class));
		getWindow().button("OK");
	}
}
