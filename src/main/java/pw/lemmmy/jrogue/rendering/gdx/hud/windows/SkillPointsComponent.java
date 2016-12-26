package pw.lemmmy.jrogue.rendering.gdx.hud.windows;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import pw.lemmmy.jrogue.dungeon.entities.player.Attribute;
import pw.lemmmy.jrogue.dungeon.entities.player.Player;
import pw.lemmmy.jrogue.rendering.gdx.utils.ImageLoader;

public class SkillPointsComponent extends Table {
	private Player player;
	
	public SkillPointsComponent(Skin skin, Player player) {
		super(skin);
		
		this.player = player;
		update();
	}
	
	private void update() {
		clearChildren();
		populate();
	}
	
	private void populate() {
		addAvailableLabel();
		
		player.getAttributes().getAttributeMap().forEach(this::addAttribute);
	}
	
	private void addAvailableLabel() {
		String label = String.format(
			"[P_GREY_2]Available:  [%s]%,d[][]",
			player.getSpendableSkillPoints() == 0 ? "P_RED" : "P_GREEN_1",
			player.getSpendableSkillPoints()
		);
		
		add(new Label(label, getSkin(), "windowStyleMarkup")).colspan(3).padBottom(16);
		
		row().padBottom(4);
	}
	
	private void addAttribute(Attribute attribute, int level) {
		boolean canSpend = player.getAttributes().canIncrementAttribute(attribute, player);
		
		int sheetX = attribute.ordinal();
		add(new Image(ImageLoader.getImageFromSheet("hud.png", sheetX, 2, 16, 16, false))).padRight(6);
		
		String label = String.format("[P_GREY_0]%s:  [P_GREEN_1]%,d[][]", attribute.getName(), level);
		add(new Label(label, getSkin(), "windowStyleMarkup")).width(180).left();
		
		Button button = new TextButton("Spend", getSkin());
		button.setDisabled(!canSpend);
		button.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				super.clicked(event, x, y);
				
				player.getAttributes().incrementAttribute(attribute, player);
				update();
			}
		});
		
		add(button).width(60).right();
		row().padBottom(4);
	}
}
