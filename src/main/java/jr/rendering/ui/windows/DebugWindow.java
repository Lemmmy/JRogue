package jr.rendering.ui.windows;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.rendering.screens.GameScreen;

public class DebugWindow extends PopupWindow {
	public DebugWindow(GameScreen renderer, Stage stage, Skin skin, Dungeon dungeon, Level level) {
		super(renderer, stage, skin, dungeon, level);
	}
	
	@Override
	public String getTitle() {
		return "Debug Tools";
	}
	
	@Override
	public void populateWindow() {
		getWindow().setWidth(350f);
		getWindow().setHeight(250f);
		
		getWindow().getContentTable()
			.add(new Label(
				String.format("Nutrition: %,d", getDungeon().getPlayer().getNutrition()),
				getSkin(),
				"windowStyle"
			));
		getWindow().getContentTable().row();
		
		getWindow().getContentTable().add(
			new Label(
				String.format("Pos: %d, %d", getDungeon().getPlayer().getX(), getDungeon().getPlayer().getY()),
				getSkin(),
				"windowStyle"
			)
		);
		getWindow().getContentTable().row();
		
		Button seeAllButton = new TextButton("See all", getSkin());
		seeAllButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				getLevel().visibilityStore.seeAll();
			}
		});
		getWindow().getContentTable().add(seeAllButton).width(50f);
	}
}
