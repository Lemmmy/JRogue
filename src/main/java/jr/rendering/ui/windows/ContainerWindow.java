package jr.rendering.ui.windows;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.player.Player;
import jr.dungeon.entities.utils.EntityHelper;
import jr.rendering.components.hud.HUDComponent;
import jr.rendering.ui.partials.ContainerPartial;
import lombok.val;

public class ContainerWindow extends WindowBase {
	private Entity entity;
	private Player player;
	
	public ContainerWindow(HUDComponent hud, Stage stage, Skin skin, Entity entity) {
		super(hud, stage, skin, entity.getDungeon(), entity.getLevel());
		
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
		getWindowBorder().setSize(580, 400);

		val ec = EntityHelper.getContainer(entity);
		String containerName = ec.isPresent() ? ec.get().getName() : "Container";
		String inventoryName = player.getContainer().isPresent() ? player.getContainer().get().getName() : "Inventory";
		
		getWindowBorder().getContentTable().padTop(4);
		getWindowBorder().getContentTable().add(new Label(containerName, getSkin(), "windowStyle"));
		getWindowBorder().getContentTable().add(new Label(inventoryName, getSkin(), "windowStyle"));
		getWindowBorder().getContentTable().row();
		
		ContainerPartial containerPartial = new ContainerPartial(getSkin(), entity, player, true);
		Table containerTable = new Table();
		containerTable.add(containerPartial).left().top();
		ScrollPane containerScrollPane = new ScrollPane(containerTable, getSkin(), "lowered");
		getWindowBorder().getContentTable().add(containerScrollPane).growY().left().top();
		containerTable.top();
		
		ContainerPartial inventoryContainerPartial = new ContainerPartial(getSkin(), player, entity, true);
		Table inventoryTable = new Table();
		inventoryTable.add(inventoryContainerPartial).left().top();
		ScrollPane inventoryScrollPane = new ScrollPane(inventoryTable, getSkin(), "lowered");
		getWindowBorder().getContentTable().add(inventoryScrollPane).growY().left().top();
		inventoryTable.top();
		
		containerPartial.addRelatedComponent(inventoryContainerPartial);
		inventoryContainerPartial.addRelatedComponent(containerPartial);
	}
}
