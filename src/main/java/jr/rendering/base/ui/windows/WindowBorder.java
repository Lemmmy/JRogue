package jr.rendering.base.ui.windows;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import java.util.ArrayList;
import java.util.List;

public class WindowBorder extends Dialog {
	private Window owner;
	
	private List<ResultListener> resultListeners = new ArrayList<>();
	
	public WindowBorder(String title, Skin skin, Window owner) {
		super(title, skin);
		
		initialise();
		this.owner = owner;
	}
	
	protected void initialise() {
		Button closeButton = new Button(getSkin(), "windowCloseButton");
		
		closeButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				WindowBorder.this.hide();
			}
		});
		
		getTitleTable().getCell(getTitleLabel()).padLeft(-3).padTop(-2);
		getTitleTable().add(closeButton).size(18, 18).padRight(-4).padTop(-4);
	}
	
	public WindowBorder(String title, Skin skin, String styleName, Window owner) {
		super(title, skin, styleName);
		
		initialise();
		this.owner = owner;
	}
	
	public WindowBorder(String title, WindowStyle style, Window owner) {
		super(title, style);
		
		initialise();
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
