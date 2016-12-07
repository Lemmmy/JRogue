package pw.lemmmy.jrogue.rendering.gdx.windows;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.entities.Player;
import pw.lemmmy.jrogue.dungeon.items.Item;
import pw.lemmmy.jrogue.rendering.gdx.GDXRenderer;
import pw.lemmmy.jrogue.rendering.gdx.items.ItemMap;
import pw.lemmmy.jrogue.rendering.gdx.items.ItemRenderer;

public class InventoryWindow extends PopupWindow {
	public InventoryWindow(GDXRenderer renderer, Stage stage, Skin skin, Dungeon dungeon, Level level) {
		super(renderer, stage, skin, dungeon, level);
	}

	@Override
	public String getTitle() {
		return "Inventory";
	}

	@Override
	public void populateWindow() {
		getWindow().setWidth(300f);
		getWindow().setHeight(400f);

		// getWindow().setDebug(true);

		Table mainTable = new Table(getSkin());
		ScrollPane scrollPane = new ScrollPane(mainTable, getSkin());

		scrollPane.setFillParent(true);

		Player player = getDungeon().getPlayer();

		player.getInventory().forEach((character, itemStack) -> { // TODO: categorical item grouping
			Item item = itemStack.getItem();
			Table itemTable = new Table();

			ItemRenderer renderer = ItemMap.valueOf(item.getAppearance().name()).getRenderer();

			String suffix = "";

			if (player.getRightHand() == itemStack && player.getLeftHand() == itemStack) {
				suffix = " [P_GREY_3](in both hands)[]";
			} else if (player.getRightHand() == itemStack) {
				suffix = " [P_GREY_3](in right hand)[]";
			} else if (player.getLeftHand() == itemStack) {
				suffix = " [P_GREY_3](in left hand)[]";
			}

			itemTable.add(new Image(renderer.getDrawable(itemStack, item))).left().padRight(6);
			itemTable.add(new Label("[P_GREY_3]" + character.toString(), getSkin(), "windowStyleMarkup")).left()
					 .padRight(6);
			itemTable.add(new Label("[BLACK]" + itemStack.getName(true) + suffix, getSkin(), "windowStyleMarkup"))
					 .growX().left().row();

			mainTable.add(itemTable).left().width(294f).row();
		});

		getWindow().getContentTable().add(scrollPane);
	}
}
