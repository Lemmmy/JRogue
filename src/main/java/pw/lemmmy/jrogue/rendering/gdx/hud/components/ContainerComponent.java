package pw.lemmmy.jrogue.rendering.gdx.hud.components;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import pw.lemmmy.jrogue.dungeon.entities.Entity;
import pw.lemmmy.jrogue.dungeon.entities.containers.Container;
import pw.lemmmy.jrogue.dungeon.entities.player.Player;
import pw.lemmmy.jrogue.dungeon.items.Item;
import pw.lemmmy.jrogue.rendering.gdx.items.ItemMap;
import pw.lemmmy.jrogue.rendering.gdx.items.ItemRenderer;

import java.util.ArrayList;
import java.util.List;

public class ContainerComponent extends Table {
	private Entity entity;
	private Entity transferTo;
	private boolean showHandedness;
	
	private List<ContainerComponent> relatedComponents = new ArrayList<>();
	
	public ContainerComponent(Skin skin, Entity entity, Entity transferTo) {
		this(skin, entity, transferTo, false);
	}
	
	public ContainerComponent(Skin skin, Entity entity, Entity transferTo, boolean showHandedness) {
		super(skin);
		
		this.entity = entity;
		this.transferTo = transferTo;
		this.showHandedness = showHandedness;
		
		update();
	}
	
	public void addRelatedComponent(ContainerComponent component) {
		relatedComponents.add(component);
	}
	
	private void update() {
		clearChildren();
		
		if (entity.getContainer().isPresent()) {
			showContainer();
		} else {
			showNoContainer();
		}
	}
	
	private void updateRelated() {
		relatedComponents.forEach(ContainerComponent::update);
	}
	
	private void showNoContainer() {
		Label label = new Label("No container.", getSkin(), "windowStyle");
		label.setWrap(true);
		add(label).left().width(262).pad(4).row();
	}
	
	private void showNoItems() {
		Label label = new Label("No items.", getSkin(), "windowStyle");
		label.setWrap(true);
		add(label).left().width(262).pad(4).row();
	}
	
	private void showContainer() {
		if (!entity.getContainer().isPresent()) {
			return;
		}
		
		Container container = entity.getContainer().get();
		boolean isPlayer = entity instanceof Player;
		
		if (container.getItems().size() == 0) {
			showNoItems();
		}
		
		container.getItems().forEach((character, itemStack) -> { // TODO: categorical item grouping
			Item item = itemStack.getItem();
			Button itemButton = new Button(getSkin(), "inventory");
			Table itemTable = new Table();
			
			ItemRenderer renderer = ItemMap.valueOf(item.getAppearance().name()).getRenderer();
			
			String suffix = "";
			
			if (isPlayer && showHandedness) {
				Player player = (Player) entity;
				
				if (
					player.getRightHand() != null && player.getLeftHand() != null &&
						player.getRightHand().getLetter() == character &&
						player.getLeftHand().getLetter() == character
					) {
					suffix = " [P_GREY_3](in both hands)[]";
				} else if (player.getRightHand() != null && player.getRightHand().getLetter() == character) {
					suffix = " [P_GREY_3](in right hand)[]";
				} else if (player.getLeftHand() != null && player.getLeftHand().getLetter() == character) {
					suffix = " [P_GREY_3](in left hand)[]";
				}
			}
			
			itemTable.add(new Image(renderer.getDrawable(itemStack, item))).left().padRight(6);
			itemTable.add(new Label(
				"[P_GREY_3]" + character.toString(),
				getSkin(),
				"windowStyleMarkup"
			)).left().padRight(6);
			itemTable.add(new Label(
				"[BLACK]" + itemStack.getName(true) + suffix,
				getSkin(),
				"windowStyleMarkup"
			)).growX().left();
			itemTable.row();
			
			if (transferTo != null) {
				itemButton.addListener(new ClickListener() {
					@Override
					public void clicked(InputEvent event, float x, float y) {
						if (!transferTo.getContainer().isPresent()) {
							return;
						}
						
						int amount = event.getButton() == Input.Buttons.LEFT ? itemStack.getCount() : 1;
						
						Container destContainer = transferTo.getContainer().get();
						container.transfer(
							destContainer,
							character,
							amount,
							isPlayer ? entity.getDungeon().getPlayer() : null
						);
						
						update();
						updateRelated();
					}
				});
			}
			
			itemButton.add(itemTable).left().width(267);
			
			add(itemButton).left().width(270).padTop(1).row();
		});
	}
}
