package jr.language;

import jr.language.transformations.Plural;

public class Lexicon {
	public static final Noun arrow = new Noun("arrow");
	public static final Noun chest = new Noun("chest");
	public static final Noun fountain = new Noun("fountain");
	public static final Noun goblin = new Noun("goblin");
	public static final Noun gravestone = new Noun("gravestone");
	public static final Noun holyAltar = new Noun("holy altar");
	public static final Noun lightOrb = new Noun("light orb");
	public static final Noun rat = new Noun("rat");
	public static final Noun spider = new Noun("spider");
	public static final Noun strike = new Noun("strike");
	public static final Noun weaponRack = new Noun("weapon rack");
	public static final Noun apple = new Noun("apple");
	public static final Noun banana = new Noun("banana");
	public static final Noun bread = new Noun("loaf of bread").addTransformer(Plural.class, (s, m) -> s.replace("loaf", "loaves"));
	public static final Noun carrot = new Noun("carrot");
	public static final Noun cherries = new Noun("pair of cherries").addTransformer(Plural.class, (s, m) -> s.replace("pair", "pairs"));
	public static final Noun corn = new Noun("ear of corn").addTransformer(Plural.class, (s, m) -> s.replace("ear", "ears"));
	public static final Noun corpse = new Noun("corpse");
	public static final Noun lemon = new Noun("lemon");
	public static final Noun orange = new Noun("orange");
	public static final Noun you = new Noun("you");
}
