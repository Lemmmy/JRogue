package jr.dungeon.entities.decoration;

import com.google.gson.annotations.Expose;
import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.EntityAppearance;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.interfaces.Decorative;
import jr.dungeon.serialisation.Registered;
import jr.dungeon.wishes.Wishable;
import jr.language.Lexicon;
import jr.language.Noun;
import jr.utils.Point;
import jr.utils.RandomUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Wishable(name="small cave crystal|cave crystal small")
@Registered(id="caveCrystalSmall")
public class EntityCaveCrystalSmall extends Entity implements Decorative {
    @Expose private CrystalColour colour;
    
    public EntityCaveCrystalSmall(Dungeon dungeon, Level level, Point position) {
        super(dungeon, level, position);
        
        colour = RandomUtils.randomFrom(CrystalColour.values());
    }
    
    @Override
    public Noun getName(EntityLiving observer) {
        return Lexicon.crystal;
    }
    
    @Override
    public EntityAppearance getAppearance() {
        return colour.getAppearance();
    }
    
    @Override
    public boolean canBeWalkedOn() {
        return true;
    }
    
    @Override
    public boolean isStatic() {
        return true;
    }
    
    @AllArgsConstructor
    @Getter
    public enum CrystalColour {
        WHITE(EntityAppearance.APPEARANCE_CAVE_CRYSTAL_SMALL_WHITE),
        BLUE(EntityAppearance.APPEARANCE_CAVE_CRYSTAL_SMALL_BLUE),
        CYAN(EntityAppearance.APPEARANCE_CAVE_CRYSTAL_SMALL_CYAN);
        
        private EntityAppearance appearance;
    }
}
