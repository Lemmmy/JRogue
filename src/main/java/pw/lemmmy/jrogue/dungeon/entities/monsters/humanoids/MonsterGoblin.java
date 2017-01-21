package pw.lemmmy.jrogue.dungeon.entities.monsters.humanoids;

import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.entities.DamageSource;
import pw.lemmmy.jrogue.dungeon.entities.EntityAppearance;
import pw.lemmmy.jrogue.dungeon.entities.EntityLiving;
import pw.lemmmy.jrogue.dungeon.entities.actions.ActionMelee;
import pw.lemmmy.jrogue.dungeon.entities.actions.EntityAction;
import pw.lemmmy.jrogue.dungeon.entities.containers.Container;
import pw.lemmmy.jrogue.dungeon.entities.effects.StatusEffect;
import pw.lemmmy.jrogue.dungeon.entities.monsters.Monster;
import pw.lemmmy.jrogue.dungeon.entities.monsters.ai.stateful.StatefulAI;
import pw.lemmmy.jrogue.dungeon.entities.monsters.ai.stateful.humanoid.StateLurk;
import pw.lemmmy.jrogue.dungeon.items.ItemStack;
import pw.lemmmy.jrogue.dungeon.items.Material;
import pw.lemmmy.jrogue.dungeon.items.weapons.ItemDagger;
import pw.lemmmy.jrogue.dungeon.items.weapons.ItemWeaponMelee;
import pw.lemmmy.jrogue.utils.RandomUtils;

import java.util.List;

public class MonsterGoblin extends Monster {
	private static final float DAGGER_DROP_CHANCE = 0.75f;
	
	public MonsterGoblin(Dungeon dungeon, Level level, int x, int y) {
		super(dungeon, level, x, y);
		
		setAI(new StatefulAI(this));
		((StatefulAI) getAI()).setDefaultState(new StateLurk((StatefulAI) getAI(), 0));
		
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
		DamageSource source = DamageSource.UNKNOWN;
		
		if (getRightHand() != null && getRightHand().getItem() instanceof ItemWeaponMelee) {
			source = ((ItemWeaponMelee) getRightHand().getItem()).getMeleeDamageSource();
		}
		
		setAction(new ActionMelee(
			getDungeon().getPlayer(),
			source,
			RandomUtils.roll(3),
			(EntityAction.CompleteCallback) entity -> getDungeon().orangeThe(
				"%s hits you with a dagger!",
				getName(getDungeon().getPlayer(), false)
			)
		));
	}
	
	@Override
	protected void onDamage(DamageSource damageSource, int damage, EntityLiving attacker, boolean isPlayer) {
		getDungeon().logRandom(
			"It yelps.",
			"It yells.",
			"It cries.",
			"It screams."
		);
	}
	
	@Override
	protected void onDie(DamageSource damageSource, int damage, EntityLiving attacker, boolean isPlayer) {
		super.onDie(damageSource, damage, attacker, isPlayer);
		
		if (getRightHand() != null && RandomUtils.randomFloat() < DAGGER_DROP_CHANCE) {
			dropItem(getRightHand().getStack());
		}
	}
}
