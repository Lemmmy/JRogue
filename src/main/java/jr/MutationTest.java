package jr;

import jr.dungeon.language.Noun;
import jr.dungeon.language.mutations.Plural;

public class MutationTest {
	public static void main(String[] args) {
		Noun noun = new Noun("city").addMutation(Plural.class, Plural.ies);
		
		System.out.println(noun.build(Plural.class));
	}
}
