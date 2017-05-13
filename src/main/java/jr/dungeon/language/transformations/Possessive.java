package jr.dungeon.language.transformations;

public class Possessive implements TransformerType {
	public static final Transformer your = (s, m) -> "your " + s;
	
	public static Transformer build(String owner) {
		return (s, m) -> owner + "'s " + s;
	}
}
