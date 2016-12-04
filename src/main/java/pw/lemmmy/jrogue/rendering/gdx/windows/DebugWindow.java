package pw.lemmmy.jrogue.rendering.gdx.windows;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.rendering.gdx.GDXRenderer;

import java.nio.file.Paths;

public class DebugWindow extends PopupWindow {
	public DebugWindow(GDXRenderer renderer, Stage stage, Skin skin, Dungeon dungeon, Level level) {
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

		getWindow().getContentTable().add(new Label(String.format("Nutrition: %,d", getDungeon().getPlayer().getNutrition()), getSkin(), "windowStyle"));
		getWindow().getContentTable().row();

		getWindow().getContentTable().add(
			new Label(
				String.format("Pos: %d, %d", getDungeon().getPlayer().getX(), getDungeon().getPlayer().getY()),
				getSkin(),
				"windowStyle"
			)
		);
		getWindow().getContentTable().row();

		getWindow().getContentTable().add(new Label("Turn: ", getSkin(), "windowStyle"));

		final TextField turnField = new TextField("", getSkin());
		getWindow().getContentTable().add(turnField).left().width(100f);

		Button turnSetButton = new TextButton("Set", getSkin());
		turnSetButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				try {
					long turn = Long.parseLong(turnField.getText());

					getDungeon().setTurn(turn);
					getDungeon().turn();

					getWindow().hide();
				} catch (NumberFormatException e) {
					new MessageWindow(getRenderer(), getStage(), getSkin(), "Error", "Invalid turn number.").show();
				}
			}
		});
		getWindow().getContentTable().add(turnSetButton).width(50f);
		getWindow().getContentTable().row();

		Button seeAllButton = new TextButton("See all", getSkin());
		seeAllButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				getLevel().seeAll();
			}
		});
		getWindow().getContentTable().add(seeAllButton).width(50f);

		Button takeSnap = new TextButton("Take Level Snapshot", getSkin());
		takeSnap.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Pixmap snapshot = getRenderer().takeLevelSnapshot();
				String path = Paths.get(System.getProperty("java.io.tmpdir")).resolve("jrogue_level_snap.png").toString();
				PixmapIO.writePNG(Gdx.files.absolute(path), snapshot);
				snapshot.dispose();
				new MessageWindow(getRenderer(), getStage(), getSkin(), "Screenshot", "Saved to [BLUE]" + path + "[]").show();
			}
		});
		getWindow().getContentTable().add(takeSnap).width(125f);
		getWindow().getContentTable().row();
	}
}
