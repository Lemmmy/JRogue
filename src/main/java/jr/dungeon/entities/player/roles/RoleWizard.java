package jr.dungeon.entities.player.roles;

import jr.dungeon.entities.monsters.familiars.Cat;
import jr.dungeon.entities.monsters.familiars.Familiar;
import jr.dungeon.entities.player.Attribute;
import jr.dungeon.entities.player.Attributes;
import jr.dungeon.entities.skills.Skill;
import jr.dungeon.entities.skills.SkillLevel;
import jr.dungeon.items.ItemStack;
import jr.dungeon.items.magical.spells.Spell;
import jr.dungeon.items.magical.spells.SpellStrike;
import jr.dungeon.items.weapons.ItemStaff;
import jr.dungeon.serialisation.Registered;
import jr.utils.RandomUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Registered(id="roleWizard")
public class RoleWizard extends Role {
    static {
        registerRole(RoleWizard.class);
    }
    
    private ItemStack staff;
    
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
        List<ItemStack> itemList = new ArrayList<>();
        
        staff = new ItemStack(new ItemStaff());
        itemList.add(staff);
        
        return itemList;
    }
    
    @Override
    public ItemStack getStartingLeftHand() {
        return staff;
    }
    
    @Override
    public ItemStack getStartingRightHand() {
        return staff;
    }
    
    @Override
    public Map<Skill, SkillLevel> getStartingSkills() {
        Map<Skill, SkillLevel> skillMap = new HashMap<>();
        
        skillMap.put(Skill.SKILL_STAFF, SkillLevel.BEGINNER);
        
        skillMap.put(Skill.SKILL_SPELLS_ATTACK, SkillLevel.EXPERT);
        skillMap.put(Skill.SKILL_SPELLS_HEALING, SkillLevel.ADVANCED);
        skillMap.put(Skill.SKILL_SPELLS_DIVINATION, SkillLevel.EXPERT);
        skillMap.put(Skill.SKILL_SPELLS_ENCHANTMENT, SkillLevel.ADVANCED);
        skillMap.put(Skill.SKILL_SPELLS_CLERICAL, SkillLevel.ADVANCED);
        skillMap.put(Skill.SKILL_SPELLS_ESCAPE, SkillLevel.EXPERT);
        skillMap.put(Skill.SKILL_SPELLS_OTHER, SkillLevel.EXPERT);
        
        return skillMap;
    }
    
    @Override
    public Map<Character, Spell> getStartingSpells() {
        Map<Character, Spell> spellMap = new HashMap<>();
        
        spellMap.put('a', new SpellStrike());
        // spellMap.put('b', new SpellLightOrb());
        
        return spellMap;
    }
    
    @Override
    public void assignAttributes(Attributes attributes) {
        attributes.initialiseAttribute(Attribute.STRENGTH, 7);
        attributes.initialiseAttribute(Attribute.AGILITY, 7);
        attributes.initialiseAttribute(Attribute.DEXTERITY, 7);
        attributes.initialiseAttribute(Attribute.CONSTITUTION, 7);
        attributes.initialiseAttribute(Attribute.INTELLIGENCE, 10);
        attributes.initialiseAttribute(Attribute.WISDOM, 7);
        attributes.initialiseAttribute(Attribute.CHARISMA, 7);
    }
    
    @Override
    public int getMaxEnergy() {
        return RandomUtils.roll(1, 3, 4); // 4+d3
    }
    
    @Override
    public int getSpellcastingSuccessBase() {
        return 1;
    }
    
    @Override
    public int getSpellcastingSuccessEscape() {
        return 0;
    }
    
    @Override
    public Attribute getSpellcastingSuccessAttribute() {
        return Attribute.INTELLIGENCE;
    }
    
    @Override
    public Class<? extends Familiar> getStartingFamiliar() {
        return Cat.class;
    }
}
