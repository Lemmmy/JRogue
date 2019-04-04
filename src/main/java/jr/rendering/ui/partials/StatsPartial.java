package jr.rendering.ui.partials;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import jr.dungeon.entities.player.Player;
import jr.rendering.utils.ImageLoader;

public class StatsPartial extends Table {
	private Player player;
	
	public StatsPartial(Skin skin, Player player) {
		super(skin);
		
		this.player = player;
		update();
	}
	
	private void update() {
		clearChildren();
		populate();
	}
	
	private void populate() {
		Table statsTable = new Table();
		addArmourClass(statsTable);
		add(statsTable).row();
	}
	
	
	private void addArmourClass(Table container) {
		container.add(new Image(ImageLoader.getImageFromSheet("textures/hud.png", 15, 10, 16, 16))).padRight(6).padTop(8);
		
		String label = String.format("[WHITE]Armour Class:  [P_GREEN_3]%,d[][]", player.getArmourClass());
		container.add(new Label(label, getSkin(), "windowStyleMarkup")).width(243).left().padTop(8);
	}
}
