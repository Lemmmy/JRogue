package jr.rendering.ui.windows;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import jr.dungeon.entities.player.Player;
import jr.rendering.components.hud.HUDComponent;
import jr.rendering.ui.partials.SpellPartial;

public class SpellWindow extends WindowBase {
	private Player player;
	
	public SpellWindow(HUDComponent hud, Stage stage, Skin skin, Player player) {
		super(hud, stage, skin, player.getDungeon(), player.getLevel());
		
		this.player = player;
	}
	
	@Override
	public String getTitle() {
		return "Spells";
	}
	
	@Override
	public void populateWindow() {
		getWindowBorder().setSize(400, 150);
		
		SpellPartial spellPartial = new SpellPartial(getSkin(), getWindowBorder(), player);
		ScrollPane spellScrollPane = new ScrollPane(spellPartial, getSkin());
		getWindowBorder().getContentTable().add(spellScrollPane).width(396).left().top().growY();
	}
}
