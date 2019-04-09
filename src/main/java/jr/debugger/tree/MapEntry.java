package jr.debugger.tree;

import jr.debugger.utils.Debuggable;
import lombok.Getter;

import java.util.Map;

public class MapEntry implements Debuggable {
    @Getter private Object key, value;
    
    public MapEntry(Object key, Object value) {
        this.key = key;
        this.value = value;
    }
    
    public MapEntry(Map.Entry entry) {
        this.key = entry.getKey();
        this.value = entry.getValue();
    }
    
    @Override
    public String getTypeOverride() {
        return "Entry";
    }
    
    @Override
    public boolean shouldShowIdenticon() {
        return false;
    }
}
