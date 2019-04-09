package jr.dungeon.generators;

import jr.dungeon.entities.monsters.MonsterSpawn;
import jr.dungeon.entities.monsters.canines.MonsterFox;
import jr.dungeon.entities.monsters.canines.MonsterJackal;
import jr.dungeon.entities.monsters.critters.MonsterLizard;
import jr.dungeon.entities.monsters.critters.MonsterRat;
import jr.dungeon.entities.monsters.critters.MonsterSpider;
import jr.dungeon.entities.monsters.humanoids.MonsterGoblin;
import jr.dungeon.entities.monsters.humanoids.MonsterSkeleton;
import jr.dungeon.entities.monsters.mold.MonsterMoldBlue;
import jr.dungeon.entities.monsters.mold.MonsterMoldGreen;
import jr.dungeon.entities.monsters.mold.MonsterMoldRed;
import jr.dungeon.entities.monsters.mold.MonsterMoldYellow;
import org.apache.commons.lang3.Range;

import java.util.Arrays;
import java.util.List;

public enum MonsterSpawningStrategy {
    // TODO: these would be better moved to their own classes, I think
    STANDARD(
        new MonsterSpawn(
            Range.between(1, 10),
            Range.between(0, 1),
            MonsterMoldRed.class
        ),
        
        new MonsterSpawn(
            Range.between(1, 10),
            Range.between(0, 1),
            MonsterMoldYellow.class
        ),
        
        new MonsterSpawn(
            Range.between(1, 2),
            Range.between(0, 1),
            MonsterGoblin.class
        ),
        
        new MonsterSpawn(
            Range.between(4, 8),
            Range.between(0, 3),
            MonsterGoblin.class
        ),
        
        new MonsterSpawn(
            Range.between(1, 2),
            Range.between(1, 3),
            Range.between(1, 2),
            MonsterJackal.class
        ),
        
        new MonsterSpawn(
            Range.between(3, 10),
            Range.between(2, 5),
            Range.between(1, 3),
            MonsterJackal.class
        ),
        
        new MonsterSpawn(
            Range.between(1, 3),
            Range.between(4, 8),
            MonsterSpider.class
        ),
        
        new MonsterSpawn(
            Range.between(1, 4),
            Range.between(2, 6),
            MonsterRat.class
        ),
        
        new MonsterSpawn(
            Range.between(3, Integer.MAX_VALUE),
            Range.between(0, 4),
            MonsterSkeleton.class
        ),
        
        new MonsterSpawn(
            Range.between(3, 6),
            Range.between(0, 2),
            Range.between(2, 5),
            MonsterFox.class
        ),
        
        new MonsterSpawn(
            Range.between(4, Integer.MAX_VALUE),
            Range.between(0, 8),
            MonsterLizard.class
        )),
    
    ICE(
        new MonsterSpawn(
            Range.between(8, Integer.MAX_VALUE),
            Range.between(0, 1),
            MonsterMoldGreen.class
        ),
        
        new MonsterSpawn(
            Range.between(11, Integer.MAX_VALUE),
            Range.between(0, 3),
            MonsterMoldBlue.class
        ),
        
        new MonsterSpawn(
            Range.between(3, Integer.MAX_VALUE),
            Range.between(0, 3),
            MonsterSkeleton.class
        ),
        
        new MonsterSpawn(
            Range.between(3, 6),
            Range.between(0, 4),
            Range.between(2, 5),
            MonsterFox.class
        )
    ),
    
    SEWER(
        new MonsterSpawn(
            Range.between(1, Integer.MAX_VALUE),
            Range.between(4, 15),
            MonsterSpider.class
        ),
        
        new MonsterSpawn(
            Range.between(1, Integer.MAX_VALUE),
            Range.between(4, 7),
            Range.between(1, 5),
            MonsterRat.class
        ),
        
        new MonsterSpawn(
            Range.between(1, Integer.MAX_VALUE),
            Range.between(0, 4),
            MonsterGoblin.class
        )
    ),
    
    CAVE(
        new MonsterSpawn(
            Range.between(1, Integer.MAX_VALUE),
            Range.between(5, 30),
            MonsterSpider.class
        )
    ),

    NONE();
    
    private List<MonsterSpawn> spawns;
    
    MonsterSpawningStrategy(MonsterSpawn... spawns) {
        this.spawns = Arrays.asList(spawns);
    }
    
    public List<MonsterSpawn> getSpawns() {
        return spawns;
    }
}
