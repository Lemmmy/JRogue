package pw.lemmmy.jrogue.dungeon.generators.rooms.features;

import com.github.alexeyr.pcg.Pcg32;
import pw.lemmmy.jrogue.dungeon.entities.decoration.EntityFountain;
import pw.lemmmy.jrogue.dungeon.generators.rooms.Room;

public class FeatureFountain extends SpecialRoomFeature {
	private Pcg32 rand = new Pcg32();
	
	@Override
	public void generate(Room room) {
		int fountainX = rand.nextInt(room.getWidth() - 2) + room.getRoomX() + 1;
		int fountainY = rand.nextInt(room.getHeight() - 2) + room.getRoomY() + 1;
		
		EntityFountain fountain = new EntityFountain(room.getLevel().getDungeon(), room.getLevel(), fountainX, fountainY);
		room.getLevel().addEntity(fountain);
	}
}
