package pw.lemmmy.jrogue.dungeon.items.weapons;

import pw.lemmmy.jrogue.dungeon.entities.skills.Skill;
import pw.lemmmy.jrogue.dungeon.items.ItemAppearance;
import pw.lemmmy.jrogue.dungeon.items.projectiles.ItemArrow;
import pw.lemmmy.jrogue.dungeon.items.projectiles.ItemProjectile;

import java.util.ArrayList;
import java.util.List;

public class ItemBow extends ItemProjectileLauncher {
	@Override
	public boolean isTwoHanded() {
		return true;
	}
	
	@Override
	public boolean isMagic() {
		return false;
	}
	
	@Override
	public int getToHitBonus() {
		return 0;
	}
	
	@Override
	public Skill getSkill() {
		return Skill.SKILL_BOW;
	}
	
	@Override
	public String getName(boolean requiresCapitalisation, boolean plural) {
		return (requiresCapitalisation ? "B" : "b") + "ow" + (plural ? "s" : "");
	}
	
	@Override
	public float getWeight() {
		return 40;
	}
	
	@Override
	public ItemAppearance getAppearance() {
		return ItemAppearance.APPEARANCE_BOW;
	}
	
	@Override
	public List<Class<? extends ItemProjectile>> getValidProjectiles() {
		List<Class<? extends ItemProjectile>> projectiles = new ArrayList<>();
		projectiles.add(ItemArrow.class);
		return projectiles;
	}
}
