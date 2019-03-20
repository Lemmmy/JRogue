package jr.debugger.utils;

public interface Debuggable {
	default String getValueHint() {
		return null;
	}
	
	default String getTypeOverride() {
		return null;
	}
	
	default boolean shouldShowIdenticon() {
		return true;
	}
}
