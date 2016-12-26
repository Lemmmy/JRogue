package pw.lemmmy.jrogue.dungeon.generators.rooms;

import com.github.alexeyr.pcg.Pcg32;
import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.entities.EntityFountain;

public class RoomFountain extends RoomBasic {
	private Pcg32 rand = new Pcg32();

	public RoomFountain(Level level, int roomX, int roomY, int roomWidth, int roomHeight) {
		super(level, roomX, roomY, roomWidth, roomHeight);
	}

	@Override
	public void addFeatures() {
		int fountainX = rand.nextInt(getRoomWidth() - 2) + getRoomX() + 1;
		int fountainY = rand.nextInt(getRoomHeight() - 2) + getRoomY() + 1;

		EntityFountain fountain = new EntityFountain(getLevel().getDungeon(), getLevel(), fountainX, fountainY);
		getLevel().addEntity(fountain);
	}
}
