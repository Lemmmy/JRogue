package pw.lemmmy.jrogue.dungeon.entities;

import pw.lemmmy.jrogue.dungeon.*;
import pw.lemmmy.jrogue.dungeon.entities.actions.ActionKick;
import pw.lemmmy.jrogue.dungeon.entities.actions.ActionMove;
import pw.lemmmy.jrogue.dungeon.entities.effects.InjuredFoot;
import pw.lemmmy.jrogue.dungeon.entities.effects.StrainedLeg;
import pw.lemmmy.jrogue.utils.Utils;

public class Player extends LivingEntity {
	private String name;

	private int baseSpeed = 12;

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
		int speed = baseSpeed;

		if (hasStatusEffect(InjuredFoot.class)) {
			baseSpeed -= 1;
		}

		if (hasStatusEffect(StrainedLeg.class)) {
			baseSpeed -= 1;
		}

		return speed;
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
		super.update();

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

		if (tile.getType().getSolidity() != TileType.Solidity.SOLID) {
			addAction(new ActionMove(getDungeon(), this, newX, newY));

			if (tile.getType().onWalk() != null) {
				getDungeon().log(tile.getType().onWalk());
			}
		} else {
			if (tile.getType() == TileType.TILE_ROOM_DOOR_CLOSED) {
				getDungeon().The("door is locked.");
			}
		}

		getDungeon().turn();
	}

	public int getVisibilityRange() {
		return 15; // TODO: Make this vary based on light
	}

	public int getCorridorVisibilityRange() {
		return 4; // TODO: Make this vary based on light
	}

	public void kick() {
		getDungeon().prompt(new Prompt("Kick in what direction?", null, new Prompt.PromptCallback() {
			@Override
			public void onNoResponse() {
				getDungeon().log("Nevermind.");
			}

			@Override
			public void onInvalidResponse(char response) {}

			@Override
			public void onResponse(char response) {
				if (!Utils.MOVEMENT_CHARS.containsKey(response)) {
					getDungeon().log(String.format("Invalid direction [YELLOW]'%s'[].", response));
				} else {
					Integer[] d = Utils.MOVEMENT_CHARS.get(response);
					addAction(new ActionKick(getDungeon(), Player.this, d));
					getDungeon().turn();
				}
			}
		}, true));
	}
}
