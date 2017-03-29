package jr.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import jr.JRogue;
import jr.dungeon.Dungeon;
import jr.dungeon.entities.player.roles.Role;
import jr.rendering.components.hud.HUDSkin;
import jr.rendering.entities.RoleMap;
import org.apache.commons.lang3.StringUtils;

public class CharacterCreationScreen extends ScreenAdapter {
	private Skin skin;
	private Stage stage;
	
	private GameAdapter game;
	
	private TextField nameField;
	private Table roleContainer;
	private TextButton createButton;
	
	private Role selectedRole;
	
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
		initClassButtons(container);
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
		
		container.add(nameTable).growX().padBottom(8).row();
	}
	
	private void initClassButtons(Table container) {
		container.add(new Label("Role", skin)).left().padBottom(8).row();
		
		roleContainer = new Table();
		
		for (RoleMap roleMap : RoleMap.values()) {
			try {
				Role roleInstance = roleMap.getRoleClass().newInstance();
				
				TextureRegion roleTexture = roleMap.getRoleTexture();
				TextureRegionDrawable roleDrawable = new TextureRegionDrawable(roleTexture);
				Image roleImage = new Image(roleDrawable);
				
				Button roleButton = new Button(skin);
				Table roleTable = new Table();
				roleTable.add(roleImage).size(32, 32).row();
				roleTable.add(new Label(roleInstance.getName(), skin)).pad(4, 4, 0, 4);
				roleButton.add(roleTable);
				
				roleButton.addListener(new ChangeListener() {
					@Override
					public void changed(ChangeEvent event, Actor actor) {
						roleContainer.getChildren().forEach(c -> {
							if (c instanceof TextButton) {
								((TextButton) c).setDisabled(false);
							}
						});
						
						roleButton.setDisabled(true);
						selectedRole = roleInstance;
						createButton.setDisabled(false);
					}
				});
				
				roleContainer.add(roleButton);
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		
		container.add(roleContainer).growX().left().row();
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
		
		createButton = new TextButton("Create", skin);
		createButton.setDisabled(true);
		createButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				JRogue.getSettings().setPlayerName(nameField.getText());
				JRogue.getSettings().setRole(selectedRole);
				
				game.setScreen(new GameScreen(game, Dungeon.load()), new SlidingTransition(
					SlidingTransition.Direction.LEFT,
					false,
					Interpolation.pow4
				), 1f);
			}
		});
		buttonTable.add(createButton).right().bottom();
		
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
