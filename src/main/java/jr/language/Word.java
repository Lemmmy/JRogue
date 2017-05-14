package jr.language;

import jr.language.transformations.Transformer;
import jr.language.transformations.TransformerType;
import jr.language.transformations.Plural;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

@Getter
@Setter
@Accessors(chain = true)
public class Word<T> {
	/**
	 * The actual word.
	 */
	private final String word;
	
	private Map<Class<? extends TransformerType>, Transformer> transformerMap = new LinkedHashMap<>();
	private Map<Class<? extends TransformerType>, Object> instanceTransformers = new LinkedHashMap<>();
	
	{ // default transformers
		transformerMap.put(Plural.class, Plural.auto);
	}
	
	public Word(String word) {
		this.word = word;
	}
	
	public T addTransformer(Class<? extends TransformerType> transformerType, Transformer transformer) {
		transformerMap.put(transformerType, transformer);
		return (T) this;
	}
	
	public T addInstanceTransformer(Class<? extends TransformerType> transformerType) {
		instanceTransformers.put(transformerType, transformerType);
		return (T) this;
	}
	
	public T addInstanceTransformer(Class<? extends TransformerType> transformerType, Transformer transformer) {
		if (transformer == null) return (T) this;
		instanceTransformers.put(transformerType, transformer);
		return (T) this;
	}
	
	public boolean hasInstanceTransformer(Class<? extends TransformerType> transformerType) {
		return instanceTransformers.containsKey(transformerType);
	}
	
	public T removeInstanceTransformer(Class<? extends TransformerType> transformerType) {
		instanceTransformers.remove(transformerType);
		return (T) this;
	}
	
	public T clearInstanceTransformers() {
		instanceTransformers.clear();
		return (T) this;
	}
	
	public String build(Object... transformers) {
		return build(word);
	}
	
	public String build(String s, Object... transformers) {
		AtomicReference<String> t = new AtomicReference<>(s);
		
		Stream.concat(
			instanceTransformers.values().stream(),
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
	
	public T clone() {
		Word newWord = new Word(word);
		newWord.setTransformerMap(new LinkedHashMap<>(transformerMap));
		newWord.setInstanceTransformers(new LinkedHashMap<>(instanceTransformers));
		return (T) newWord;
	}
}
