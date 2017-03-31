package jr.rendering.components.hud.windows;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import java.util.ArrayList;
import java.util.List;

public class Window extends Dialog {
	private PopupWindow owner;
	
	private List<ResultListener> resultListeners = new ArrayList<>();
	
	public Window(String title, Skin skin, PopupWindow owner) {
		super(title, skin);
		
		init();
		this.owner = owner;
	}
	
	private void init() {
		Button closeButton = new Button(getSkin(), "windowCloseButton");
		
		closeButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Window.this.hide();
			}
		});
		
		getTitleTable().add(closeButton).size(18, 18).padRight(-3).padTop(0);
	}
	
	public Window(String title, Skin skin, String styleName, PopupWindow owner) {
		super(title, skin, styleName);
		
		init();
		this.owner = owner;
	}
	
	public Window(String title, WindowStyle style, PopupWindow owner) {
		super(title, style);
		
		init();
		this.owner = owner;
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		setClip(true);
		super.draw(batch, parentAlpha);
		setClip(false);
	}
	
	@Override
	public void hide(Action action) {
		super.hide(action);
		
		owner.remove();
	}
	
	@Override
	protected void result(Object result) {
		super.result(result);
		
		resultListeners.forEach(l -> l.onResult(result));
	}
	
	public void addResultListener(ResultListener listener) {
		resultListeners.add(listener);
	}
	
	public interface ResultListener {
		void onResult(Object result);
	}
}
