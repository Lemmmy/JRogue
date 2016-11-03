package pw.lemmmy.jrogue.dungeon.entities.roles;

import pw.lemmmy.jrogue.dungeon.items.ItemStack;

import java.util.List;

public abstract class Role {
	public abstract String getName();

	public abstract int getStartingHealth();
	public abstract List<ItemStack> getStartingItems();

	public abstract int getStrength();
	public abstract int getAgility();
	public abstract int getDexterity();
	public abstract int getConstitution();
	public abstract int getIntelligence();
	public abstract int getWisdom();
	public abstract int getCharisma();

	public abstract float getStrengthRemaining();
	public abstract float getAgilityRemaining();
	public abstract float getDexterityRemaining();
	public abstract float getConstitutionRemaining();
	public abstract float getIntelligenceRemaining();
	public abstract float getWisdomRemaining();
	public abstract float getCharismaRemaining();
}
