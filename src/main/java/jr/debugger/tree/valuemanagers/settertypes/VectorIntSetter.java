package jr.debugger.tree.valuemanagers.settertypes;

import jr.debugger.tree.valuemanagers.ValueSetError;
import jr.utils.VectorInt;

import java.lang.reflect.Field;

public class VectorIntSetter extends TypeValueSetter<VectorInt, Integer[]> {
    @Override
    public void set(Field field, Object instance, Integer[] value) {
        try {
            if (value.length != 2) throw new ValueSetError("VectorInt must provide x and y components");
            field.set(instance, VectorInt.get(value[0], value[1]));
        } catch (IllegalAccessException | IllegalArgumentException e) {
            throw new ValueSetError("Error setting point value", e);
        }
    }
}
