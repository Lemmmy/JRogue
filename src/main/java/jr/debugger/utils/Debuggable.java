package jr.debugger.utils;

public interface Debuggable {
	default String getNameHint() {
		return null;
	}
}
