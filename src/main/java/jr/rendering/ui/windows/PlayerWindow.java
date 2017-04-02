package jr.rendering.ui.windows;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.player.Player;
import jr.dungeon.entities.utils.EntityHelper;
import jr.rendering.ui.partials.AttributesPartial;
import jr.rendering.ui.partials.ContainerPartial;
import jr.rendering.ui.partials.StatsPartial;
import jr.rendering.screens.GameScreen;
import lombok.val;

public class PlayerWindow extends PopupWindow {
	private Entity entity;
	
	public PlayerWindow(GameScreen renderer, Stage stage, Skin skin, Entity entity) {
		super(renderer, stage, skin, entity.getDungeon(), entity.getLevel());
		
		this.entity = entity;
	}
	
	@Override
	public String getTitle() {
		return entity instanceof Player ? entity.getName((Player) entity, true) : "Player";
	}
	
	@Override
	public void populateWindow() {
		getWindow().setWidth(592);
		getWindow().setHeight(400);

		val ec = EntityHelper.getContainer(entity);
		String inventoryName = ec.isPresent() ? ec.get().getName() : "Inventory";
		
		getWindow().getContentTable().padTop(4);
		getWindow().getContentTable().add(new Label("Statistics", getSkin(), "windowStyle"));
		getWindow().getContentTable().add(new Label(inventoryName, getSkin(), "windowStyle"));
		getWindow().getContentTable().row();
		
		if (entity instanceof Player) {
			Table tempTable = new Table();
			ScrollPane tempScrollPane = new ScrollPane(tempTable, getSkin(), "lowered");
			
			StatsPartial statsPartial = new StatsPartial(getSkin(), (Player) entity);
			tempTable.add(statsPartial).width(276).left().top().row();
			
			Container<Actor> splitter = new Container<>();
			splitter.setBackground(getSkin().get("splitterHorizontalDarkLowered", NinePatchDrawable.class));
			tempTable.add(splitter).growX().pad(8, 4, 0, 4).row();
			
			AttributesPartial attributesPartial = new AttributesPartial(getSkin(), ((Player) entity).getAttributes(),
				false);
			tempTable.add(attributesPartial).width(276);
			
			tempTable.top();
			getWindow().getContentTable().add(tempScrollPane).growY().width(276);
		}
		
		ContainerPartial inventoryComponent = new ContainerPartial(getSkin(), entity, null, true);
		Table inventoryTable = new Table();
		inventoryTable.add(inventoryComponent).left().top();
		ScrollPane inventoryScrollPane = new ScrollPane(inventoryTable, getSkin(), "lowered");
		getWindow().getContentTable().add(inventoryScrollPane).growY().left().top();
		inventoryTable.top();
	}
}
