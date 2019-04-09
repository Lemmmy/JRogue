package jr.dungeon.serialisation;

import com.google.gson.InstanceCreator;
import jr.dungeon.Dungeon;

import java.lang.reflect.Type;

public class DungeonInstanceCreator implements InstanceCreator<Dungeon> {
    @Override
    public Dungeon createInstance(Type type) {
        Dungeon dungeon = new Dungeon();
        DungeonSerialiser.currentDeserialisingDungeon = dungeon;
        return dungeon;
    }
}
