package jr.rendering.ui.windows;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.rendering.screens.GameScreen;
import lombok.Getter;

@Getter
public abstract class WindowBase extends Window {
	private final Dungeon dungeon;
	private final Level level;
	private final GameScreen renderer;
	
	public WindowBase(GameScreen renderer, Stage stage, Skin skin, Dungeon dungeon, Level level) {
		super(stage, skin);
		
		this.renderer = renderer;
		this.dungeon = dungeon;
		this.level = level;
	}
	
	@Override
	public void show() {
		renderer.getHudComponent().addWindow(this);
		super.show();
	}
	
	@Override
	protected void remove() {
		renderer.getHudComponent().removeWindow(this);
	}
}
