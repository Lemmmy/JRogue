package jr.debugger.tree.valuemanagers;

public class ValueSetError extends RuntimeException {
	public ValueSetError(String message) {
		super(message);
	}
	
	public ValueSetError(String message, Throwable cause) {
		super(message, cause);
	}
}
