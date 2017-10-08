package jr.language;

public class Lexicon {
	public static final Noun alexandrite = new Noun("alexandrite");
	public static final Noun amethyst = new Noun("amethyst");
	public static final Noun apatite = new Noun("apatite");
	public static final Noun apple = new Noun("apple");
	public static final Noun aquamarine = new Noun("aquamarine");
	public static final Noun arrow = new Noun("arrow");
	public static final Noun banana = new Noun("banana");
	public static final Noun blackDiamond = new Noun("black diamond");
	public static final Noun blackOnyx = new Noun("black onyx");
	public static final Noun blueApatite = new Noun("blue apatite");
	public static final Noun blueDiamond = new Noun("blue diamond");
	public static final Noun blueMold = new Noun("blue mold");
	public static final Noun book = new Noun("book");
	public static final Noun bow = new Noun("bow");
	public static final Noun bread = new Noun("loaf of bread");
	public static final Noun candlestick = new Noun("candlestick");
	public static final Noun carrot = new Noun("carrot");
	public static final Noun cat = new Noun("cat");
	public static final Noun cherries = new Noun("pair of cherries");
	public static final Noun chest = new Noun("chest");
	public static final Noun citrine = new Noun("citrine");
	public static final Noun corn = new Noun("ear of corn");
	public static final Noun corpse = new Noun("corpse");
	public static final Noun dagger = new Noun("dagger");
	public static final Noun diamond = new Noun("diamond");
	public static final Noun diopside = new Noun("diopside");
	public static final Noun emerald = new Noun("emerald");
	public static final Noun fish = new Noun("fish");
	public static final Noun fountain = new Noun("fountain");
	public static final Noun fox = new Noun("fox");
	public static final Noun gem = new Noun("gem");
	public static final Noun glassBottle = new Noun("glass bottle");
	public static final Noun goblin = new Noun("goblin");
	public static final Noun goblinZombie = new Noun("goblin zombie");
	public static final Noun gold = new Noun("gold").setUncountable(true);
	public static final Noun goldenSapphire = new Noun("golden sapphire");
	public static final Noun gravestone = new Noun("gravestone");
	public static final Noun greenAmethyst = new Noun("green amethyst");
	public static final Noun greenGarnet = new Noun("green garnet");
	public static final Noun greenMold = new Noun("green mold");
	public static final Noun hellhound = new Noun("hellhound");
	public static final Noun hematite = new Noun("hematite");
	public static final Noun holyAltar = new Noun("holy altar");
	public static final Noun hound = new Noun("hound");
	public static final Noun icehound = new Noun("icehound");
	public static final Noun jackal = new Noun("jackal");
	public static final Noun jade = new Noun("jade");
	public static final Noun lapisLazuli = new Noun("lapis lazuli");
	public static final Noun lemon = new Noun("lemon");
	public static final Noun lightOrb = new Noun("light orb");
	public static final Noun lizard = new Noun("lizard");
	public static final Noun longsword = new Noun("longsword");
	public static final Noun orange = new Noun("orange");
	public static final Noun peridot = new Noun("peridot");
	public static final Noun potion = new Noun("potion");
	public static final Noun pufferfish = new Noun("pufferfish");
	public static final Noun quartz = new Noun("quartz");
	public static final Noun rat = new Noun("rat");
	public static final Noun redGarnet = new Noun("red garnet");
	public static final Noun redMold = new Noun("red mold");
	public static final Noun ruby = new Noun("ruby");
	public static final Noun sapphire = new Noun("sapphire");
	public static final Noun shortsword = new Noun("shortsword");
	public static final Noun skeleton = new Noun("skeleton");
	public static final Noun skyBlueTopaz = new Noun("sky blue topaz");
	public static final Noun spellbook = new Noun("spellbook");
	public static final Noun spider = new Noun("spider");
	public static final Noun spinel = new Noun("spinel");
	public static final Noun staff = new Noun("staff");
	public static final Noun strike = new Noun("strike");
	public static final Noun tanzanite = new Noun("tanzanite");
	public static final Noun thermometer = new Noun("thermometer");
	public static final Noun topaz = new Noun("topaz");
	public static final Noun torch = new Noun("torch");
	public static final Noun weaponRack = new Noun("weapon rack");
	public static final Noun whiteSapphire = new Noun("white sapphire");
	public static final Noun whiteTopaz = new Noun("white topaz");
	public static final Noun worthlessGem = new Noun("worthless piece of glass");
	public static final Noun yellowDiamond = new Noun("yellow diamond");
	public static final Noun yellowMold = new Noun("yellow mold");
	public static final Noun yellowTopaz = new Noun("yellow topaz");
	
	public static final Pronoun it = new Pronoun("it");
	public static final Pronoun you = new Pronoun("you");
	
	public static final Verb attack = new Verb("attack");
	public static final Verb bash = new Verb("bash");
	public static final Verb be = new Verb("be")
		.addConjugationRule(Person.THIRD_SINGULAR, Tense.SIMPLE_PRESENT, (s, m) -> "is")
		.addConjugationRule(Person.THIRD_SINGULAR, Tense.PRESENT_PARTICIPLE, (s, m) -> "is")
		.addConjugationRule(Person.THIRD_PLURAL, Tense.SIMPLE_PRESENT, (s, m) -> "are")
		.addConjugationRule(Person.THIRD_PLURAL, Tense.PRESENT_PARTICIPLE, (s, m) -> "are");
	public static final Verb bite = new Verb("bite");
	public static final Verb drop = new Verb("drop");
	public static final Verb headbutt = new Verb("headbutt");
	public static final Verb hit = new Verb("hit");
	public static final Verb kick = new Verb("kick");
	public static final Verb kill = new Verb("kill");
	public static final Verb miss = new Verb("miss");
	public static final Verb move = new Verb("move");
	public static final Verb punch = new Verb("punch");
	public static final Verb step = new Verb("step");
	public static final Verb swipe = new Verb("swipe");
}
