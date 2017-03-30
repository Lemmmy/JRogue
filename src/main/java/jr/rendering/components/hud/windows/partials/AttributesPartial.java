package jr.rendering.components.hud.windows.partials;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import jr.dungeon.entities.player.Attribute;
import jr.dungeon.entities.player.Attributes;
import jr.rendering.utils.ImageLoader;

public class AttributesPartial extends Table {
	private Attributes attributes;
	
	public AttributesPartial(Skin skin, Attributes attributes) {
		super(skin);
		
		this.attributes = attributes;
		
		update();
	}
	
	public void update() {
		clearChildren();
		populate();
	}
	
	private void populate() {
		Table attributesTable = new Table();
		addAvailableLabel(attributesTable);
		attributes.getAttributeMap().forEach((a, l) -> addAttribute(attributesTable, a, l));
		add(attributesTable).row();
	}
	
	private void addAvailableLabel(Table container) {
		String label = String.format(
			"[P_GREY_3]Skill points available:  [%s]%,d[][]",
			attributes.getSpendableSkillPoints() == 0 ? "P_RED" : "P_GREEN_3",
			attributes.getSpendableSkillPoints()
		);
		
		container.add(new Label(label, getSkin(), "windowStyleMarkup")).colspan(3).padTop(8).padBottom(8);
		container.row().padBottom(4);
	}
	
	private void addAttribute(Table container, Attribute attribute, int level) {
		boolean canSpend = attributes.canIncrementAttribute(attribute);
		
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
				
				attributes.incrementAttribute(attribute);
				update();
			}
		});
		
		container.add(button).width(60).right();
		container.row().padBottom(4);
	}
}
