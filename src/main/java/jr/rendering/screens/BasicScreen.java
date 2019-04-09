package jr.rendering.screens;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ScreenAdapter;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class BasicScreen extends ScreenAdapter {
    @Getter private List<InputProcessor> inputProcessors = new ArrayList<>();
    
    protected void clearInputProcessors() {
        inputProcessors.clear();
    }
    
    protected void addInputProcessor(InputProcessor inputProcessor) {
        inputProcessors.add(inputProcessor);
    }
}
