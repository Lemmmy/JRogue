package jr.dungeon.entities.player;

import org.apache.commons.lang3.StringUtils;

public enum Attribute {
    STRENGTH, AGILITY, DEXTERITY, CONSTITUTION, INTELLIGENCE, WISDOM, CHARISMA;
    
    public String getName() {
        return StringUtils.capitalize(name().toLowerCase());
    }
}
