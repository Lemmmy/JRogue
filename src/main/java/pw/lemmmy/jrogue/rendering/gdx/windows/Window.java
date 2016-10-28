package pw.lemmmy.jrogue.rendering.gdx.windows;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class Window extends com.badlogic.gdx.scenes.scene2d.ui.Dialog {
	private Button closeButton;

	public Window(String title, Skin skin) {
		super(title, skin);

		init();
	}

	public Window(String title, Skin skin, String styleName) {
		super(title, skin, styleName);

		init();
	}

	public Window(String title, WindowStyle style) {
		super(title, style);

		init();
	}

	private void init() {
		closeButton = new Button(getSkin(), "windowCloseButton");
		closeButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Window.this.hide();
			}
		});

		getTitleTable().add(closeButton).size(18, 18).padRight(-3).padTop(0);
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		setClip(true);
		super.draw(batch, parentAlpha);
		setClip(false);
	}
}
