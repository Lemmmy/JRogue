package pw.lemmmy.jrogue.dungeon.entities;

import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.Level;

public abstract class Entity {
	private int x;
	private int y;

	private Dungeon dungeon;
	private Level level;

	private int movementPoints = 0;

	public Entity(Dungeon dungeon, Level level, int x, int y) {
		this.dungeon = dungeon;
		this.level = level;
		this.x = x;
		this.y = y;
	}

	public abstract String getName();

	public abstract Appearance getAppearance();

	public int getMovementPoints() {
		return movementPoints;
	}

	protected void addMovementPoints(int points) {
		movementPoints += points;

		if (movementPoints > 12) {
			for (int i = 0; i < Math.floor(movementPoints / 12); i++) {
				move();
			}

			movementPoints = movementPoints % 12;
		}
	}

	protected abstract void move();

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public Dungeon getDungeon() {
		return dungeon;
	}

	public Level getLevel() {
		return level;
	}

	public void setLevel(Level level) {
		this.level = level;
	}
}
