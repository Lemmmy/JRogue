package jr.debugger.tree.valuemanagers.jrogue;

import jr.debugger.tree.valuemanagers.TypeValueManager;
import jr.debugger.tree.valuemanagers.TypeValueManagerHandler;
import jr.debugger.tree.valuemanagers.settertypes.PointSetter;
import jr.utils.Point;

import java.lang.reflect.Field;

@TypeValueManagerHandler({ Point.class })
public class PointValueManager extends TypeValueManager<Point, PointSetter> {
    private static final PointSetter setter = new PointSetter();
    
    @Override
    public String valueToString(Field field, Point instance) {
        return instance.toString();
    }
    
    @Override
    public boolean canSet(Field field, Point instance) {
        return true;
    }
    
    @Override
    public PointSetter getSetter(Field field, Point instance) {
        return setter;
    }
}
