package jr.language.transformers;

import java.util.function.BiFunction;

@FunctionalInterface
public interface Transformer extends BiFunction<String, Object[], String> {
}
