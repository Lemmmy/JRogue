package pw.lemmmy.jrogue.dungeon.entities.roles;

import pw.lemmmy.jrogue.dungeon.items.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Wizard extends Role {
	@Override
	public String getName() {
		return "Wizard";
	}

	@Override
	public int getStartingHealth() {
		return 10;
	}

	@Override
	public List<ItemStack> getStartingItems() {
		return new ArrayList<ItemStack>();
	}

	@Override
	public int getStrength() {
		return 7;
	}

	@Override
	public int getAgility() {
		return 7;
	}

	@Override
	public int getDexterity() {
		return 7;
	}

	@Override
	public int getConstitution() {
		return 7;
	}

	@Override
	public int getIntelligence() {
		return 10;
	}

	@Override
	public int getWisdom() {
		return 7;
	}

	@Override
	public int getCharisma() {
		return 7;
	}

	@Override
	public float getStrengthRemaining() {
		return 0.1f;
	}

	@Override
	public float getAgilityRemaining() {
		return 0.1f;
	}

	@Override
	public float getDexterityRemaining() {
		return 0.2f;
	}

	@Override
	public float getConstitutionRemaining() {
		return 0.2f;
	}

	@Override
	public float getIntelligenceRemaining() {
		return 0.3f;
	}

	@Override
	public float getWisdomRemaining() {
		return 0.1f;
	}

	@Override
	public float getCharismaRemaining() {
		return 0.1f;
	}
}
