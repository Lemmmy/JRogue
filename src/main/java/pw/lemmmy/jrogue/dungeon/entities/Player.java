package pw.lemmmy.jrogue.dungeon.entities;

import pw.lemmmy.jrogue.dungeon.*;
import pw.lemmmy.jrogue.dungeon.entities.actions.ActionMove;

public class Player extends LivingEntity {
	private String name;

	public Player(Dungeon dungeon, Level level, int x, int y, String name) {
		super(dungeon, level, x, y);

		this.name = name;
	}

	@Override
	public int getMaxHealth() {
		return 10;
	}

	@Override
	protected void onDamage(DamageSource damageSource, int damage) {

	}

	@Override
	protected void onDie(DamageSource damageSource) {
		getDungeon().You("die.");
	}

	@Override
	public int getMovementSpeed() {
		return 12;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Appearance getAppearance() {
		return Appearance.APPEARANCE_PLAYER;
	}

	@Override
	protected void onKick(Entity kicker) {
		getDungeon().You("step on your own foot.");
	}

	@Override
	public void update() {
		getLevel().updateSight(this);
	}

	public void walk(int dx, int dy) {
		dx = Math.max(-1, Math.min(1, dx));
		dy = Math.max(-1, Math.min(1, dy));

		int newX = getX() + dx;
		int newY = getY() + dy;

		Tile tile = getLevel().getTileInfo(newX, newY);

		// TODO: More in-depth movement verification
		// 		 e.g. a player shouldn't be able to travel diagonally to
		//       a different tile type than the one they are standing on
		//       unless it is a door

		if (tile.getType().getSolidity() != Solidity.SOLID) {
			addAction(new ActionMove(this, newX, newY));
		} else {
			if (tile.getType() == TileType.TILE_ROOM_DOOR_CLOSED) {
				getDungeon().The("door is locked.");

				getDungeon().You("kick the door down!"); // TODO: Temporary
				getLevel().setTile(newX, newY, TileType.TILE_ROOM_DOOR_BROKEN);
			}
		}

		getDungeon().turn();
	}

	public int getVisibilityRange() {
		return 15; // TODO: Make this vary based on light
	}

	public int getCorridorVisibilityRange() {
		return 3; // TODO: Make this vary based on light
	}
}
