package jr.rendering.ui.windows;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import jr.dungeon.entities.player.Player;
import jr.rendering.screens.GameScreen;
import jr.rendering.ui.partials.SpellPartial;

public class SpellWindow extends PopupWindow {
	private Player player;
	
	public SpellWindow(GameScreen renderer, Stage stage, Skin skin, Player player) {
		super(renderer, stage, skin, player.getDungeon(), player.getLevel());
		
		this.player = player;
	}
	
	@Override
	public String getTitle() {
		return "Spells";
	}
	
	@Override
	public void populateWindow() {
		getWindow().setWidth(400);
		getWindow().setHeight(150);
		
		SpellPartial spellPartial = new SpellPartial(getSkin(), getWindow(), player);
		ScrollPane spellScrollPane = new ScrollPane(spellPartial, getSkin());
		getWindow().getContentTable().add(spellScrollPane).width(396).left().top().growY();
	}
}
