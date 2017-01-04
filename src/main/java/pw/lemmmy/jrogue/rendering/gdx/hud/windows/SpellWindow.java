package pw.lemmmy.jrogue.rendering.gdx.hud.windows;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import pw.lemmmy.jrogue.dungeon.entities.player.Player;
import pw.lemmmy.jrogue.rendering.gdx.GDXRenderer;
import pw.lemmmy.jrogue.rendering.gdx.hud.components.SpellComponent;

public class SpellWindow extends PopupWindow {
	private Player player;
	
	public SpellWindow(GDXRenderer renderer, Stage stage, Skin skin, Player player) {
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
		
		SpellComponent spellComponent = new SpellComponent(getSkin(), getWindow(), player);
		ScrollPane spellScrollPane = new ScrollPane(spellComponent, getSkin());
		getWindow().getContentTable().add(spellScrollPane).width(396).left().top().growY();
	}
}
