package jr.dungeon.wishes;

import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.player.Player;
import jr.dungeon.tiles.Tile;
import jr.dungeon.tiles.TileFlag;
import jr.dungeon.tiles.TileType;
import jr.utils.Point;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

public class WishExplore implements Wish {
	@Override
	public void grant(Dungeon d, Player p, String... a) {
		boolean isGod = p.isGodmode();
		p.setGodmode(true);
		
		Level firstLevel = d.getLevel();
		Point firstLevelSpawn = p.getPosition();
		
		AtomicReference<Tile> firstSewerDown = new AtomicReference<>();
		
		for (int i = 0; i < 7; i++) {
			if (firstSewerDown.get() == null) {
				Arrays.stream(p.getLevel().tileStore.getTiles())
					.filter(t -> t.getType() == TileType.TILE_LADDER_DOWN)
					.findFirst().ifPresent(firstSewerDown::set);
			}
			
			Arrays.stream(p.getLevel().tileStore.getTiles())
				.filter(t -> t.getType() == TileType.TILE_ROOM_STAIRS_DOWN)
				.findFirst().ifPresent(t -> {
				p.setPosition(t.position);
				p.defaultVisitors.climbDown();
				d.greenYou("traverse to [CYAN]%s[].", d.getLevel());
			});
		}
		
		if (firstSewerDown.get() != null) {
			Tile fsdt = firstSewerDown.get();
			
			d.changeLevel(fsdt.getLevel(), fsdt.position);
			
			Arrays.stream(p.getLevel().tileStore.getTiles())
				.filter(t -> t.getType() == TileType.TILE_LADDER_DOWN)
				.findFirst().ifPresent(t -> {
				p.setPosition(t.position);
				p.defaultVisitors.climbDown();
				d.greenYou("traverse to [CYAN]%s[].", d.getLevel());
			});
			
			for (int i = 0; i < 7; i++) {
				Arrays.stream(p.getLevel().tileStore.getTiles())
					.filter(t -> (t.getType().getFlags() & TileFlag.DOWN) == TileFlag.DOWN)
					.findFirst().ifPresent(t -> {
					p.setPosition(t.position);
					p.defaultVisitors.climbDown();
					d.greenYou("traverse to [CYAN]%s[].", d.getLevel());
				});
			}
		}
		
		d.changeLevel(firstLevel, firstLevelSpawn);
		p.setGodmode(isGod);
	}
}
