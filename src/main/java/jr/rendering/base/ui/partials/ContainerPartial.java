package jr.rendering.base.ui.partials;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.containers.Container;
import jr.dungeon.entities.player.Player;
import jr.dungeon.entities.utils.EntityHelper;
import jr.dungeon.items.Item;
import jr.rendering.base.ui.utils.FunctionalClickListener;
import jr.rendering.gdx2d.items.ItemMap;
import jr.rendering.gdx2d.items.ItemRenderer;
import lombok.val;

import java.util.ArrayList;
import java.util.List;

public class ContainerPartial extends Table {
	private Entity entity;
	private Entity transferTo;
	private boolean showHandedness;
	
	private List<ContainerPartial> relatedComponents = new ArrayList<>();
	
	public ContainerPartial(Skin skin, Entity entity, Entity transferTo) {
		this(skin, entity, transferTo, false);
	}
	
	public ContainerPartial(Skin skin, Entity entity, Entity transferTo, boolean showHandedness) {
		super(skin);
		
		this.entity = entity;
		this.transferTo = transferTo;
		this.showHandedness = showHandedness;
		
		update();
	}
	
	public void addRelatedComponent(ContainerPartial component) {
		relatedComponents.add(component);
	}
	
	private void update() {
		clearChildren();
		
		if (EntityHelper.hasContainer(entity)) {
			showContainer();
		} else {
			showNoContainer();
		}
	}
	
	private void updateRelated() {
		relatedComponents.forEach(ContainerPartial::update);
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
		val containerOpt = EntityHelper.getContainer(entity);

		if (!containerOpt.isPresent()) {
			return;
		}

		Container container = containerOpt.get();

		boolean isPlayer = entity instanceof Player;
		
		if (container.getItems().size() == 0) {
			showNoItems();
		}
		
		container.getItems().forEach((character, itemStack) -> { // TODO: categorical item grouping
			Item item = itemStack.getItem();
			Button itemButton = new Button(getSkin(), "containerEntry");
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
			)).left().width(16);
			itemTable.add(new Label(
				"[WHITE]" + itemStack.getName(entity.getDungeon().getPlayer()) + suffix,
				getSkin(),
				"windowStyleMarkup"
			)).growX().left();
			itemTable.row();
			
			if (transferTo != null) {
				itemButton.addListener(new FunctionalClickListener((fcl, event, x, y) -> {
					val transferContainerOpt = EntityHelper.getContainer(transferTo);
					if (!transferContainerOpt.isPresent()) {
						return;
					}
					
					int amount = event.getButton() == Input.Buttons.LEFT ? itemStack.getCount() : 1;
					
					Container destContainer = transferContainerOpt.get();
					container.transfer(
						destContainer,
						character,
						amount,
						isPlayer ? entity.getDungeon().getPlayer() : null
					);
					
					update();
					updateRelated();
				}));
			}
			
			itemButton.add(itemTable).left().width(266);
			
			add(itemButton).left().width(269).padTop(1).padRight(1).row();
		});
	}
}
