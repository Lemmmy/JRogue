package jr.debugger.tree.valuemanagers.settertypes;

import jr.debugger.tree.valuemanagers.ValueSetError;

import java.lang.reflect.Field;

public class BooleanSetter extends TypeValueSetter<Boolean, Boolean> {
    @Override
    public void set(Field field, Object instance, Boolean value) {
        try {
            field.setBoolean(instance, value);
        } catch (IllegalAccessException | IllegalArgumentException e) {
            throw new ValueSetError("Error setting boolean value", e);
        }
    }
}