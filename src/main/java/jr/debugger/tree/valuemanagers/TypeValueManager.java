package jr.debugger.tree.valuemanagers;

import jr.debugger.tree.valuemanagers.settertypes.TypeValueSetter;

import java.lang.reflect.Field;

public abstract class TypeValueManager<T, SetterT extends TypeValueSetter<T, ?>> {
    public abstract String valueToString(Field field, T instance);
    
    public boolean canSet(Field field, T instance) {
        return false;
    }
    
    public SetterT getSetter(Field field, T instance) {
        return null;
    }
}