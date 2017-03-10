package jr.rendering.gdx.components.hud.windows;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.player.Player;
import jr.dungeon.entities.utils.EntityHelper;
import jr.rendering.gdx.GDXRenderer;
import jr.rendering.gdx.components.hud.windows.components.ContainerComponent;
import lombok.val;

import java.util.Optional;

public class ContainerWindow extends PopupWindow {
	private Entity entity;
	private Player player;
	
	public ContainerWindow(GDXRenderer renderer, Stage stage, Skin skin, Entity entity) {
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
		getWindow().setSize(560, 400);

		val ec = EntityHelper.getContainer(entity);
		String containerName = ec.isPresent() ? ec.get().getName() : "Container";
		String inventoryName = player.getContainer().isPresent() ? player.getContainer().get().getName() : "Inventory";
		
		getWindow().getContentTable().padTop(4);
		getWindow().getContentTable().add(new Label(containerName, getSkin(), "windowStyle"));
		getWindow().getContentTable().add(new Label("", getSkin(), "windowStyle"));
		getWindow().getContentTable().add(new Label(inventoryName, getSkin(), "windowStyle"));
		getWindow().getContentTable().row();
		
		ContainerComponent containerComponent = new ContainerComponent(getSkin(), entity, player, true);
		ScrollPane containerScrollPane = new ScrollPane(containerComponent, getSkin());
		getWindow().getContentTable().add(containerScrollPane).left().top();
		
		Container<Actor> splitter = new Container<>();
		splitter.setBackground(getSkin().getDrawable("grey4"));
		getWindow().getContentTable().add(splitter).left().top().bottom().growY();
		
		ContainerComponent inventoryContainerComponent = new ContainerComponent(getSkin(), player, entity, true);
		ScrollPane inventoryScrollPane = new ScrollPane(inventoryContainerComponent, getSkin());
		getWindow().getContentTable().add(inventoryScrollPane).left().top();
		
		containerComponent.addRelatedComponent(inventoryContainerComponent);
		inventoryContainerComponent.addRelatedComponent(containerComponent);
	}
}
