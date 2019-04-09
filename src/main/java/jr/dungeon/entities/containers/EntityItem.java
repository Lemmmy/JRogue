package jr.dungeon.entities.containers;

import com.google.gson.annotations.Expose;
import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.EntityAppearance;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.effects.MercuryPoisoning;
import jr.dungeon.entities.events.EntityAddedEvent;
import jr.dungeon.entities.events.EntityKickedEntityEvent;
import jr.dungeon.entities.events.ItemDroppedEvent;
import jr.dungeon.entities.events.ItemDroppedOnEntityEvent;
import jr.dungeon.events.EventHandler;
import jr.dungeon.items.Item;
import jr.dungeon.items.ItemStack;
import jr.dungeon.items.Shatterable;
import jr.dungeon.items.valuables.ItemThermometer;
import jr.dungeon.serialisation.Registered;
import jr.dungeon.tiles.Solidity;
import jr.dungeon.tiles.TileType;
import jr.language.LanguageUtils;
import jr.language.Noun;
import jr.language.transformers.Capitalise;
import jr.utils.Point;
import lombok.Getter;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Registered(id="entityItem")
public class EntityItem extends Entity {
    @Expose    @Getter private ItemStack itemStack;
    
    public EntityItem(Dungeon dungeon, Level level, Point position, ItemStack itemStack) {
        super(dungeon, level, position);
        
        this.itemStack = itemStack;
    }
    
    protected EntityItem() { super(); }
    
    public Item getItem() {
        return itemStack.getItem();
    }
    
    @Override
    public int getDepth() {
        return 2;
    }
    
    @Override
    public boolean isStatic() {
        return true;
    }
    
    @Override
    public EntityAppearance getAppearance() {
        return EntityAppearance.APPEARANCE_ITEM;
    }
    
    @Override
    public void update() {
        super.update();
        
        itemStack.getItem().update(this);
    }
    
    @EventHandler(selfOnly = true)
    public void onKick(EntityKickedEntityEvent e) {
        Point newPosition = getPosition().add(e.getDirection());
        
        if (getItem() instanceof Shatterable) {
            getDungeon().log(
                "%s shatters into a thousand pieces!",
                LanguageUtils.object(this).build(Capitalise.first)
            );
            
            if (getItem() instanceof ItemThermometer) {
                e.getKicker().addStatusEffect(new MercuryPoisoning());
            }
            
            remove();
            return;
        }
        
        TileType tile = getLevel().tileStore.getTileType(newPosition);
        
        if (tile == null || tile.getSolidity() == Solidity.SOLID) {
            getDungeon().log(
                "%s strikes the side of the wall.",
                LanguageUtils.object(this).build(Capitalise.first)
            );
            
            return;
        }
        
        setPosition(newPosition);
    }
    
    @Override
    public Noun getName(EntityLiving observer) {
        return itemStack.getName(observer);
    }
    
    @EventHandler(selfOnly = true)
    public void onSpawn(EntityAddedEvent event) {
        if (event.isNew()) {
            getDungeon().eventSystem.triggerEvent(new ItemDroppedEvent(this));
            
            getLevel().entityStore.getEntitiesAt(getPosition())
                .filter(e -> !e.equals(this))
                .forEach(e -> getDungeon().eventSystem.triggerEvent(new ItemDroppedOnEntityEvent(e, this)));
        }
    }
    
    @Override
    public boolean canBeWalkedOn() {
        return true;
    }
    
    @Override
    public ToStringBuilder toStringBuilder() {
        return super.toStringBuilder()
            .append("itemStack", itemStack.toStringBuilder());
    }
}
