package jr.dungeon.serialisation;

import jr.JRogue;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DungeonRegistries {
	private static final Map<Class, DungeonRegistry> registries = new HashMap<>();
	
	public static void findRegistries() {
		JRogue.getReflections().getTypesAnnotatedWith(HasRegistry.class).forEach(clazz -> {
			JRogue.getLogger().debug("Making registry for {}", clazz);
			
			DungeonRegistry registry = new DungeonRegistry<>(clazz);
			registries.put(clazz, registry);
			
			registry.scanClasses();
		});
	}
	
	public static <T> Optional<DungeonRegistry<T>> findRegistryForClass(Class<? extends T> clazz) {
		Class c = clazz;
		while (c != null) {
			if (registries.containsKey(c)) return Optional.of(registries.get(c));
			c = c.getSuperclass();
		}
		return Optional.empty();
	}
}
