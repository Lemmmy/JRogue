package jr.debugger.tree.valuemanagers.settertypes;

import jr.debugger.tree.valuemanagers.ValueSetError;

import java.lang.reflect.Field;

public class StringSetter extends TypeValueSetter<String, String> {
    @Override
    public void set(Field field, Object instance, String value) {
        try {
            field.set(instance, value);
        } catch (IllegalAccessException | IllegalArgumentException e) {
            throw new ValueSetError("Error setting string value", e);
        }
    }
}