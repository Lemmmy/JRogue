package jr.rendering.components.hud;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import jr.Settings;
import jr.dungeon.Dungeon;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.events.EntityAttackMissedEvent;
import jr.dungeon.entities.events.EntityEnergyChangedEvent;
import jr.dungeon.entities.events.EntityHealthChangedEvent;
import jr.dungeon.entities.player.Player;
import jr.dungeon.events.DungeonEventHandler;
import jr.dungeon.events.DungeonEventInvocationTime;
import jr.dungeon.events.DungeonEventListener;
import jr.rendering.GameScreen;
import jr.rendering.tiles.TileMap;
import jr.utils.Point;

public class TextPopups implements DungeonEventListener {
	private static final float TEXT_POPUP_DURATION = 0.4f;
	
	public HUDComponent hudComponent;
	
	private Dungeon dungeon;
	private Stage stage;
	private GameScreen renderer;
	private Settings settings;
	
	public TextPopups(HUDComponent hudComponent) {
		this.hudComponent = hudComponent;
		
		dungeon = hudComponent.dungeon;
		stage = hudComponent.getStage();
		renderer = hudComponent.renderer;
		settings = hudComponent.settings;
	}
	
	@DungeonEventHandler
	private void onEntityHealthChanged(EntityHealthChangedEvent e) {
		Entity entity = e.getEntity();
		int delta = e.getNewHealth() - e.getOldHealth();
		boolean positive = delta >= 0;
		
		showTextPopup(entity.getPosition(), String.format(
			"[%s]%s%,d HP[]",
			entity instanceof Player ? positive ? "P_GREEN_2" : "P_ORANGE_2" : positive ? "P_GREEN_1" : "P_RED",
			positive ? "+" : "-",
			Math.abs(delta)
		));
	}
	
	@DungeonEventHandler(invocationTime = DungeonEventInvocationTime.TURN_COMPLETE)
	public void onPlayerEnergyChanged(EntityEnergyChangedEvent e) {
		Entity entity = e.getEntity();
		int delta = e.getNewEnergy() - e.getOldEnergy();
		boolean positive = delta >= 0;
		
		showTextPopup(entity.getPosition(), String.format(
			"[%s]%s%,d MP[]",
			positive ? "P_PURPLE_3" : "P_PURPLE_1",
			positive ? "+" : "-",
			Math.abs(delta)
		));
	}
	
	@DungeonEventHandler
	private void onEntityAttackMissed(EntityAttackMissedEvent e) {
		showTextPopup(e.getAttacker().getPosition(), "[P_ORANGE_2]missed[]");
	}
	
	private void showTextPopup(Point worldPos, String text) {
		if (dungeon.getLevel().getVisibilityStore().isTileInvisible(worldPos)) {
			return;
		}
		
		int setting = settings.getTextPopup();
		
		if (setting == 0) {
			return;
		}
		
		Vector3 pos = renderer.getCamera().project(
			new Vector3((worldPos.getX() + 0.5f) * TileMap.TILE_WIDTH, worldPos.getY() * TileMap.TILE_HEIGHT, 0)
		);
		
		Table table = new Table(hudComponent.getSkin());
		table.add(new Label(text, hudComponent.getSkin(), setting == 1 ? "default" : "large"));
		
		stage.getRoot().addActor(table);
		table.pack();
		table.setPosition((int) pos.x - (int) (table.getWidth() / 2), (int) pos.y);
		hudComponent.getSingleTurnActors().add(table);
		
		table.addAction(Actions.moveTo(table.getX(), table.getY() + TileMap.TILE_HEIGHT / 2, TEXT_POPUP_DURATION));
		table.addAction(Actions.sequence(
			Actions.delay(TEXT_POPUP_DURATION / 2),
			Actions.fadeOut(TEXT_POPUP_DURATION))
		);
	}
}
