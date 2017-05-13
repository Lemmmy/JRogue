package jr;

import jr.dungeon.language.Noun;
import jr.dungeon.language.transformations.Article;
import jr.dungeon.language.transformations.Capitalize;
import jr.dungeon.language.transformations.Plural;
import jr.dungeon.language.transformations.Possessive;

public class NLGTest {
	public static void main(String[] args) {
		// noun defined in the lexicon
		Noun noun = new Noun("city")
			.addTransformer(Plural.class, Plural.ies);
		
		// noun in e.g. getName()
		Noun myNoun = noun.clone();
		//	.addInstanceTransformer(Possessive.class, Possessive.your);
		
		// somewhere that builds and logs the noun
		Plural.addCount(myNoun, 5);
		Article.addTheIfPossible(myNoun, false); // prefixes 'the' if it's not possessive or plural
		System.out.println(myNoun.build(Capitalize.first));
	}
}
