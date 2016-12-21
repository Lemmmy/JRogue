package pw.lemmmy.jrogue.rendering.gdx.windows;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import pw.lemmmy.jrogue.dungeon.entities.Entity;
import pw.lemmmy.jrogue.rendering.gdx.GDXRenderer;

public class InventoryWindow extends PopupWindow {
	private Entity entity;

	public InventoryWindow(GDXRenderer renderer, Stage stage, Skin skin, Entity entity) {
		super(renderer, stage, skin, entity.getDungeon(), entity.getLevel());

		this.entity = entity;
	}

	@Override
	public String getTitle() {
		return entity.getContainer().isPresent() ? entity.getContainer().get().getName() : "Container";
	}

	@Override
	public void populateWindow() {
		getWindow().setWidth(300);
		getWindow().setHeight(400);

		ContainerComponent inventoryContainerComponent = new ContainerComponent(getSkin(), entity, true);
		ScrollPane scrollPane = new ScrollPane(inventoryContainerComponent, getSkin());
		scrollPane.setFillParent(true);
		getWindow().getContentTable().add(scrollPane);
	}
}
