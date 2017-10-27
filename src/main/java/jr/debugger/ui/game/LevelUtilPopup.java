package jr.debugger.ui.game;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import jr.JRogue;
import jr.debugger.ui.DebugUI;
import jr.dungeon.Dungeon;
import jr.dungeon.entities.Entity;
import jr.dungeon.tiles.Tile;
import jr.language.transformers.Capitalise;
import jr.rendering.base.ui.utils.FunctionalClickListener;
import jr.rendering.gdx2d.entities.EntityMap;
import jr.rendering.gdx2d.entities.EntityRenderer;
import jr.rendering.gdx2d.tiles.TileMap;
import jr.rendering.gdx2d.tiles.TileRenderer;

public class LevelUtilPopup extends Table {
	private FunctionalClickListener stageClickListener;
	
	private DebugUI ui;
	private GameWidget gameWidget;
	private Skin skin;
	
	private Dungeon dungeon;
	private int worldX, worldY;
	
	public LevelUtilPopup(DebugUI ui, GameWidget gameWidget, Skin skin, Dungeon dungeon, int worldX, int worldY) {
		super(skin);
		
		this.ui = ui;
		this.gameWidget = gameWidget;
		this.skin = skin;
		
		this.dungeon = dungeon;
		this.worldX = worldX;
		this.worldY = worldY;
		
		initialise();
	}
	
	private void initialise() {
		setBackground("blackTransparent");
		
		Table innerContainer = new Table();
		
		initialiseCoordsLabel(innerContainer);
		initialiseButtons(innerContainer);
		
		add(innerContainer).pad(2);
		pack();
	}
	
	private void initialiseCoordsLabel(Table container) {
		addLabel(container, String.format("%,d[P_GREY_3],[] %,d", worldX, worldY));
	}
	
	private void initialiseButtons(Table container) {
		initialiseTileButton(container);
		initialiseEntityButtons(container);
	}
	
	private void initialiseTileButton(Table container) {
		try {
			Tile tile = dungeon.getLevel().tileStore.getTile(worldX, worldY);
			TileRenderer tr = TileMap.valueOf(tile.getType().name()).getRenderer();
			TextureRegion region = new TextureRegion(tr.getTextureRegion(dungeon, worldX, worldY));
			region.flip(false, true);
			
			addButton(container, new TextureRegionDrawable(region), tile.getType().name());
		} catch (Exception e) {
			addLabel(container, "[P_RED]Tile err[]");
			JRogue.getLogger().error(e);
		}
	}
	
	private void initialiseEntityButtons(Table container) {
		dungeon.getLevel().entityStore.getEntitiesAt(worldX, worldY)
			.forEach(e -> initialiseEntityButton(container, e));
	}
	
	private void initialiseEntityButton(Table container, Entity entity) {
		try {
			EntityRenderer er = EntityMap.valueOf(entity.getAppearance().name()).getRenderer();
			TextureRegion region = new TextureRegion(er.getTextureRegion(dungeon, entity));
			region.flip(false, true);
			
			addButton(container, new TextureRegionDrawable(region), entity.getName(null).build(Capitalise.first));
		} catch (Exception e) {
			addLabel(container, "[P_RED]Entity err[]");
			JRogue.getLogger().error(e);
		}
	}
	
	private void addButton(Table container, Drawable icon, String text) {
		Button button = new Button(skin);
		Table buttonTable = new Table();
		Image iconImage = new Image(icon);
		
		buttonTable.add(iconImage).size(16, 16).left().padRight(4);
		buttonTable.add(new Label(text, skin)).growX().left();
		
		button.add(buttonTable).left().growX();
		container.add(button).left().growX().row();
	}
	
	private void addLabel(Table container, String text) {
		container.add(new Label(text, skin)).left().fillX().padBottom(2).row();
	}
	
	@Override
	protected void setStage(Stage stage) {
		if (stageClickListener != null && getStage() != null) {
			getStage().removeListener(stageClickListener);
		}
		
		super.setStage(stage);
		
		if (stage != null) {
			stage.addListener(stageClickListener = new FunctionalClickListener((fcl, event, x, y) -> {
				Actor a = event.getTarget();
				
				if (a == null) return;
				
				boolean hitMe = false;
				
				while (a != null) {
					if (a == this) {
						hitMe = true;
						break;
					}
					
					a = a.getParent();
				}
				
				if (!hitMe) remove();
			}));
		}
	}
	
	@Override
	public boolean remove() {
		getStage().removeListener(stageClickListener);
		stageClickListener = null;
		gameWidget.setLevelUtilPopup(null);
		
		return super.remove();
	}
}
