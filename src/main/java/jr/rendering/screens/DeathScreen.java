package jr.rendering.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
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
import jr.rendering.SlidingTransition;
import jr.rendering.components.hud.HUDSkin;
import jr.rendering.utils.HUDUtils;

public class DeathScreen extends ScreenAdapter {
	private Skin skin;
	private Stage stage;
	
	private GameAdapter game;
	private Dungeon dungeon;
	private EntityDeathEvent event;
	
	public DeathScreen(GameAdapter game, Dungeon dungeon, EntityDeathEvent event) {
		this.game = game;
		this.dungeon = dungeon;
		this.event = event;
		
		skin = new HUDSkin();
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
		initLog(container);
		initBottomButtons(container);
		
		container.top().pad(32);
		
		stage.addActor(container);
	}
	
	private void initDeathMessage(Table container) {
		DamageSource source = event.getDamageSource();
		DamageType type = source.getType();
		
		String deathMessage = type.getDeathStringPastTense() != null ? type.getDeathStringPastTense() : "You died.";
		
		Label deathMessageLabel = new Label("[P_RED]" + deathMessage, skin, "large");
		deathMessageLabel.setAlignment(Align.center);
		container.add(deathMessageLabel).top().padBottom(8).row();
		
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
	
	private void initLog(Table container) {
		Table logTable = new Table(skin);
		
		dungeon.getLogHistory().forEach(entry -> {
			entry = HUDUtils.replaceMarkupString(entry);
			logTable.add(entry).left().growX().row();
		});
		
		ScrollPane logScrollPane = new ScrollPane(logTable, skin);
		container.add(logScrollPane).expand().growX().padBottom(8).row();
		logScrollPane.setScrollPercentY(100);
	}
	
	private void initBottomButtons(Table container) {
		Table buttonTable = new Table();
		
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
		
		stage.getViewport().update(width, height);
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
