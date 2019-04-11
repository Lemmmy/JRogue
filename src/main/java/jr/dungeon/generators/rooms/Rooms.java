package jr.dungeon.generators.rooms;

import jr.dungeon.Level;
import jr.dungeon.generators.GeneratorRooms;
import jr.utils.WeightedCollection;
import lombok.AllArgsConstructor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static jr.dungeon.generators.rooms.RoomSpawnStrategy.Always;

public class Rooms {
    private static Map<Class<? extends GeneratorRooms>, Registry> registries = new HashMap<>();
    
    static {
        registerRoom(RoomBasic.class);
        registerRoom(RoomWater.class, 2);
        registerRoom(RoomGraveyard.class, 1);
    }
    
    public static void registerRoom(Class<? extends GeneratorRooms> generatorClass, Class<? extends Room> roomClass, int weight, RoomSpawnStrategy strategy) {
        if (!registries.containsKey(generatorClass)) {
            registries.put(generatorClass, new Registry());
        }
        
        Registry registry = registries.get(generatorClass);
        registry.add(new Registered(roomClass, weight, strategy));
    }
    
    public static void registerRoom(Class<? extends GeneratorRooms> generatorClass, Class<? extends Room> roomClass, int weight) {
        registerRoom(generatorClass, roomClass, weight, new Always());
    }
    
    public static void registerRoom(Class<? extends GeneratorRooms> generatorClass, Class<? extends Room> roomClass) {
        registerRoom(generatorClass, roomClass, -1, new Always());
    }
    
    public static void registerRoom(Class<? extends Room> roomClass, int weight, RoomSpawnStrategy strategy) {
        registerRoom(GeneratorRooms.class, roomClass, weight, strategy);
    }
    
    public static void registerRoom(Class<? extends Room> roomClass, int weight) {
        registerRoom(GeneratorRooms.class, roomClass, weight, new Always());
    }
    
    public static void registerRoom(Class<? extends Room> roomClass) {
        registerRoom(GeneratorRooms.class, roomClass, -1, new Always());
    }
    
    public static Class<? extends Room> getRandomRoom(Level level, GeneratorRooms generator) {
        Registry registry = findRegistryForGenerator(generator);
        return registry.getWeightedRooms(level, generator).next();
    }
    
    private static Registry findRegistryForGenerator(GeneratorRooms generator) {
        Registry registry = null;
        Class<? extends GeneratorRooms> clazz = generator.getClass();
        while (clazz != null) {
            if (!GeneratorRooms.class.isAssignableFrom(clazz)) break;
            if (registries.containsKey(clazz)) {
                registry = registries.get(clazz);
                break;
            }
            
            clazz = (Class<? extends GeneratorRooms>) clazz.getSuperclass();
        }
        
        if (registry == null) {
            throw new RuntimeException("No room registry found for " + generator.getClass().getName());
        }
        
        return registry;
    }
    
    public static final class Registry {
        private final Set<Registered> rooms = new HashSet<>();
        
        private void add(Registered room) {
            rooms.add(room);
        }
        
        private WeightedCollection<Class<? extends Room>> getWeightedRooms(Level level, GeneratorRooms generator) {
            WeightedCollection<Class<? extends Room>> collection = new WeightedCollection();
            
            rooms.stream()
                .filter(r -> r.strategy.shouldSpawn(level, generator))
                .forEach(r -> {
                    int weight = r.weight != -1 ? r.weight : generator.getDefaultRoomWeight();
                    collection.add(weight, r.roomClass);
                });
            
            return collection;
        }
    }
    
    @AllArgsConstructor
    public static final class Registered {
        private final Class<? extends Room> roomClass;
        private final int weight;
        private final RoomSpawnStrategy strategy;
    }
}
