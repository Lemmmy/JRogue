package jr.rendering.ui.partials;

import com.badlogic.gdx.scenes.scene2d.ui.*;
import jr.dungeon.entities.player.Attribute;
import jr.dungeon.entities.player.Attributes;
import jr.rendering.ui.skin.UIAttributeIcons;
import jr.rendering.ui.utils.FunctionalClickListener;

public class AttributesPartial extends Table {
	private Attributes attributes;
	private boolean canDecrement;
	
	public AttributesPartial(Skin skin, Attributes attributes, boolean canDecrement) {
		super(skin);
		
		this.attributes = attributes;
		this.canDecrement = canDecrement;
		
		update();
	}
	
	public void update() {
		clearChildren();
		
		if (attributes.getAttributeMap().values().stream().mapToInt(Integer::intValue).sum() > 0) {
			populate();
		}
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
		container.add(UIAttributeIcons.getImage(getSkin(), attribute)).padRight(6);
		
		String label = String.format("[WHITE]%s:  [P_GREEN_3]%,d[][]", attribute.getName(), level);
		container.add(new Label(label, getSkin(), "windowStyleMarkup")).width(180).left();
		
		if (!canDecrement) {
			addSpendButton(container, attribute, level);
		} else {
			addAdjustButtons(container, attribute, level);
		}
		
		container.row().padBottom(4);
	}
	
	private void addSpendButton(Table container, Attribute attribute, int level) {
		boolean canSpend = attributes.canIncrementAttribute(attribute);
		
		Button button = new TextButton("Spend", getSkin());
		button.setDisabled(!canSpend);
		button.addListener(new FunctionalClickListener((fcl, event, x, y) -> {
			attributes.incrementAttribute(attribute);
			update();
		}));
		
		container.add(button).width(60).right();
	}
	
	private void addAdjustButtons(Table container, Attribute attribute, int level) {
		boolean canIncrement = attributes.canIncrementAttribute(attribute);
		boolean canDecrement = attributes.canDecrementAttribute(attribute);
		
		Button decrement = new TextButton("-", getSkin());
		decrement.setDisabled(!canDecrement);
		decrement.addListener(new FunctionalClickListener((fcl, event, x, y) -> {
			attributes.decrementAttribute(attribute);
			update();
		}));
		
		container.add(decrement).width(22).padRight(4).right();
		
		Button increment = new TextButton("+", getSkin());
		increment.setDisabled(!canIncrement);
		increment.addListener(new FunctionalClickListener((fcl, event, x, y) -> {
			attributes.incrementAttribute(attribute);
			update();
		}));
		
		container.add(increment).width(22).right();
	}
}
