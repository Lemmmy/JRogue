package jr.rendering.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import jr.JRogue;
import jr.dungeon.Dungeon;
import jr.dungeon.entities.DamageSource;
import jr.dungeon.entities.DamageType;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.events.EntityDeathEvent;
import jr.dungeon.entities.player.Player;
import jr.dungeon.items.Item;
import jr.rendering.GameAdapter;
import jr.rendering.screens.utils.SlidingTransition;
import jr.rendering.ui.partials.ContainerPartial;
import jr.rendering.ui.partials.DungeonOverviewPartial;
import jr.rendering.ui.skin.UISkin;
import jr.rendering.utils.HUDUtils;

public class DeathScreen extends ScreenAdapter {
	private Skin skin;
	private Stage stage;
	
	private GameAdapter game;
	private Dungeon dungeon;
	private EntityDeathEvent event;
	
	private Cell<? extends Actor> screenCell;
	private Table logScreen, inventoryScreen, dungeonScreen;
	
	public DeathScreen(GameAdapter game, Dungeon dungeon, EntityDeathEvent event) {
		this.game = game;
		this.dungeon = dungeon;
		this.event = event;
		
		skin = UISkin.instance;
		ScreenViewport stageViewport = new ScreenViewport();
		stageViewport.setUnitsPerPixel(1f / JRogue.getSettings().getHudScale());
		stage = new Stage(stageViewport);
		
		initLayout(new Table(skin));
		
		Gdx.graphics.setTitle(GameAdapter.WINDOW_TITLE);
	}
	
	private void initLayout(Table container) {
		container.setFillParent(true);
		container.row().fill().top();
		
		initDeathMessage(container);
		
		Container<Actor> splitter = new Container<>();
		splitter.setBackground(skin.get("splitterHorizontalRaised", NinePatchDrawable.class));
		container.add(splitter).growX().minHeight(2).pad(16, 0, 4, 0).row();
		
		Table buttonContainer = new Table();
		container.add(buttonContainer).left().pad(8, 0, 8, 0).row();
		
		screenCell = container.add(new Actor()).left().top().grow().padBottom(8);
		container.row();
		
		initScreenTabs(buttonContainer);
		
		initBottomButtons(container);
		
		container.top().pad(32);
		
		stage.addActor(container);
	}
	
	private void initScreenTabs(Table container) {
		ButtonGroup<TextButton> screenTabs = new ButtonGroup<>();
		screenTabs.setMaxCheckCount(1);
		screenTabs.setMinCheckCount(0);
		screenTabs.setUncheckLast(true);
		
		initScreenTab(container, screenTabs, "Log", logScreen = new Table());
		initLogScreen(logScreen);
		
		Player player = dungeon.getPlayer();
		
		player.getContainer().ifPresent(inventory -> {
			initScreenTab(container, screenTabs, "Inventory", inventoryScreen = new Table());
			initInventoryScreen(inventoryScreen);
		});
		
		initScreenTab(container, screenTabs, "Dungeon", dungeonScreen = new Table());
		initDungeonScreen(dungeonScreen);
		
		switchScreen(logScreen);
		screenTabs.getButtons().get(0).setChecked(true);
	}
	
	private void initScreenTab(Table container, ButtonGroup<TextButton> group, String name, Table screen) {
		TextButton button = new TextButton(name, skin, "checkable");
		button.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (!button.isChecked()) return;
				
				switchScreen(screen);
			}
		});
		
		group.add(button);
		container.add(button).left().padRight(8);
	}
	
	private void switchScreen(Table screen) {
		screenCell.setActor(screen);
		screenCell.expand();
	}
	
	private void initDeathMessage(Table container) {
		DamageSource source = event.getDamageSource();
		DamageType type = source.getType();
		
		String deathMessage = type.getDeathStringPastTense() != null ? type.getDeathStringPastTense() : "You died.";
		
		Label deathMessageLabel = new Label("[P_RED]" + deathMessage, skin, "large");
		deathMessageLabel.setAlignment(Align.center);
		container.add(deathMessageLabel).top().padBottom(8).row();
		
		Label deathLocationLabel = new Label(String.format(
			"[P_RED]Died in [P_ORANGE_3]%s[], in [P_ORANGE_3]%s[].",
			dungeon.getLevel().toString(),
			dungeon.getName()
		), skin);
		deathLocationLabel.setAlignment(Align.center);
		container.add(deathLocationLabel).top().padBottom(4).row();
		
		String deathCauseString = "";
		
		if (source.getAttacker() != null && !(source.getAttacker() instanceof Player)) {
			Entity attacker = source.getAttacker();
			
			if (source.getItem() != null) {
				Item item = source.getItem();
				
				deathCauseString = String.format(
					"Killed by [WHITE]%s[]'s [WHITE]%s[].",
					attacker.getName(dungeon.getPlayer(), true),
					item.getName(dungeon.getPlayer(), true, false)
				);
			} else {
				deathCauseString = String.format(
					"Killed by [WHITE]%s[].",
					attacker.getName(dungeon.getPlayer(), true)
				);
			}
		} else if (source.getItem() != null) {
			Item item = source.getItem();
			
			deathCauseString = String.format(
				"Killed by [WHITE]%s[].",
				item.getName(dungeon.getPlayer(), true, false)
			);
		}
		
		if (!deathCauseString.isEmpty()) {
			Label deathCauseLabel = new Label("[P_ORANGE_0]" + deathCauseString, skin);
			deathCauseLabel.setAlignment(Align.center);
			container.add(deathCauseLabel).top().padBottom(8).row();
		}
	}
	
	private void initLogScreen(Table container) {
		Table logTable = new Table(skin);
		
		dungeon.getLogHistory().forEach(entry -> {
			entry = HUDUtils.replaceMarkupString(entry);
			logTable.add(entry).left().growX().row();
		});
		
		logTable.top().left();
		
		ScrollPane logScrollPane = new ScrollPane(logTable, skin);
		container.add(logScrollPane).bottom().left().grow().row();
		logScrollPane.layout();
		logScrollPane.setScrollPercentY(100);
	}
	
	private void initInventoryScreen(Table container) {
		Table inventoryTable = new Table(skin);
		
		ContainerPartial inventoryPartial = new ContainerPartial(skin, dungeon.getPlayer(), null);
		inventoryTable.add(inventoryPartial).top().left().row();
		inventoryTable.top().left();
		
		ScrollPane inventoryScrollPane = new ScrollPane(inventoryTable, skin);
		container.add(inventoryScrollPane).top().left().grow().row();
	}
	
	private void initDungeonScreen(Table container) {
		DungeonOverviewPartial dungeonOverview = new DungeonOverviewPartial(skin, dungeon);
		ScrollPane dungeonOverviewScrollPane = new ScrollPane(dungeonOverview, skin);
		container.add(dungeonOverviewScrollPane).top().left().grow().row();
	}
	
	private void initBottomButtons(Table container) {
		Table buttonTable = new Table();
		
		TextButton tempReloadButton = new TextButton("Reload (temporary)", skin);
		tempReloadButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				stage.clear();
				initLayout(new Table(skin));
			}
		});
		buttonTable.add(tempReloadButton).right().bottom().padRight(8);
		
		TextButton newCharacterButton = new TextButton("New character", skin);
		newCharacterButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.setScreen(new CharacterCreationScreen(game), new SlidingTransition(
					SlidingTransition.Direction.UP,
					false,
					Interpolation.pow4
				), 1f);
			}
		});
		buttonTable.add(newCharacterButton).right().bottom();
		
		buttonTable.align(Align.right);
		container.add(buttonTable).growX().row();
	}
	
	@Override
	public void render(float delta) {
		super.render(delta);
		
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act(delta);
		stage.draw();
	}
	
	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		
		stage.getViewport().update(width, height, true);
	}
	
	@Override
	public void dispose() {
		super.dispose();
		
		stage.dispose();
		skin.dispose();
	}
	
	@Override
	public void show() {
		super.show();
		
		Gdx.input.setInputProcessor(stage);
	}
}
