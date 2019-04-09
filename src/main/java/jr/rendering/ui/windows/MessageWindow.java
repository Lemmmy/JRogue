package jr.rendering.ui.windows;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import jr.rendering.components.hud.HUDComponent;

public class MessageWindow extends WindowBase {
    private String title;
    private String message;
    
    public MessageWindow(HUDComponent hud, Stage stage, Skin skin, String title, String message) {
        super(hud, stage, skin, null, null);
        
        this.title = title;
        this.message = message;
    }
    
    @Override
    public String getTitle() {
        return title;
    }
    
    @Override
    public void populateWindow() {
        Label label = new Label("[WHITE]" + message + "[]", getSkin(), "windowStyleMarkup");
        label.setWrap(true);
        getWindowBorder().getContentTable().add(label).pad(16).prefWidth(350);
        getWindowBorder().button("OK");
        getWindowBorder().key(Input.Keys.ENTER, true);
        getWindowBorder().setModal(false);
        getWindowBorder().pack();
    }
}
