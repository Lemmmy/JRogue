package pw.lemmmy.jrogue.rendering.gdx.windows;

import com.badlogic.gdx.scenes.scene2d.ui.*;
import pw.lemmmy.jrogue.dungeon.entities.*;
import pw.lemmmy.jrogue.dungeon.entities.Container;
import pw.lemmmy.jrogue.dungeon.items.Item;
import pw.lemmmy.jrogue.rendering.gdx.items.ItemMap;
import pw.lemmmy.jrogue.rendering.gdx.items.ItemRenderer;

public class ContainerComponent extends Table {
	private Entity entity;
	private boolean showHandedness;

	public ContainerComponent(Skin skin, Entity entity) {
		this(skin, entity, false);
	}

	public ContainerComponent(Skin skin, Entity entity, boolean showHandedness) {
		super(skin);

		this.entity = entity;
		this.showHandedness = showHandedness;

		if (entity.getContainer().isPresent()) {
			showContainer();
		} else {
			showNoContainer();
		}
	}

	private void showNoContainer() {
		Label label = new Label("No container.", getSkin(), "windowStyle");
		label.setWrap(true);
		add(label);
	}

	private void showContainer() {
		if (!entity.getContainer().isPresent()) {
			return;
		}

		Container container = entity.getContainer().get();
		boolean isPlayer = entity instanceof Player;

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

			itemButton.add(itemTable).left().width(291);

			add(itemButton).left().width(294).padTop(1).row();
		});
	}
}
