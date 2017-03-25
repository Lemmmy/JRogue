package jr.rendering.components.hud.windows.partials;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import jr.dungeon.entities.player.Attribute;
import jr.dungeon.entities.player.Player;
import jr.rendering.utils.ImageLoader;

public class AttributesPartial extends Table {
	private Player player;
	
	public AttributesPartial(Skin skin, Player player) {
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
		splitter.setBackground(getSkin().get("splitterHorizontalDarkLowered", NinePatchDrawable.class));
		container.add(splitter).growX().pad(8, 4, 8, 4 ).row();
	}
	
	private void addArmourClass(Table container) {
		container.add(new Image(ImageLoader.getImageFromSheet("textures/hud.png", 15, 10, 16, 16, false))).padRight(6).padTop(8);
		
		String label = String.format("[WHITE]Armour Class:  [P_GREEN_3]%,d[][]", player.getArmourClass());
		container.add(new Label(label, getSkin(), "windowStyleMarkup")).width(243).left().padTop(8);
	}
	
	private void addAvailableLabel(Table container) {
		String label = String.format(
			"[P_GREY_4]Skill points available:  [%s]%,d[][]",
			player.getSpendableSkillPoints() == 0 ? "P_RED" : "P_GREEN_3",
			player.getSpendableSkillPoints()
		);
		
		container.add(new Label(label, getSkin(), "windowStyleMarkup")).colspan(3).padBottom(16);
		container.row().padBottom(4);
	}
	
	private void addAttribute(Table container, Attribute attribute, int level) {
		boolean canSpend = player.getAttributes().canIncrementAttribute(attribute, player);
		
		int sheetX = attribute.ordinal();
		container.add(new Image(ImageLoader.getImageFromSheet("textures/hud.png", sheetX, 10, 16, 16, false))).padRight(6);
		
		String label = String.format("[WHITE]%s:  [P_GREEN_3]%,d[][]", attribute.getName(), level);
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
