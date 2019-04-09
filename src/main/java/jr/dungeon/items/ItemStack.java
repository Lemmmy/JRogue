package jr.dungeon.items;

import com.google.gson.annotations.Expose;
import jr.dungeon.entities.EntityLiving;
import jr.language.Noun;
import jr.language.transformers.Plural;
import jr.utils.DebugToStringStyle;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Getter
public class ItemStack {
    @Expose private Item item;
    @Expose @Setter private int count;
    
    public ItemStack(Item item) {
        this(item, 1);
    }
    
    public ItemStack(Item item, int count) {
        this.item = item;
        this.count = count;
    }
    
    public Noun getName(EntityLiving observer) {
        return Plural.addCount(item.getName(observer), count);
    }
    
    public ItemAppearance getAppearance() {
        return item.getAppearance();
    }
    
    public ItemCategory getCategory() {
        return item.getCategory();
    }
    
    public void addCount(int count) {
        this.count += count;
    }
    
    public void subtractCount(int i) {
        count = Math.max(0, count - i);
    }
    
    public float getWeight() {
        return item.getWeight() * count;
    }
    
    @Override
    public String toString() {
        return toStringBuilder().toString();
    }
    
    public ToStringBuilder toStringBuilder() {
        return new ToStringBuilder(this, DebugToStringStyle.STYLE)
            .append("item", item.toStringBuilder())
            .append("count", count);
    }
}