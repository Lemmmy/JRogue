package jr.dungeon.language;

import jr.dungeon.language.mutations.Mutation;
import jr.dungeon.language.mutations.MutationType;
import jr.dungeon.language.mutations.Plural;
import lombok.Getter;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicReference;

@Getter
public class Noun {
	/**
	 * The actual noun.
	 */
	private String noun;
	
	private Map<Class<? extends MutationType>, Mutation> mutationMap = new LinkedHashMap<>();
	
	{ // default mutations
		mutationMap.put(Plural.class, Plural.s);
	}
	
	public Noun(String noun) {
		this.noun = noun;
	}
	
	public Noun addMutation(Class<? extends MutationType> mutationType, Mutation mutation) {
		mutationMap.put(mutationType, mutation);
		
		return this;
	}
	
	public String build(Class<? extends MutationType>... mutations) {
		AtomicReference<String> t = new AtomicReference<>(noun);
		
		Arrays.stream(mutations).forEach(mutationType -> {
			if (mutationMap.containsKey(mutationType)) {
				Mutation mutation = mutationMap.get(mutationType);
				t.set(mutation.apply(t.get(), mutationMap));
			}
		});
		
		return t.get();
	}
}
