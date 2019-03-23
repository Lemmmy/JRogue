package jr.dungeon.generators.rooms.features;

import com.github.alexeyr.pcg.Pcg32;
import jr.dungeon.entities.QuickSpawn;
import jr.dungeon.entities.decoration.EntityFountain;
import jr.dungeon.generators.rooms.Room;
import jr.dungeon.serialisation.Registered;
import jr.language.Lexicon;
import jr.language.Noun;

@Registered(id="specialRoomFeatureFountain")
public class FeatureFountain extends SpecialRoomFeature {
	private Pcg32 rand = new Pcg32();
	
	@Override
	public void generate(Room room) {
		int fountainX = rand.nextInt(room.getWidth() - 2) + room.getX() + 1;
		int fountainY = rand.nextInt(room.getHeight() - 2) + room.getY() + 1;
		
		QuickSpawn.spawnClass(EntityFountain.class, room.getLevel(), fountainX, fountainY);
	}
	
	@Override
	public Noun getName() {
		return Lexicon.fountain.clone();
	}
}
