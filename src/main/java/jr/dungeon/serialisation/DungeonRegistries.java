package jr.dungeon.serialisation;

import jr.JRogue;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DungeonRegistries {
    private static final Map<Class, DungeonRegistry> registries = new HashMap<>();
    @Getter private static final Map<Class, DungeonRegistryTypeAdapterFactory> typeAdapterFactories = new HashMap<>();
    
    public static void findRegistries() {
        JRogue.getReflections().getTypesAnnotatedWith(HasRegistry.class).stream()
            .filter(clazz -> clazz.getAnnotation(HasRegistry.class) != null)
            .forEach(clazz -> {
                JRogue.getLogger().debug("Making registry for {}", clazz);
                
                DungeonRegistry registry = new DungeonRegistry<>(clazz);
                registries.put(clazz, registry);
                
                registry.scanClasses();
                
                typeAdapterFactories.put(clazz, new DungeonRegistryTypeAdapterFactory<>(clazz));
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
