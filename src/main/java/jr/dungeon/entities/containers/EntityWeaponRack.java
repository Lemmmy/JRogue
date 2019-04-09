package jr.dungeon.entities.containers;

import com.google.gson.annotations.Expose;
import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.EntityAppearance;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.events.EntityWalkedOnEvent;
import jr.dungeon.entities.interfaces.ContainerOwner;
import jr.dungeon.entities.interfaces.Lootable;
import jr.dungeon.events.EventHandler;
import jr.dungeon.serialisation.Registered;
import jr.dungeon.wishes.Wishable;
import jr.language.LanguageUtils;
import jr.language.Lexicon;
import jr.language.Noun;
import jr.utils.Point;

import java.util.Optional;

@Wishable(name="weapon rack")
@Registered(id="entityWeaponRack")
public class EntityWeaponRack extends Entity implements Lootable, ContainerOwner {
    @Expose private Container container;
    
    public EntityWeaponRack(Dungeon dungeon, Level level, Point position) {
        super(dungeon, level, position);
        
        container = new WeaponRackContainer(getName(null));
    }
    
    protected EntityWeaponRack() { super(); }
    
    @Override
    public Noun getName(EntityLiving observer) {
        return Lexicon.weaponRack.clone();
    }
    
    @Override
    public EntityAppearance getAppearance() {
        return container.isEmpty() ? EntityAppearance.APPEARANCE_WEAPON_RACK
                                   : EntityAppearance.APPEARANCE_WEAPON_RACK_STOCKED;
    }
    
    @Override
    public int getDepth() {
        return 1;
    }
    
    @Override
    public boolean isStatic() {
        return true;
    }
    
    @Override
    public Optional<Container> getContainer() {
        return Optional.ofNullable(container);
    }

    @Override
    public Optional<String> getLootSuccessString() {
        return Optional.of(String.format("You browse %s.", LanguageUtils.object(this)));
    }
    
    @EventHandler(selfOnly = true)
    public void onWalk(EntityWalkedOnEvent e) {
        if (e.isWalkerPlayer()) {
            getDungeon().log("There is %s here.", LanguageUtils.anObject(this));
        }
    }
    
    @Override
    public boolean canBeWalkedOn() {
        return true;
    }
}
