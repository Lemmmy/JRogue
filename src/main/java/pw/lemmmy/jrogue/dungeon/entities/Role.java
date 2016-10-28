package pw.lemmmy.jrogue.dungeon.entities;

public enum Role {
	ROLE_WIZARD(
		"Wizard",
		10,
		7, 7, 7, 7, 10, 7, 7,
		0.1f, 0.1f, 0.2f, 0.2f, 0.3f, 0.1f, 0.1f
	);

	private String name;

	private int health;

	private int strength;
	private int agility;
	private int dexterity;
	private int constitution;
	private int intelligence;
	private int wisdom;
	private int charisma;

	private float strengthRemaining;
	private float agilityRemaining;
	private float dexterityRemaining;
	private float constitutionRemaining;
	private float intelligenceRemaining;
	private float wisdomRemaining;
	private float charismaRemaining;

	Role(String name, int health,
		 int strength, int agility, int dexterity, int constitution, int intelligence, int wisdom, int charisma,
		 float strengthRemaining, float agilityRemaining, float dexterityRemaining, float constitutionRemaining,
		 float intelligenceRemaining, float wisdomRemaining, float charismaRemaining) {
		this.name = name;

		this.health = health;

		this.strength = strength;
		this.agility = agility;
		this.dexterity = dexterity;
		this.constitution = constitution;
		this.intelligence = intelligence;
		this.wisdom = wisdom;
		this.charisma = charisma;

		this.strengthRemaining = strengthRemaining;
		this.agilityRemaining = agilityRemaining;
		this.dexterityRemaining = dexterityRemaining;
		this.constitutionRemaining = constitutionRemaining;
		this.intelligenceRemaining = intelligenceRemaining;
		this.wisdomRemaining = wisdomRemaining;
		this.charismaRemaining = charismaRemaining;
	}

	public String getName() {
		return name;
	}

	public int getHealth() {
		return health;
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

	public float getStrengthRemaining() {
		return strengthRemaining;
	}

	public float getAgilityRemaining() {
		return agilityRemaining;
	}

	public float getDexterityRemaining() {
		return dexterityRemaining;
	}

	public float getConstitutionRemaining() {
		return constitutionRemaining;
	}

	public float getIntelligenceRemaining() {
		return intelligenceRemaining;
	}

	public float getWisdomRemaining() {
		return wisdomRemaining;
	}

	public float getCharismaRemaining() {
		return charismaRemaining;
	}
}
