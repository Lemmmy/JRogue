package jr.debugger.tree.valuemanagers.java;

import jr.debugger.tree.valuemanagers.TypeValueManager;
import jr.debugger.tree.valuemanagers.TypeValueManagerHandler;
import jr.debugger.tree.valuemanagers.settertypes.BooleanSetter;

import java.lang.reflect.Field;

@TypeValueManagerHandler({ Boolean.class })
public class BooleanValueManager extends TypeValueManager<Boolean, BooleanSetter> {
    private static final BooleanSetter setter = new BooleanSetter();
    
    @Override
    public String valueToString(Field field, Boolean instance) {
        return instance.toString();
    }
    
    @Override
    public boolean canSet(Field field, Boolean instance) {
        return true;
    }
    
    @Override
    public BooleanSetter getSetter(Field field, Boolean instance) {
        return setter;
    }
}