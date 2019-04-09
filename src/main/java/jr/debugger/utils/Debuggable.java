package jr.debugger.utils;

public interface Debuggable {
    default String getValueString() {
        return null;
    }
    
    default String getTypeOverride() {
        return null;
    }
    
    default boolean shouldShowIdenticon() {
        return true;
    }
}
