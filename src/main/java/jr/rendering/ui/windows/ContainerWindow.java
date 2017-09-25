package jr.rendering.ui.windows;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.player.Player;
import jr.dungeon.entities.utils.EntityHelper;
import jr.rendering.screens.GameScreen;
import jr.rendering.ui.partials.ContainerPartial;
import lombok.val;

public class ContainerWindow extends PopupWindowBase {
	private Entity entity;
	private Player player;
	
	public ContainerWindow(GameScreen renderer, Stage stage, Skin skin, Entity entity) {
		super(renderer, stage, skin, entity.getDungeon(), entity.getLevel());
		
		this.entity = entity;
		player = entity.getDungeon().getPlayer();
	}
	
	@Override
	public String getTitle() {
		val ec = EntityHelper.getContainer(entity);
		return ec.isPresent() ? ec.get().getName() : "Container";
	}
	
	@Override
	public void populateWindow() {
		getWindow().setSize(580, 400);

		val ec = EntityHelper.getContainer(entity);
		String containerName = ec.isPresent() ? ec.get().getName() : "Container";
		String inventoryName = player.getContainer().isPresent() ? player.getContainer().get().getName() : "Inventory";
		
		getWindow().getContentTable().padTop(4);
		getWindow().getContentTable().add(new Label(containerName, getSkin(), "windowStyle"));
		getWindow().getContentTable().add(new Label(inventoryName, getSkin(), "windowStyle"));
		getWindow().getContentTable().row();
		
		ContainerPartial containerPartial = new ContainerPartial(getSkin(), entity, player, true);
		Table containerTable = new Table();
		containerTable.add(containerPartial).left().top();
		ScrollPane containerScrollPane = new ScrollPane(containerTable, getSkin(), "lowered");
		getWindow().getContentTable().add(containerScrollPane).growY().left().top();
		containerTable.top();
		
		ContainerPartial inventoryContainerPartial = new ContainerPartial(getSkin(), player, entity, true);
		Table inventoryTable = new Table();
		inventoryTable.add(inventoryContainerPartial).left().top();
		ScrollPane inventoryScrollPane = new ScrollPane(inventoryTable, getSkin(), "lowered");
		getWindow().getContentTable().add(inventoryScrollPane).growY().left().top();
		inventoryTable.top();
		
		containerPartial.addRelatedComponent(inventoryContainerPartial);
		inventoryContainerPartial.addRelatedComponent(containerPartial);
	}
}
