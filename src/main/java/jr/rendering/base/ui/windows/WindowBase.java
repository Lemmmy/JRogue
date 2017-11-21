package jr.rendering.base.ui.windows;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.rendering.base.components.hud.HUDComponent;
import lombok.Getter;

@Getter
public abstract class WindowBase extends Window {
	private final Dungeon dungeon;
	private final Level level;
	private final HUDComponent hud;
	
	public WindowBase(HUDComponent hud, Stage stage, Skin skin, Dungeon dungeon, Level level) {
		super(stage, skin);
		
		this.hud = hud;
		this.dungeon = dungeon;
		this.level = level;
	}
	
	@Override
	public void show() {
		if (hud != null) hud.addWindow(this);
		super.show();
	}
	
	@Override
	protected void remove() {
		if (hud != null) hud.removeWindow(this);
	}
}
