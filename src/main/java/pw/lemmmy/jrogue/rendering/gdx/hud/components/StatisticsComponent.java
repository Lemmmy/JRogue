package pw.lemmmy.jrogue.rendering.gdx.hud.components;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import pw.lemmmy.jrogue.dungeon.entities.player.Attribute;
import pw.lemmmy.jrogue.dungeon.entities.player.Player;
import pw.lemmmy.jrogue.rendering.gdx.utils.ImageLoader;

public class StatisticsComponent extends Table {
	private Player player;
	
	public StatisticsComponent(Skin skin, Player player) {
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
		
		addSeparator(this);
		
		Table attributesTable = new Table();
		addAvailableLabel(attributesTable);
		player.getAttributes().getAttributeMap().forEach((a, l) -> addAttribute(attributesTable, a, l));
		add(attributesTable).row();
	}
	
	private void addSeparator(Table container) {
		Container<Actor> splitter = new Container<>();
		splitter.setBackground(getSkin().getDrawable("grey4"));
		container.add(splitter).growX().pad(8, 0, 8, 0).row();
	}
	
	private void addArmourClass(Table container) {
		container.add(new Image(ImageLoader.getImageFromSheet("hud.png", 15, 2, 16, 16, false))).padRight(6);
		
		String label = String.format("[P_GREY_0]Armour Class:  [P_GREEN_1]%,d[][]", player.getArmourClass());
		container.add(new Label(label, getSkin(), "windowStyleMarkup")).width(243).left();
	}
	
	private void addAvailableLabel(Table container) {
		String label = String.format(
			"[P_GREY_2]Skill points available:  [%s]%,d[][]",
			player.getSpendableSkillPoints() == 0 ? "P_RED" : "P_GREEN_1",
			player.getSpendableSkillPoints()
		);
		
		container.add(new Label(label, getSkin(), "windowStyleMarkup")).colspan(3).padBottom(16);
		container.row().padBottom(4);
	}
	
	private void addAttribute(Table container, Attribute attribute, int level) {
		boolean canSpend = player.getAttributes().canIncrementAttribute(attribute, player);
		
		int sheetX = attribute.ordinal();
		container.add(new Image(ImageLoader.getImageFromSheet("hud.png", sheetX, 2, 16, 16, false))).padRight(6);
		
		String label = String.format("[P_GREY_0]%s:  [P_GREEN_1]%,d[][]", attribute.getName(), level);
		container.add(new Label(label, getSkin(), "windowStyleMarkup")).width(180).left();
		
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
		
		container.add(button).width(60).right();
		container.row().padBottom(4);
	}
}
