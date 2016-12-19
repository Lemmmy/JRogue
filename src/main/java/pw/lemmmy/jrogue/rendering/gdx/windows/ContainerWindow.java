package pw.lemmmy.jrogue.rendering.gdx.windows;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import pw.lemmmy.jrogue.dungeon.entities.Container;
import pw.lemmmy.jrogue.dungeon.entities.Entity;
import pw.lemmmy.jrogue.dungeon.entities.LivingEntity;
import pw.lemmmy.jrogue.dungeon.items.Item;
import pw.lemmmy.jrogue.rendering.gdx.GDXRenderer;
import pw.lemmmy.jrogue.rendering.gdx.items.ItemMap;
import pw.lemmmy.jrogue.rendering.gdx.items.ItemRenderer;

public class ContainerWindow extends PopupWindow {
	private Entity entity;

	public ContainerWindow(GDXRenderer renderer, Stage stage, Skin skin, Entity entity) {
		super(renderer, stage, skin, entity.getDungeon(), entity.getLevel());

		this.entity = entity;
	}

	@Override
	public String getTitle() {
		return entity.getContainer().isPresent() ? entity.getContainer().get().getName() : "Container";
	}

	@Override
	public void populateWindow() {
		if (!entity.getContainer().isPresent()) {
			return;
		}

		Container container = entity.getContainer().get();
		boolean isLivingEntity = entity instanceof LivingEntity;
		LivingEntity livingEntity = isLivingEntity ? (LivingEntity) entity : null;

		getWindow().setWidth(300);
		getWindow().setHeight(400);

		Table mainTable = new Table(getSkin());
		ScrollPane scrollPane = new ScrollPane(mainTable, getSkin());

		scrollPane.setFillParent(true);

		container.getItems().forEach((character, itemStack) -> { // TODO: categorical item grouping
			Item item = itemStack.getItem();
			Button itemButton = new Button(getSkin(), "inventory");
			Table itemTable = new Table();

			ItemRenderer renderer = ItemMap.valueOf(item.getAppearance().name()).getRenderer();

			String suffix = "";

			if (isLivingEntity) {
				if (
					livingEntity.getRightHand() != null && livingEntity.getLeftHand() != null &&
					livingEntity.getRightHand().getLetter() == character &&
					livingEntity.getLeftHand().getLetter() == character
				) {
					suffix = " [P_GREY_3](in both hands)[]";
				} else if (livingEntity.getRightHand() != null && livingEntity.getRightHand().getLetter() == character) {
					suffix = " [P_GREY_3](in right hand)[]";
				} else if (livingEntity.getLeftHand() != null && livingEntity.getLeftHand().getLetter() == character) {
					suffix = " [P_GREY_3](in left hand)[]";
				}
			}

			itemTable.add(new Image(renderer.getDrawable(itemStack, item)))
					 .left().padRight(6);
			itemTable.add(new Label("[P_GREY_3]" + character.toString(), getSkin(), "windowStyleMarkup"))
					 .left().padRight(6);
			itemTable.add(new Label("[BLACK]" + itemStack.getName(true) + suffix, getSkin(), "windowStyleMarkup"))
					 .growX().left().row();

			itemButton.add(itemTable)
					  .left().width(291);

			mainTable.add(itemButton)
					 .left().width(294).padTop(1).row();
		});

		getWindow().getContentTable().add(scrollPane);
	}
}
