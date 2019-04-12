package jr.debugger.tree.valuemanagers.jrogue;

import jr.debugger.tree.valuemanagers.TypeValueManager;
import jr.debugger.tree.valuemanagers.TypeValueManagerHandler;
import jr.debugger.tree.valuemanagers.settertypes.VectorIntSetter;
import jr.utils.VectorInt;

import java.lang.reflect.Field;

@TypeValueManagerHandler({ VectorInt.class })
public class VectorIntValueManager extends TypeValueManager<VectorInt, VectorIntSetter> {
    private static final VectorIntSetter setter = new VectorIntSetter();
    
    @Override
    public String valueToString(Field field, VectorInt instance) {
        return instance.toString();
    }
    
    @Override
    public boolean canSet(Field field, VectorInt instance) {
        return true;
    }
    
    @Override
    public VectorIntSetter getSetter(Field field, VectorInt instance) {
        return setter;
    }
}
