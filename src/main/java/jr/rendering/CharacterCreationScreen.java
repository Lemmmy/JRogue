package jr.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import jr.JRogue;
import jr.dungeon.Dungeon;
import jr.rendering.components.hud.HUDSkin;

public class CharacterCreationScreen extends ScreenAdapter {
	private Skin skin;
	private Stage stage;
	
	private GameAdapter game;
	
	public CharacterCreationScreen(GameAdapter game) {
		this.game = game;
		
		skin = new HUDSkin();
		ScreenViewport stageViewport = new ScreenViewport();
		stageViewport.setUnitsPerPixel(1f / JRogue.getSettings().getHudScale());
		stage = new Stage(stageViewport);
		
		initLayout(new Table(skin));
	}
	
	private void initLayout(Table container) {
		container.setFillParent(true);
		
		container.debug();
		
		container.row().fill().top();
		
		container.add(new Label("Test", skin)).fillX().left().pad(8);
		
		container.row();
		container.add(new Container<>()).expand().row();
		
		TextButton goButton = new TextButton("Go", skin);
		goButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.setScreen(new GameScreen(game, Dungeon.load()), new SlidingTransition(
					SlidingTransition.Direction.LEFT,
					false,
					Interpolation.pow4
				), 1f);
			}
		});
		container.add(goButton).right().bottom();
		
		container.top();
		
		stage.addActor(container);
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
