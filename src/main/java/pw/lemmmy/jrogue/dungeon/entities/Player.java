package pw.lemmmy.jrogue.dungeon.entities;

import com.github.alexeyr.pcg.Pcg32;
import pw.lemmmy.jrogue.dungeon.*;
import pw.lemmmy.jrogue.dungeon.entities.actions.ActionKick;
import pw.lemmmy.jrogue.dungeon.entities.actions.ActionMove;
import pw.lemmmy.jrogue.dungeon.entities.effects.InjuredFoot;
import pw.lemmmy.jrogue.dungeon.entities.effects.StrainedLeg;
import pw.lemmmy.jrogue.utils.Utils;

public class Player extends LivingEntity {
	private Pcg32 rand = new Pcg32();

	private String name;
	private Role role;

	private int baseSpeed = Dungeon.NORMAL_SPEED;

	private int strength;
	private int agility;
	private int dexterity;
	private int constitution;
	private int intelligence;
	private int wisdom;
	private int charisma;

	public Player(Dungeon dungeon, Level level, int x, int y, String name, Role role) {
		super(dungeon, level, x, y, 1);

		this.name = name;
		this.role = role;

		this.strength = role.getStrength() + (int) ((float) Math.ceil(role.getStrength() * rand.nextFloat(role.getStrengthRemaining())));
		this.agility = role.getAgility() + (int) ((float) Math.ceil(role.getAgility() * rand.nextFloat(role.getAgilityRemaining())));
		this.dexterity = role.getDexterity() + (int) ((float) Math.ceil(role.getDexterity() * rand.nextFloat(role.getDexterityRemaining())));
		this.constitution = role.getConstitution() + (int) ((float) Math.ceil(role.getConstitution() * rand.nextFloat(role.getConstitutionRemaining())));
		this.intelligence = role.getIntelligence() + (int) ((float) Math.ceil(role.getIntelligence() * rand.nextFloat(role.getIntelligenceRemaining())));
		this.wisdom = role.getWisdom() + (int) ((float) Math.ceil(role.getWisdom() * rand.nextFloat(role.getWisdomRemaining())));
		this.charisma = role.getCharisma() + (int) ((float) Math.ceil(role.getCharisma() * rand.nextFloat(role.getCharismaRemaining())));

		setHealth(getMaxHealth());

		this.setMovementPoints(Dungeon.NORMAL_SPEED);
	}

	public int getStrength() {
		return strength;
	}

	public int getAgility() {
		return agility;
	}

	public int getDexterity() {
		return dexterity;
	}

	public int getConstitution() {
		return constitution;
	}

	public int getIntelligence() {
		return intelligence;
	}

	public int getWisdom() {
		return wisdom;
	}

	public int getCharisma() {
		return charisma;
	}

	public int getConstitutionBonus() {
		return (int) Math.floor(0.25 * getConstitution() - 2);
	}

	@Override
	public int getMaxHealth() {
		return 10 + getConstitutionBonus();
	}

	@Override
	protected void onDamage(DamageSource damageSource, int damage, Entity attacker, boolean isPlayer) {

	}

	@Override
	protected void onDie(DamageSource damageSource) {
		getDungeon().You("die.");
	}

	@Override
	public int getMovementSpeed() {
		int speed = baseSpeed;

		if (hasStatusEffect(InjuredFoot.class)) {
			speed -= 1;
		}

		if (hasStatusEffect(StrainedLeg.class)) {
			speed -= 1;
		}

		return speed;
	}

	@Override
	public String getName(boolean requiresCapitalisation) {
		return name;
	}

	@Override
	public EntityAppearance getAppearance() {
		return EntityAppearance.APPEARANCE_PLAYER;
	}

	@Override
	public void update() {
		super.update();

		if (getHealth() > getMaxHealth()) {
			setHealth(getMaxHealth());
		}
	}

	@Override
	protected void onKick(LivingEntity kicker, boolean isPlayer, int x, int y) {
		getDungeon().You("step on your own foot.");
	}

	@Override
	protected void onWalk(LivingEntity walker, boolean isPlayer) {}

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
			setAction(new ActionMove(getDungeon(), this, newX, newY));
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
					getDungeon().log(String.format("Invalid direction '[YELLOW]%s[]'.", response));
				} else {
					// TODO: If the player has low wisdom, bypass the foot/leg check.
					// Damage their injured foot/leg further.

					if (hasStatusEffect(InjuredFoot.class)) {
						getDungeon().Your("foot is in no shape for kicking.");
						return;
					}

					if (hasStatusEffect(StrainedLeg.class)) {
						getDungeon().Your("leg is in no shape for kicking.");
						return;
					}

					Integer[] d = Utils.MOVEMENT_CHARS.get(response);

					if (getLevel().getEntitiesAt(getX() + d[0], getY() + d[1]).size() > 0) {
						setAction(new ActionKick(getDungeon(), Player.this, d, getLevel().getEntitiesAt(getX() + d[0], getY() + d[1]).get(0)));
					} else {
						setAction(new ActionKick(getDungeon(), Player.this, d));
					}

					getDungeon().turn();
				}
			}
		}, true));
	}

	@Override
	public boolean canBeWalkedOn() {
		return false;
	}

	public Role getRole() {
		return role;
	}
}
