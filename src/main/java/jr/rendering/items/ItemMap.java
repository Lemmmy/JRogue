package jr.rendering.items;

import jr.dungeon.items.ItemAppearance;
import jr.rendering.assets.RegisterAssetManager;
import jr.rendering.assets.UsesAssets;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

@RegisterAssetManager
public enum ItemMap {
    APPEARANCE_CORPSE("corpse"),
    
    APPEARANCE_BREAD("bread"),
    APPEARANCE_APPLE("apple"),
    APPEARANCE_ORANGE("orange"),
    APPEARANCE_LEMON("lemon"),
    APPEARANCE_BANANA("banana"),
    APPEARANCE_CARROT("carrot"),
    APPEARANCE_MELON_SLICE("melon_slice"),
    APPEARANCE_MELON("melon"),
    APPEARANCE_WATERMELON_SLICE("watermelon_slice"),
    APPEARANCE_WATERMELON("watermelon"),
    APPEARANCE_CHERRIES("cherries"),
    APPEARANCE_EGGS("egg"),
    APPEARANCE_CORN("corn"),
    APPEARANCE_POTATO_RAW("potato"),
    
    APPEARANCE_STAFF("staff"),
    APPEARANCE_DAGGER(new ItemRendererSword("dagger")),
    APPEARANCE_SHORTSWORD(new ItemRendererSword("shortsword")),
    APPEARANCE_LONGSWORD(new ItemRendererSword("longsword")),
    
    APPEARANCE_BOW("bow"),
    APPEARANCE_ARROW("arrow"),
    
    APPEARANCE_GOLD(new ItemRendererGold()),
    
    APPEARANCE_GEM_WHITE("gem_white"),
    APPEARANCE_GEM_RED("gem_red"),
    APPEARANCE_GEM_ORANGE("gem_orange"),
    APPEARANCE_GEM_YELLOW("gem_yellow"),
    APPEARANCE_GEM_LIME("gem_lime"),
    APPEARANCE_GEM_GREEN("gem_green"),
    APPEARANCE_GEM_CYAN("gem_cyan"),
    APPEARANCE_GEM_BLUE("gem_blue"),
    APPEARANCE_GEM_PURPLE("gem_purple"),
    APPEARANCE_GEM_PINK("gem_pink"),
    APPEARANCE_GEM_BLACK("gem_black"),
    
    APPEARANCE_SPELLBOOK(new ItemRendererRandom("spellbooks", 9)),
    
    APPEARANCE_POTION_EMPTY("potion"),
    APPEARANCE_POTION_LABEL_EMPTY("potion_label"),
    APPEARANCE_POTION_CORK_EMPTY("potion_cork"),
    APPEARANCE_POTION_CORK_LABEL_EMPTY("potion_cork_label"),
    APPEARANCE_POTION_FAT_EMPTY("potion_fat"),
    
    APPEARANCE_POTION(new ItemRendererPotion("potion", "potion_liquid")),
    APPEARANCE_POTION_LABEL(new ItemRendererPotion("potion_label", "potion_liquid")),
    APPEARANCE_POTION_CORK(new ItemRendererPotion("potion_cork", "potion_liquid")),
    APPEARANCE_POTION_CORK_LABEL(new ItemRendererPotion("potion_cork_label", "potion_liquid")),
    APPEARANCE_POTION_FAT(new ItemRendererPotion("potion_fat", "potion_fat_liquid")),
    
    APPEARANCE_THERMOMETER("thermometer");
    
    public static final int ITEM_WIDTH = 16;
    public static final int ITEM_HEIGHT = 16;
    
    @Getter private ItemRenderer renderer;
    
    ItemMap(ItemRenderer renderer) {
        this.renderer = renderer;
    }
    
    ItemMap(String fileName) {
        this.renderer = new ItemRendererBasic(fileName);
    }
    
    public ItemAppearance getAppearance() {
        return ItemAppearance.valueOf(name());
    }
    
    public static Collection<? extends UsesAssets> getAssets() {
        return Arrays.stream(values()).map(ItemMap::getRenderer).collect(Collectors.toList());
    }
}
