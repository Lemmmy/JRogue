package jr.rendering.gdxvox.models.magicavoxel.parser;

public class VoxParseException extends Exception {
	public VoxParseException(String s) {
		super(s);
	}
	
	public VoxParseException(AssertionError e) {
		super(e);
	}
}
