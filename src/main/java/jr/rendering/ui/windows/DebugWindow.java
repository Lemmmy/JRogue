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
import jr.rendering.GameAdapter;
import jr.rendering.components.hud.HUDComponent;

public class DebugWindow extends WindowBase {
    public DebugWindow(HUDComponent hud, Stage stage, Skin skin, Dungeon dungeon, Level level) {
        super(hud, stage, skin, dungeon, level);
    }
    
    @Override
    public String getTitle() {
        return "Debug Tools";
    }
    
    @Override
    public void populateWindow() {
        getWindowBorder().setWidth(350f);
        getWindowBorder().setHeight(250f);
        
        getWindowBorder().getContentTable()
            .add(new Label(
                String.format("Nutrition: %,d", getDungeon().getPlayer().getNutrition()),
                getSkin(),
                "windowStyle"
            ));
        getWindowBorder().getContentTable().row();
        
        getWindowBorder().getContentTable().add(
            new Label(
                String.format("Pos: %s", getDungeon().getPlayer().getPosition()),
                getSkin(),
                "windowStyle"
            )
        );
        getWindowBorder().getContentTable().row();
    
        GameAdapter game = getHud().renderer.getGame();
        Button debugClientButton = new TextButton("Open debug client", getSkin());
        debugClientButton.setDisabled(game.getDebugClient() != null);
        debugClientButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (game.getDebugClient() != null) return;
                
                game.openDebugClient();
                debugClientButton.setDisabled(true);
            }
        });
        getWindowBorder().getContentTable().add(debugClientButton);
    }
}
