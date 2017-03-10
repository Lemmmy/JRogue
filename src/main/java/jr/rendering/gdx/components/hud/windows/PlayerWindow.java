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
import jr.rendering.gdx.components.hud.windows.components.StatisticsComponent;
import lombok.val;

public class PlayerWindow extends PopupWindow {
	private Entity entity;
	
	public PlayerWindow(GDXRenderer renderer, Stage stage, Skin skin, Entity entity) {
		super(renderer, stage, skin, entity.getDungeon(), entity.getLevel());
		
		this.entity = entity;
	}
	
	@Override
	public String getTitle() {
		return entity instanceof Player ? entity.getName((Player) entity, true) : "Player";
	}
	
	@Override
	public void populateWindow() {
		getWindow().setWidth(600);
		getWindow().setHeight(400);

		val ec = EntityHelper.getContainer(entity);
		String inventoryName = ec.isPresent() ? ec.get().getName() : "Inventory";
		
		getWindow().getContentTable().padTop(4);
		getWindow().getContentTable().add(new Label("Statistics", getSkin(), "windowStyle"));
		getWindow().getContentTable().add(new Label("", getSkin(), "windowStyle"));
		getWindow().getContentTable().add(new Label(inventoryName, getSkin(), "windowStyle"));
		getWindow().getContentTable().row();
		
		if (entity instanceof Player) {
			StatisticsComponent statisticsComponent = new StatisticsComponent(getSkin(), (Player) entity);
			ScrollPane statisticsScrollPane = new ScrollPane(statisticsComponent, getSkin());
			getWindow().getContentTable().add(statisticsScrollPane).width(276).left().top().padRight(8);
		}
		
		Container<Actor> splitter = new Container<>();
		splitter.setBackground(getSkin().getDrawable("grey4"));
		getWindow().getContentTable().add(splitter).left().top().bottom().growY();
		
		ContainerComponent inventoryComponent = new ContainerComponent(getSkin(), entity, null, true);
		ScrollPane inventoryScrollPane = new ScrollPane(inventoryComponent, getSkin());
		getWindow().getContentTable().add(inventoryScrollPane).left().top().padLeft(8);
	}
}
