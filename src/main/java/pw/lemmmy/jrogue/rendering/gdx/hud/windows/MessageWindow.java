package pw.lemmmy.jrogue.rendering.gdx.hud.windows;

import com.badlogic.gdx.Input;
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
		Label label = new Label("[P_GREY_0]" + message + "[]", getSkin(), "windowStyleMarkup");
		label.setWrap(true);
		getWindow().getContentTable().add(label).pad(16).prefWidth(350);
		getWindow().button("OK");
		getWindow().key(Input.Keys.ENTER, true);
		getWindow().setModal(false);
		getWindow().pack();
	}
}
