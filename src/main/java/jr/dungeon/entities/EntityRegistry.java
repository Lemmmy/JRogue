package jr.dungeon.entities;

import jr.JRogue;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class EntityRegistry {
	private static final Map<String, Class<? extends Entity>> registry = new HashMap<>();
	
	public static void findEntityClasses() {
		JRogue.getReflections().getTypesAnnotatedWith(RegisteredEntity.class).forEach(clazz -> {
			if (!Entity.class.isAssignableFrom(clazz)) {
				throw new RuntimeException(String.format("Class `%s` has @RegisteredEntity annotation but does not extend Entity", clazz.getName()));
			}
			
			RegisteredEntity registeredEntity = clazz.getAnnotation(RegisteredEntity.class);
			String id = registeredEntity.id();
			
			if (id.isEmpty()) {
				throw new RuntimeException(String.format("Entity `%s` has an empty ID", clazz.getName()));
			}
			
			if (registry.containsKey(id)) {
				throw new RuntimeException(String.format(
					"Entity `%s` has ID `%s` but entity `%s` already has it",
					clazz.getName(),
					id,
					registry.get(id)
				));
			}
			
			registry.put(id, (Class<? extends Entity>) clazz);
		});
	}
	
	public static Optional<String> getEntityID(Class<? extends Entity> clazz) {
		RegisteredEntity registeredEntity = clazz.getAnnotation(RegisteredEntity.class);
		return registeredEntity != null ? Optional.of(registeredEntity.id()) : Optional.empty();
	}
	
	public static Optional<Class<? extends Entity>> getEntityClass(String id) {
		return registry.containsKey(id) ? Optional.of(registry.get(id)) : Optional.empty();
	}
}
