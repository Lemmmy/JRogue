package jr.language;

import jr.language.transformations.Plural;
import jr.language.transformations.Transformer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Accessors(chain = true)
public class Verb extends Word<Verb> {
	private Person person;
	private Tense tense;
	private boolean negative = false;
	
	private Map<VerbStateCombination, Transformer> conjugationRules = new HashMap<>();
	
	{ // default conjugation rules TODO: rest of the rules
		addConjugationRule(Person.THIRD_SINGULAR, Tense.SIMPLE_PRESENT, Plural.auto);
		addConjugationRule(Person.THIRD_SINGULAR, Tense.PRESENT_CONTINUOUS, Plural.auto);
		addConjugationRule(Person.THIRD_PLURAL, Tense.SIMPLE_PRESENT, Plural.auto);
		addConjugationRule(Person.THIRD_PLURAL, Tense.PRESENT_CONTINUOUS, Plural.auto);
	}
	
	public Verb(String infinitive) {
		super(infinitive);
	}
	
	public Verb addConjugationRule(Person person, Tense tense, Transformer transformer) {
		conjugationRules.put(new VerbStateCombination(person, tense), transformer);
		return this;
	}
	
	@Override
	public String build(Object... transformers) {
		return super.build(conjugate(), transformers);
	}
	
	public String conjugate() {
		String s = getWord();
		VerbStateCombination vsc = new VerbStateCombination(person, tense);
		Transformer t = conjugationRules.get(vsc);
		return t != null ? t.apply(s, null) : s;
	}
	
	@Override
	public Class<? extends Word> getCloneClass() {
		return Verb.class;
	}
	
	@Override
	public Verb clone() {
		return super.clone().setPerson(person).setTense(tense);
	}
	
	@Data
	@AllArgsConstructor
	public class VerbStateCombination {
		private Person person;
		private Tense tense;
	}
}
