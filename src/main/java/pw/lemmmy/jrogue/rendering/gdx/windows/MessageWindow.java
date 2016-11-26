package pw.lemmmy.jrogue.rendering.gdx.windows;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import pw.lemmmy.jrogue.rendering.gdx.GDXRenderer;

public class MessageWindow extends PopupWindow {
	private String title;
	private String message;

	public MessageWindow(GDXRenderer renderer, Stage stage, Skin skin, String title, String message) {
		super(renderer, stage, skin, null, null);

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
