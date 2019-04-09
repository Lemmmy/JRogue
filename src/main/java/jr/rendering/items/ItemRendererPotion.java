package jr.rendering.items;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import jr.dungeon.Dungeon;
import jr.dungeon.items.Item;
import jr.dungeon.items.ItemStack;
import jr.dungeon.items.quaffable.potions.ItemPotion;
import jr.rendering.assets.Assets;
import jr.rendering.utils.CompositeDrawable;

import static jr.rendering.assets.Textures.itemFile;

public class ItemRendererPotion extends ItemRenderer {
    private final String fluidFileName, bottleFileName;
    private TextureRegion fluidTex, bottleTex;
    private TextureRegionDrawable fluidDrawable, bottleDrawable;
    
    private Color oldColour = new Color();
    
    public ItemRendererPotion(String fluidFileName, String bottleFileName) {
        this.fluidFileName = fluidFileName;
        this.bottleFileName = bottleFileName;
    }
    
    @Override
    public void onLoad(Assets assets) {
        super.onLoad(assets);
        
        assets.textures.loadPacked(itemFile(fluidFileName), t -> {
            fluidTex = t; fluidDrawable = new TextureRegionDrawable(t);
        });
        
        assets.textures.loadPacked(itemFile(bottleFileName), t -> {
            bottleTex = t; bottleDrawable = new TextureRegionDrawable(t);
        });
    }
    
    @Override
    public TextureRegion getTextureRegion(Dungeon dungeon, ItemStack itemStack, Item item, boolean reflect) {
        return bottleTex;
    }
    
    @Override
    public void draw(SpriteBatch batch, Dungeon dungeon, ItemStack itemStack, Item item, int x, int y, boolean reflect) {
        if (item instanceof ItemPotion) {
            ItemPotion potion = (ItemPotion) item;
            oldColour.set(batch.getColor());
            batch.setColor(PotionColourMap.fromPotion(potion));
            drawItem(batch, fluidTex, x, y, reflect);
            batch.setColor(oldColour);
        }
        
        drawItem(batch, bottleTex, x, y, reflect);
    }
    
    @Override
    public Drawable getDrawable(ItemStack itemStack, Item item) {
        if (item instanceof ItemPotion) {
            ItemPotion potion = (ItemPotion) item;
            return new CompositeDrawable(
                ItemMap.ITEM_WIDTH, ItemMap.ITEM_HEIGHT,
                fluidDrawable.tint(PotionColourMap.fromPotion(potion)), bottleDrawable
            );
        }
        
        return bottleDrawable;
    }
}
