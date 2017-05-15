package jr.language.transformations;

import java.util.function.BiFunction;

@FunctionalInterface
public interface Transformer extends BiFunction<String, Object[], String> {
}
