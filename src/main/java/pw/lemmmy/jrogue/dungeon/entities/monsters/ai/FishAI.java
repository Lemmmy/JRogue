package pw.lemmmy.jrogue.dungeon.entities.monsters.ai;

import com.github.alexeyr.pcg.Pcg32;
import pw.lemmmy.jrogue.dungeon.entities.monsters.Monster;
import pw.lemmmy.jrogue.dungeon.tiles.Tile;
import pw.lemmmy.jrogue.dungeon.tiles.TileType;
import pw.lemmmy.jrogue.utils.Utils;

import java.util.Arrays;

public class FishAI extends AI {
	private Pcg32 random = new Pcg32();

	private float moveProbability = 0.1f;

	public FishAI(Monster monster) {
		super(monster);
	}

	public void setMoveProbability(float moveProbability) {
		this.moveProbability = moveProbability;
	}

	@Override
	public void update() {
		if (random.nextFloat() < moveProbability) {
			Tile[] tiles = getMonster().getLevel().getAdjacentTiles(getMonster().getX(), getMonster().getY());
			Tile[] waterTiles = Arrays.stream(tiles).filter(t -> t != null && t.getType() != null && t.getType() == TileType.TILE_GROUND_WATER).toArray(Tile[]::new);

			if (waterTiles.length > 0) {
				Tile destination = Utils.randomFrom(waterTiles);

				moveTowards(destination.getX(), destination.getY());
			}
		}
	}
}
