package jr.rendering.components.hud.windows;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.player.Player;
import jr.dungeon.entities.utils.EntityHelper;
import jr.rendering.Renderer;
import jr.rendering.components.hud.windows.partials.ContainerPartial;
import lombok.val;

public class ContainerWindow extends PopupWindow {
	private Entity entity;
	private Player player;
	
	public ContainerWindow(Renderer renderer, Stage stage, Skin skin, Entity entity) {
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
		
		ContainerPartial containerPartial = new ContainerPartial(getSkin(), entity, player, true);
		ScrollPane containerScrollPane = new ScrollPane(containerPartial, getSkin());
		getWindow().getContentTable().add(containerScrollPane).left().top();
		
		Container<Actor> splitter = new Container<>();
		splitter.setBackground(getSkin().getDrawable("grey4"));
		getWindow().getContentTable().add(splitter).left().top().bottom().growY();
		
		ContainerPartial inventoryContainerPartial = new ContainerPartial(getSkin(), player, entity, true);
		ScrollPane inventoryScrollPane = new ScrollPane(inventoryContainerPartial, getSkin());
		getWindow().getContentTable().add(inventoryScrollPane).left().top();
		
		containerPartial.addRelatedComponent(inventoryContainerPartial);
		inventoryContainerPartial.addRelatedComponent(containerPartial);
	}
}
