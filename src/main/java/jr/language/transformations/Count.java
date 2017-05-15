package jr.language.transformations;

public class Count implements TransformerType {
	public static Transformer build(int count) {
		return (s, m) -> String.format("%,d %s", count, s);
	}
}
