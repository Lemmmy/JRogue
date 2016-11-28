package pw.lemmmy.jrogue.dungeon.entities;

import com.github.alexeyr.pcg.Pcg32;
import org.apache.commons.lang3.StringUtils;
import pw.lemmmy.jrogue.dungeon.*;
import pw.lemmmy.jrogue.dungeon.entities.actions.ActionEat;
import pw.lemmmy.jrogue.dungeon.entities.actions.ActionKick;
import pw.lemmmy.jrogue.dungeon.entities.actions.ActionMove;
import pw.lemmmy.jrogue.dungeon.entities.effects.InjuredFoot;
import pw.lemmmy.jrogue.dungeon.entities.effects.StrainedLeg;
import pw.lemmmy.jrogue.dungeon.entities.roles.Role;
import pw.lemmmy.jrogue.dungeon.entities.skills.Skill;
import pw.lemmmy.jrogue.dungeon.entities.skills.SkillLevel;
import pw.lemmmy.jrogue.dungeon.items.Item;
import pw.lemmmy.jrogue.dungeon.items.ItemComestible;
import pw.lemmmy.jrogue.dungeon.items.ItemStack;
import pw.lemmmy.jrogue.dungeon.items.ItemWeaponMelee;
import pw.lemmmy.jrogue.dungeon.tiles.Tile;
import pw.lemmmy.jrogue.dungeon.tiles.TileType;
import pw.lemmmy.jrogue.utils.Utils;

import java.util.*;
import java.util.List;

public class Player extends LivingEntity {
	private static final char[] INVENTORY_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();

	private Pcg32 rand = new Pcg32();

	private String name;
	private Role role;

	private int baseSpeed = Dungeon.NORMAL_SPEED;

	private int nutrition;

	private int strength;
	private int agility;
	private int dexterity;
	private int constitution;
	private int intelligence;
	private int wisdom;
	private int charisma;

	private Map<Character, ItemStack> inventory;
	private Map<Skill, SkillLevel> skills;

	public Player(Dungeon dungeon, Level level, int x, int y, String name, Role role) {
		super(dungeon, level, x, y, 1);

		this.name = name;
		this.role = role;

		nutrition = 1000;

		strength = role.getStrength() + (int) ((float) Math.ceil(role.getStrength() * rand.nextFloat(role.getStrengthRemaining())));
		agility = role.getAgility() + (int) ((float) Math.ceil(role.getAgility() * rand.nextFloat(role.getAgilityRemaining())));
		dexterity = role.getDexterity() + (int) ((float) Math.ceil(role.getDexterity() * rand.nextFloat(role.getDexterityRemaining())));
		constitution = role.getConstitution() + (int) ((float) Math.ceil(role.getConstitution() * rand.nextFloat(role.getConstitutionRemaining())));
		intelligence = role.getIntelligence() + (int) ((float) Math.ceil(role.getIntelligence() * rand.nextFloat(role.getIntelligenceRemaining())));
		wisdom = role.getWisdom() + (int) ((float) Math.ceil(role.getWisdom() * rand.nextFloat(role.getWisdomRemaining())));
		charisma = role.getCharisma() + (int) ((float) Math.ceil(role.getCharisma() * rand.nextFloat(role.getCharismaRemaining())));

		inventory = new LinkedHashMap<>();
		skills = new HashMap<>(role.getStartingSkills());

		role.getStartingItems().forEach(i -> inventory.put(getAvailableInventoryLetter(), i));
		setLeftHand(role.getStartingLeftHand());
		setRightHand(role.getStartingRightHand());

		setHealth(getMaxHealth());
		setMovementPoints(Dungeon.NORMAL_SPEED);
	}

	public int getNutrition() {
		return nutrition;
	}

	public NutritionState getNutritionState() {
		if (nutrition >= 1500) {
			return NutritionState.CHOKING;
		} else if (nutrition >= 1000) {
			return NutritionState.STUFFED;
		} else if (nutrition >= 600) {
			return NutritionState.NOT_HUNGRY;
		} else if (nutrition >= 300) {
			return NutritionState.HUNGRY;
		} else if (nutrition >= 0) {
			return NutritionState.STARVING;
		} else {
			return NutritionState.FAINTING;
		}
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
	public Size getSize() {
		return LivingEntity.Size.LARGE;
	}

	@Override
	public String getName(boolean requiresCapitalisation) {
		return requiresCapitalisation ? StringUtils.capitalize(name) : name;
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

		nutrition--;
	}

	@Override
	protected void onKick(LivingEntity kicker, boolean isPlayer, int x, int y) {
		getDungeon().You("step on your own foot.");
	}

	@Override
	protected void onWalk(LivingEntity walker, boolean isPlayer) {}

	private void walkAction(Tile tile, int newX, int newY) {
		if (tile.getType().getSolidity() != TileType.Solidity.SOLID) {
			setAction(new ActionMove(getDungeon(), this, newX, newY));
		} else if (tile.getType() == TileType.TILE_ROOM_DOOR_CLOSED) {
			getDungeon().The("door is locked.");
		}
	}

	public void walk(int dx, int dy) {
		dx = Math.max(-1, Math.min(1, dx));
		dy = Math.max(-1, Math.min(1, dy));

		int newX = getX() + dx;
		int newY = getY() + dy;

		Tile tile = getLevel().getTile(newX, newY);

		// TODO: More in-depth movement verification
		// 		 e.g. a player shouldn't be able to travel diagonally to
		//       a different tile type than the one they are standing on
		//       unless it is a door

		if (tile == null) {
			getDungeon().log("It would be silly to walk there.");
			return;
		}

		List<Entity> destEntities = getLevel().getEntitiesAt(newX, newY);

		if (destEntities.size() > 0) {
			// TODO: Ask the player to confirm if they want to attack something silly (e.g. their familiar or a clerk)

			Optional<Entity> ent = destEntities.stream()
				.filter(e -> e instanceof LivingEntity)
				.findFirst();

			if (ent.isPresent()) {
				if (getRightHand() != null && getRightHand().getItem() instanceof ItemWeaponMelee) {
					((ItemWeaponMelee) getRightHand().getItem()).hit(this, (LivingEntity) ent.get());
				} else {
					walkAction(tile, newX, newY);
				}
			} else {
				walkAction(tile, newX, newY);
			}
		} else {
			walkAction(tile, newX, newY);
		} // TODO: Restructure this mess

		getDungeon().turn();
	}

	public int getLightLevel() {
		return getLevel().getTile(getX(), getY()).getLightIntensity();
	}

	public int getVisibilityRange() {
		return 10 * ((getLightLevel() - 20) / 100) + 10;
	}

	public int getCorridorVisibilityRange() {
		return 2 * ((getLightLevel() - 20) / 100) + 5;
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

	public void eat() {
		List<Entity> floorEntities = getLevel().getEntitiesAt(getX(), getY());

		if (floorEntities.size() > 0) {
			for (Entity entity : floorEntities) {
				if (entity instanceof EntityItem && ((EntityItem) entity).getItem() instanceof ItemComestible) {
					Item item = ((EntityItem) entity).getItem();

					String promptString = "";

					if (item.beginsWithVowel()) {
						promptString = String.format("There is an %s here. Eat it?", item.getName(false, false));
					} else {
						promptString = String.format("There is a %s here. Eat it?", item.getName(false, false));
					}

					getDungeon().prompt(new Prompt(promptString, new char[] {'y', 'n'}, new Prompt.PromptCallback() {
						@Override
						public void onNoResponse() {
							getDungeon().log("Nevermind.");
						}

						@Override
						public void onInvalidResponse(char response) {
							getDungeon().log(String.format("Invalid response '[YELLOW]%s[]'.", response));
						}

						@Override
						public void onResponse(char response) {
							if (response == 'n') {
								// TODO
								return;
							}

							setAction(new ActionEat(getDungeon(), Player.this, (ItemComestible) item, (EntityItem) entity));
							getDungeon().turn();
						}
					}, true));

					break;
				}
			}
		} else {
			// TODO
		}
	}

	public ItemComestible.EatenState consume(ItemComestible item) {
		if (item.getEatenState() == ItemComestible.EatenState.UNEATEN) {
			getDungeon().You("start eating the %s.", item.getName(false, false));

			nutrition += Math.floor(item.getNutrition() / 2);

			if (item.getStatusEffects(this) != null &&
				getNutritionState() != NutritionState.STARVING && getNutritionState() != NutritionState.FAINTING &&
				getWisdom() > 6) {

				getDungeon().You("feel funny.");
				getDungeon().You("think it might not be a good idea to continue eating.");
			}

			return ItemComestible.EatenState.PARTLY_EATEN;
		} else if (item.getEatenState() == ItemComestible.EatenState.PARTLY_EATEN) {
			getDungeon().You("finish eating the %s.", item.getName(false, false));

			nutrition += Math.ceil(item.getNutrition() / 2);

			if (item.getStatusEffects(this) != null) {
				item.getStatusEffects(this).forEach(this::addStatusEffect);
			}

			return ItemComestible.EatenState.EATEN;
		}

		return ItemComestible.EatenState.EATEN;
	}

	public char getAvailableInventoryLetter() {
		for (char letter : INVENTORY_CHARS) {
			if (!inventory.containsKey(letter)) {
				return letter;
			}
		}

		return ' ';
	}

	@Override
	public int getDepth() {
		return 3;
	}

	public Map<Character, ItemStack> getInventory() {
		return inventory;
	}

	public SkillLevel getSkillLevel(Skill skill) {
		if (!skills.containsKey(skill)) {
			return SkillLevel.UNSKILLED;
		} else {
			return skills.get(skill);
		}
	}

	public enum NutritionState {
		CHOKING("Choking", 2),
		STUFFED("Stuffed", 1),
		NOT_HUNGRY("Not hungry"),
		HUNGRY("Hungry", 1),
		STARVING("Starving", 2),
		FAINTING("Fainting", 2);

		private String string;
		private int importance = 0;

		NutritionState(String string) {
			this(string, 0);
		}

		NutritionState(String string, int importance) {
			this.string = string;
			this.importance = importance;
		}

		@Override
		public String toString() {
			return string;
		}

		public int getImportance() {
			return importance;
		}
	}
}
