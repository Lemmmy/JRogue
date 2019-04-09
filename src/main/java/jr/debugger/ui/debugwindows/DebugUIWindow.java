package jr.debugger.ui.debugwindows;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import jr.ErrorHandler;
import jr.rendering.ui.windows.Window;
import lombok.Getter;
import org.apache.commons.lang3.reflect.ConstructorUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.stream.Stream;

@Getter
public class DebugUIWindow<WindowT extends Window> {
    private String windowName;
    private Class<WindowT> windowClass;
    private Object[] constructorArgs;
    
    public DebugUIWindow(String windowName, Class<WindowT> windowClass, Object... constructorArgs) {
        this.windowName = windowName;
        this.windowClass = windowClass;
        this.constructorArgs = constructorArgs;
    }
    
    public WindowT show(Stage stage, Skin skin) {
        Object[] ctorArgs = Stream.concat(
            Arrays.stream(new Object[] { stage, skin }),
            Arrays.stream(constructorArgs)
        ).toArray();
        
        Class<?>[] ctorTypes = Arrays.stream(ctorArgs)
            .map(Object::getClass)
            .toArray(Class<?>[]::new);
        
        try {
            Constructor<WindowT> constructor = ConstructorUtils.getMatchingAccessibleConstructor(windowClass, ctorTypes);
            WindowT window = constructor.newInstance(ctorArgs);
            window.show();
            return window;
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            ErrorHandler.error("Error instantiating debug window", e);
        }
        
        return null;
    }
}