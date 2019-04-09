package jr.debugger.tree;

import org.apache.commons.text.WordUtils;

public enum AccessLevel {
    UNKNOWN, PACKAGE_PRIVATE, PRIVATE, PROTECTED, PUBLIC;
    
    public String humanName() {
        return WordUtils.capitalize(name().toLowerCase().replaceAll("_", " "));
    }
}
