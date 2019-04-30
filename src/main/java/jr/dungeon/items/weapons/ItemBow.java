package jr.dungeon.items.weapons;

import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.skills.Skill;
import jr.dungeon.items.ItemAppearance;
import jr.dungeon.items.projectiles.ItemArrow;
import jr.dungeon.items.projectiles.ItemProjectile;
import jr.dungeon.serialisation.Registered;
import jr.dungeon.wishes.Wishable;
import jr.language.Lexicon;
import jr.language.Noun;

import java.util.ArrayList;
import java.util.List;

@Wishable(name="bow")
@Registered(id="itemBow")
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
    public Noun getBaseName(EntityLiving observer) {
        return Lexicon.bow.clone();
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
