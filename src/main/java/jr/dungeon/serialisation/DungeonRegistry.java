package jr.dungeon.serialisation;

import jr.JRogue;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DungeonRegistry<T> {
	private final Map<String, Class<? extends T>> entries = new HashMap<>();
	private Class<T> targetClass;
	
	public DungeonRegistry(Class<T> targetClass) {
		this.targetClass = targetClass;
	}
	
	public void scanClasses() {
		JRogue.getReflections().getTypesAnnotatedWith(Registered.class).forEach(clazz -> {
			if (!targetClass.isAssignableFrom(clazz)) {
				throw new RuntimeException(String.format("Class `%s` has @Registered annotation but does not extend %s", clazz.getName(), targetClass.getSimpleName()));
			}
			
			Registered registered = clazz.getAnnotation(Registered.class);
			String id = registered.id();
			
			if (id.isEmpty()) {
				throw new RuntimeException(String.format("%s `%s` has an empty ID", targetClass.getName(), clazz.getName()));
			}
			
			if (entries.containsKey(id)) {
				throw new RuntimeException(String.format(
					"%s `%s` has ID `%s` but is already taken by `%s`",
					targetClass.getSimpleName(),
					clazz.getName(),
					id,
					entries.get(id)
				));
			}
			
			entries.put(id, (Class<? extends T>) clazz);
		});
	}
	
	public Optional<String> getID(Class<? extends T> clazz) {
		Registered registered = clazz.getAnnotation(Registered.class);
		return registered != null ? Optional.of(registered.id()) : Optional.empty();
	}
	
	public Optional<Class<? extends T>> getClassFromID(String id) {
		return entries.containsKey(id) ? Optional.of(entries.get(id)) : Optional.empty();
	}
}
