package pw.lemmmy.jrogue.rendering.gdx.windows;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.Level;
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

		getDungeon().getPlayer().getInventory().forEach((character, itemStack) -> {
			Table itemTable = new Table();

			ItemRenderer renderer = ItemMap.valueOf(itemStack.getItem().getAppearance().name()).getRenderer();

			itemTable.add(new Image(renderer.getDrawable(itemStack, itemStack.getItem()))).left().padRight(6);
			itemTable.add(new Label(itemStack.getName(true), getSkin(), "windowStyle")).left().growX().row();

			mainTable.add(itemTable).left().width(294f).row();
		});

		getWindow().getContentTable().add(scrollPane);
	}
}
