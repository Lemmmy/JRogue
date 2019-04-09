package jr.rendering.ui.windows;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.rendering.components.hud.HUDComponent;
import jr.rendering.ui.partials.DungeonOverviewPartial;

public class DungeonOverviewWindow extends WindowBase {
    public DungeonOverviewWindow(HUDComponent hud,
                                 Stage stage,
                                 Skin skin,
                                 Dungeon dungeon,
                                 Level level) {
        super(hud, stage, skin, dungeon, level);
    }
    
    @Override
    public String getTitle() {
        return "Dungeon Overview";
    }
    
    @Override
    public void populateWindow() {
        getWindowBorder().setWidth(592);
        getWindowBorder().setHeight(400);
        
        DungeonOverviewPartial dungeonOverview = new DungeonOverviewPartial(getSkin(), getDungeon());
        ScrollPane dungeonOverviewScrollPane = new ScrollPane(dungeonOverview, getSkin());
        getWindowBorder().getContentTable().add(dungeonOverviewScrollPane).top().left().grow().row();
    }
}
