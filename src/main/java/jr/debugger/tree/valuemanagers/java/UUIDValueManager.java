package jr.debugger.tree.valuemanagers.java;

import jr.debugger.tree.valuemanagers.TypeValueManager;
import jr.debugger.tree.valuemanagers.TypeValueManagerHandler;
import jr.debugger.tree.valuemanagers.settertypes.UUIDSetter;

import java.lang.reflect.Field;
import java.util.UUID;

@TypeValueManagerHandler({ UUID.class })
public class UUIDValueManager extends TypeValueManager<UUID, UUIDSetter> {
    private static final UUIDSetter setter = new UUIDSetter();
    
    @Override
    public String valueToString(Field field, UUID instance) {
        return instance.toString();
    }
    
    @Override
    public boolean canSet(Field field, UUID instance) {
        return true;
    }
    
    @Override
    public UUIDSetter getSetter(Field field, UUID instance) {
        return setter;
    }
}