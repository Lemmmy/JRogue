package jr.rendering.ui.windows;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public abstract class TooltipWindow extends Window {
    public TooltipWindow(Stage stage, Skin skin) {
        super(stage, skin);
    }
    
    @Override
    protected void initialiseWindow() {
        windowBorder = new TooltipWindowBorder(getTitle(), getSkin(), this);
        
        windowBorder.addResultListener(this);
        
        windowBorder.setModal(true);
        windowBorder.pad(28, 10, 10, 10);
        
        windowBorder.key(Input.Keys.ESCAPE, false);
        
        populateWindow();
        
        windowBorder.setPosition(Gdx.input.getX(), Gdx.input.getY());
        
        getStage().addActor(windowBorder);
    }
}
