package jr.rendering.gdx2d.ui.utils;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class FunctionalClickListener extends ClickListener {
	private final FunctionalClickInterface fci;
	
	public FunctionalClickListener(FunctionalClickInterface fci) {
		this.fci = fci;
	}
	
	@Override
	public void clicked(InputEvent event, float x, float y) {
		if (this.fci != null) {
			this.fci.clicked(event, x, y);
		}
	}
	
	@FunctionalInterface
	public interface FunctionalClickInterface {
		void clicked(InputEvent event, float x, float y);
	}
}
