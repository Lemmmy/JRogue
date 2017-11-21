package jr.rendering.base.ui.windows;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.player.Player;
import jr.dungeon.entities.utils.EntityHelper;
import jr.language.transformers.Capitalise;
import jr.rendering.base.components.hud.HUDComponent;
import jr.rendering.base.ui.partials.AttributesPartial;
import jr.rendering.base.ui.partials.ContainerPartial;
import jr.rendering.base.ui.partials.StatsPartial;
import lombok.val;

public class PlayerWindow extends WindowBase {
	private Entity entity;
	
	public PlayerWindow(HUDComponent renderer, Stage stage, Skin skin, Entity entity) {
		super(renderer, stage, skin, entity.getDungeon(), entity.getLevel());
		
		this.entity = entity;
	}
	
	@Override
	public String getTitle() {
		return entity instanceof Player ? entity.getName(null).build(Capitalise.first) : "Player";
	}
	
	@Override
	public void populateWindow() {
		getWindowBorder().setWidth(592);
		getWindowBorder().setHeight(400);

		val ec = EntityHelper.getContainer(entity);
		String inventoryName = ec.isPresent() ? ec.get().getName() : "Inventory";
		
		getWindowBorder().getContentTable().padTop(4);
		getWindowBorder().getContentTable().add(new Label("Statistics", getSkin(), "windowStyle"));
		getWindowBorder().getContentTable().add(new Label(inventoryName, getSkin(), "windowStyle"));
		getWindowBorder().getContentTable().row();
		
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
			getWindowBorder().getContentTable().add(tempScrollPane).growY().width(276);
		}
		
		ContainerPartial inventoryComponent = new ContainerPartial(getSkin(), entity, null, true);
		Table inventoryTable = new Table();
		inventoryTable.add(inventoryComponent).left().top();
		ScrollPane inventoryScrollPane = new ScrollPane(inventoryTable, getSkin(), "lowered");
		getWindowBorder().getContentTable().add(inventoryScrollPane).growY().left().top();
		inventoryTable.top();
	}
}
