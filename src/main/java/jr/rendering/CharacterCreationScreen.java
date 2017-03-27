package jr.rendering;

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
import jr.rendering.components.hud.HUDSkin;
import org.apache.commons.lang3.StringUtils;

public class CharacterCreationScreen extends ScreenAdapter {
	private Skin skin;
	private Stage stage;
	
	private GameAdapter game;
	
	private TextField nameField;
	
	public CharacterCreationScreen(GameAdapter game) {
		this.game = game;
		
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
		
		initTitle(container);
		initNameField(container);
		container.add(new Container<>()).expand().row();
		initBottomButtons(container);
		
		container.top().pad(32);
		
		stage.addActor(container);
	}
	
	private void initTitle(Table container) {
		container.add(new Label("Character Creation", skin, "large")).fillX().left().padBottom(8);
		container.row();
	}
	
	private void initNameField(Table container) {
		Table nameTable = new Table();
		
		nameTable.add(new Label("Name", skin)).left().padRight(8);
		nameField = new TextField(StringUtils.capitalize(JRogue.getSettings().getPlayerName()), skin);
		nameTable.add(nameField).growX().left().row();
		nameField.setTextFieldFilter((textField, c) -> Character.isLetter(c) && textField.getText().length() < 20);
		nameField.setTextFieldListener((textField, c) -> {
			int p = nameField.getCursorPosition();
			nameField.setText(StringUtils.capitalize(textField.getText()));
			nameField.setCursorPosition(p);
		});
		nameField.setProgrammaticChangeEvents(false);
		
		container.add(nameTable).growX().row();
	}
	
	private void initBottomButtons(Table container) {
		Table buttonTable = new Table();
		
		TextButton tempReloadButton = new TextButton("Reload", skin);
		tempReloadButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				stage.clear();
				initLayout(new Table(skin));
			}
		});
		buttonTable.add(tempReloadButton).right().bottom().padRight(8);
		
		TextButton goButton = new TextButton("Create", skin);
		goButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				JRogue.getSettings().setPlayerName(nameField.getText());
				
				game.setScreen(new GameScreen(game, Dungeon.load()), new SlidingTransition(
					SlidingTransition.Direction.LEFT,
					false,
					Interpolation.pow4
				), 1f);
			}
		});
		buttonTable.add(goButton).right().bottom();
		
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
