package pw.lemmmy.jrogue.dungeon.items;

import pw.lemmmy.jrogue.dungeon.entities.LivingEntity;
import pw.lemmmy.jrogue.utils.Utils;

public abstract class ItemSword extends ItemWeaponMelee implements HasMaterial {
	private Material material;

	public ItemSword(Material material) {
		this.material = material;
	}

	public abstract String getSwordName();

	@Override
	public String getName(boolean requiresCapitalisation, boolean plural) {
		String s = "p";
		String material = this.material.getName(requiresCapitalisation);

		return material + " " + getSwordName() + s;
	}

	@Override
	public int getWeight() {
		return 40 + getMaterial().getValue();
	}

	@Override
	public void zap(LivingEntity attacker, LivingEntity victim) {}

	@Override
	public void fire(LivingEntity attacker, LivingEntity victim) {}

	@Override
	public boolean isMelee() {
		return true;
	}

	@Override
	public boolean isRanged() {
		return false;
	}

	@Override
	public boolean isMagic() {
		return false;
	}

	@Override
	public void onHit(LivingEntity attacker, LivingEntity victim) {
		hitLog("You hit the %s!", "The %s hits you!", "The %s hits the %s!", attacker, victim);
	}

	@Override
	public int getSmallDamage() {
		return Utils.roll(getMaterial().getBaseDamage());
	}

	@Override
	public int getLargeDamage() {
		return Utils.roll(getMaterial().getBaseDamage());
	}

	@Override
	public Material getMaterial() {
		return material;
	}
}
