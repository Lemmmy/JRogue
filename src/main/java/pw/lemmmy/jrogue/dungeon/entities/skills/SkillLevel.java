package pw.lemmmy.jrogue.dungeon.entities.skills;

public enum SkillLevel {
	UNSKILLED(-2),
	BEGINNER(0),
	ADVANCED(1),
	EXPERT(2),
	MASTER(3);
	
	int meleeWeaponDamage;
	
	SkillLevel(int meleeWeaponDamage) {
		this.meleeWeaponDamage = meleeWeaponDamage;
	}
	
	public int getMeleeWeaponDamage() {
		return meleeWeaponDamage;
	}
}
