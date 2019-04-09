package jr.dungeon.generators.rooms.features;

import jr.dungeon.entities.QuickSpawn;
import jr.dungeon.entities.decoration.EntityFountain;
import jr.dungeon.generators.rooms.Room;
import jr.dungeon.serialisation.Registered;
import jr.language.Lexicon;
import jr.language.Noun;

@Registered(id="specialRoomFeatureFountain")
public class FeatureFountain extends SpecialRoomFeature {
    @Override
    public void generate(Room room) {
        QuickSpawn.spawnClass(EntityFountain.class, room.level, room.randomPoint());
    }
    
    @Override
    public Noun getName() {
        return Lexicon.fountain.clone();
    }
}
