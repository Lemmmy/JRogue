package jr.language;

import jr.ErrorHandler;
import jr.language.transformers.Plural;
import jr.language.transformers.Transformer;
import jr.language.transformers.TransformerType;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

@Getter
@Setter
@Accessors(chain = true)
@SuppressWarnings("unchecked")
public class Word<T extends Word> {
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
		if (transformer == null) return (T) this;
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
		return build(word, transformers);
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
	
	public Class<? extends Word> getCloneClass() {
		return Word.class;
	}
	
	@SuppressWarnings("MethodDoesntCallSuperMethod")
	@Override
	public T clone() {
		try {
			Constructor c = getCloneClass().getConstructor(String.class);
			Word newWord = (Word) c.newInstance(getWord());
			newWord.setTransformerMap(new LinkedHashMap<>(transformerMap));
			newWord.setInstanceTransformers(new LinkedHashMap<>(instanceTransformers));
			return (T) newWord;
		} catch (Exception e) {
			ErrorHandler.error("Error cloning word", e);
			return null;
		}
	}
	
	@Override
	public String toString() {
		return build();
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) { return true; }
		if (o == null || getClass() != o.getClass()) { return false; }
		Word<?> word1 = (Word<?>) o;
		return Objects.equals(word, word1.word) &&
			Objects.equals(transformerMap, word1.transformerMap) &&
			Objects.equals(instanceTransformers, word1.instanceTransformers);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(word, transformerMap, instanceTransformers);
	}
}
