package pw.lemmmy.jrogue.dungeon.items;

import pw.lemmmy.jrogue.dungeon.entities.LivingEntity;

public class ItemStaff extends ItemWeapon {
	@Override
	public String getName(boolean requiresCapitalisation, boolean plural) {
		if (requiresCapitalisation) {
			return plural ? "Staves" : "Staff";
		} else {
			return plural ? "staves" : "staff";
		}
	}

	@Override
	public int getWeight() {
		return 50;
	}

	@Override
	public ItemAppearance getAppearance() {
		return ItemAppearance.APPEARANCE_STAFF;
	}

	@Override
	public void hit(LivingEntity attacker, LivingEntity victim) {

	}

	@Override
	public void zap(LivingEntity attacker, LivingEntity victim) {

	}

	@Override
	public void fire(LivingEntity attacker, LivingEntity victim) {

	}

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
		return true;
	}
}
