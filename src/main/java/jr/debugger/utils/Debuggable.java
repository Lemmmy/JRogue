package jr.debugger.utils;

public interface Debuggable {
	default String getValueHint() {
		return null;
	}
}
