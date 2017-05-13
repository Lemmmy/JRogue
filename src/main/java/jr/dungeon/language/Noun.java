package jr.dungeon.language;

import jr.dungeon.language.transformations.Transformer;
import jr.dungeon.language.transformations.TransformerType;
import jr.dungeon.language.transformations.Plural;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

@Getter
@Setter
public class Noun {
	/**
	 * The actual noun.
	 */
	private String noun;
	
	private Map<Class<? extends TransformerType>, Transformer> transformerMap = new LinkedHashMap<>();
	private List<Object> instanceTransformers = new ArrayList<>();
	
	{ // default transformers
		transformerMap.put(Plural.class, Plural.s);
	}
	
	public Noun(String noun) {
		this.noun = noun;
	}
	
	public Noun addTransformer(Class<? extends TransformerType> transformerType, Transformer transformer) {
		transformerMap.put(transformerType, transformer);
		
		return this;
	}
	
	public Noun addInstanceTransformer(Object transformer) {
		instanceTransformers.add(transformer);
		
		return this;
	}
	
	public Noun clearInstanceTransformers() {
		instanceTransformers.clear();
		
		return this;
	}
	
	public String build(Object... transformers) {
		AtomicReference<String> t = new AtomicReference<>(noun);
		
		Stream.concat(
			instanceTransformers.stream(),
			Arrays.stream(transformers)
		).forEach(m -> {
			if (m instanceof Class && TransformerType.class.isAssignableFrom((Class) m)) {
				if (transformerMap.containsKey(m)) {
					Transformer transformer = transformerMap.get(m);
					t.set(transformer.apply(t.get(), transformers));
				}
			} else if (m instanceof Transformer) {
				t.set(((Transformer) m).apply(t.get(), transformers));
			}
		});
		
		return t.get();
	}
	
	public Noun clone() {
		Noun newNoun = new Noun(noun);
		newNoun.setTransformerMap(new LinkedHashMap<>(transformerMap));
		newNoun.setInstanceTransformers(new ArrayList<>(instanceTransformers));
		return newNoun;
	}
}
