package jr;

import jr.language.Noun;
import jr.language.transformations.Article;
import jr.language.transformations.Capitalise;
import jr.language.transformations.Plural;

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
		System.out.println(myNoun.build(Capitalise.first));
	}
}
