package jr.dungeon.generators.rooms;

import jr.dungeon.Level;
import jr.dungeon.generators.GeneratorRooms;

import java.util.Arrays;
import java.util.List;

/**
 * Determines whether or not a {@link Room} class should have a chance to be spawned in a given level/generator.
 */
@FunctionalInterface
public interface RoomSpawnStrategy {
    /**
     * Determines whether or not a {@link Room} class should have a chance to be spawned in a given level/generator.
     */
    boolean shouldSpawn(Level level, GeneratorRooms generator);
    
    /**
     * This {@link Room} should <b>only</b> spawn in the given set of {@link GeneratorRooms generator classes}.
     */
    class Whitelist implements RoomSpawnStrategy {
        private List<Class<? extends GeneratorRooms>> classes;
    
        @SafeVarargs
        public Whitelist(Class<? extends GeneratorRooms>... classes) {
            this.classes = Arrays.asList(classes);
        }
    
        @Override
        public boolean shouldSpawn(Level level, GeneratorRooms generator) {
            return classes.stream().anyMatch(c -> c.isInstance(generator));
        }
    }
    
    /**
     * This {@link Room} should <b>never</b> spawn in the given set of {@link GeneratorRooms generator classes}.
     */
    class Blacklist implements RoomSpawnStrategy {
        private List<Class<? extends GeneratorRooms>> classes;
        
        @SafeVarargs
        public Blacklist(Class<? extends GeneratorRooms>... classes) {
            this.classes = Arrays.asList(classes);
        }
        
        @Override
        public boolean shouldSpawn(Level level, GeneratorRooms generator) {
            return classes.stream().noneMatch(c -> c.isInstance(generator));
        }
    }
    
    /**
     * This {@link Room} should always have a chance to spawn.
     */
    class Always implements RoomSpawnStrategy {
        @Override
        public boolean shouldSpawn(Level level, GeneratorRooms generator) {
            return true;
        }
    }
}
