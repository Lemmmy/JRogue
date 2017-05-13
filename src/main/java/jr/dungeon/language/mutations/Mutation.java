package jr.dungeon.language.mutations;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

@FunctionalInterface
public interface Mutation extends BiFunction<String, Map<Class<? extends MutationType>, Mutation>, String> {
}
