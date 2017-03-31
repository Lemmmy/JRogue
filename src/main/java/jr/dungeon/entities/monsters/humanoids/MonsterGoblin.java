package jr.dungeon.entities.monsters.humanoids;

import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.DamageSourceType;
import jr.dungeon.entities.EntityAppearance;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.actions.Action;
import jr.dungeon.entities.actions.ActionMelee;
import jr.dungeon.entities.containers.Container;
import jr.dungeon.entities.effects.StatusEffect;
import jr.dungeon.entities.events.EntityDamagedEvent;
import jr.dungeon.entities.events.EntityDeathEvent;
import jr.dungeon.entities.monsters.Monster;
import jr.dungeon.entities.monsters.ai.stateful.StatefulAI;
import jr.dungeon.entities.monsters.ai.stateful.humanoid.StateLurk;
import jr.dungeon.events.DungeonEventHandler;
import jr.dungeon.items.ItemStack;
import jr.dungeon.items.Material;
import jr.dungeon.items.weapons.ItemDagger;
import jr.dungeon.items.weapons.ItemWeaponMelee;
import jr.utils.RandomUtils;

import java.util.List;

public class MonsterGoblin extends Monster {
	private static final float DAGGER_DROP_CHANCE = 0.75f;
	
	public MonsterGoblin(Dungeon dungeon, Level level, int x, int y) {
		super(dungeon, level, x, y);
		
		if (dungeon == null) {
			return;
		}
		
		StatefulAI ai = new StatefulAI(this);
		setAI(ai);
		ai.setDefaultState(new StateLurk(ai, 0));
		
		setInventoryContainer(new Container("Inventory"));
		
		getContainer().ifPresent(c -> c.add(new ItemStack(new ItemDagger(Material.IRON))).ifPresent(this::setRightHand));
	}
	
	@Override
	public String getName(EntityLiving observer, boolean requiresCapitalisation) {
		return requiresCapitalisation ? "Goblin" : "goblin";
	}
	
	@Override
	public EntityAppearance getAppearance() {
		return EntityAppearance.APPEARANCE_GOBLIN;
	}
	
	@Override
	public int getBaseArmourClass() {
		return 10;
	}
	
	@Override
	public int getMovementSpeed() {
		return 9;
	}
	
	@Override
	public Size getSize() {
		return Size.LARGE;
	}
	
	@Override
	public boolean isHostile() {
		return true;
	}
	
	@Override
	public int getWeight() {
		return 400;
	}
	
	@Override
	public int getNutrition() {
		return 120;
	}
	
	@Override
	public float getCorpseChance() {
		return 0.8f;
	}
	
	@Override
	public List<StatusEffect> getCorpseEffects(EntityLiving victim) {
		return null;
	}
	
	@Override
	public int getVisibilityRange() {
		return 12;
	}
	
	@Override
	public boolean canMoveDiagonally() {
		return true;
	}
	
	@Override
	public boolean canMeleeAttack() {
		return true;
	}
	
	@Override
	public boolean canRangedAttack() {
		return false;
	}
	
	@Override
	public boolean canMagicAttack() {
		return false;
	}
	
	@Override
	public void meleeAttack(EntityLiving victim) {
		DamageSourceType source = DamageSourceType.UNKNOWN;
		
		if (getRightHand() != null && getRightHand().getItem() instanceof ItemWeaponMelee) {
			source = ((ItemWeaponMelee) getRightHand().getItem()).getMeleeDamageSource();
		}
		
		setAction(new ActionMelee(
			getDungeon().getPlayer(),
			source,
			RandomUtils.roll(3),
			(Action.CompleteCallback) entity -> getDungeon().orangeThe(
				"%s hits you with a dagger!",
				getName(getDungeon().getPlayer(), false)
			)
		));
	}
	
	@DungeonEventHandler(selfOnly = true)
	public void onDamage(EntityDamagedEvent e) {
		getDungeon().logRandom(
			"It yelps.",
			"It yells.",
			"It cries.",
			"It screams."
		);
	}
	
	@DungeonEventHandler(selfOnly = true)
	public void onDie(EntityDeathEvent e) {
		if (getRightHand() != null && RandomUtils.randomFloat() < DAGGER_DROP_CHANCE) {
			dropItem(getRightHand().getStack());
		}
	}
}
