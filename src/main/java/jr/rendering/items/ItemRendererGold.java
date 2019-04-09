package jr.rendering.items;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import jr.dungeon.Dungeon;
import jr.dungeon.items.Item;
import jr.dungeon.items.ItemStack;
import jr.rendering.assets.Assets;
import jr.rendering.utils.ImageUtils;

import static jr.rendering.assets.Textures.itemFile;

public class ItemRendererGold extends ItemRenderer {
    private int[] values = new int[]{1, 2, 5, 10, 20, 30, 50, 100, 200, 300};
    
    private TextureRegion[] images = new TextureRegion[values.length];
    
    @Override
    public void onLoad(Assets assets) {
        super.onLoad(assets);
        
        assets.textures.loadPacked(itemFile("gold"), t -> ImageUtils.loadSheet(t, images, values.length, 1));
    }
    
    @Override
    public TextureRegion getTextureRegion(Dungeon dungeon, ItemStack itemStack, Item item, boolean reflect) {
        return getImageFromAmount(itemStack.getCount());
    }
    
    @Override
    public void draw(SpriteBatch batch, Dungeon dungeon, ItemStack itemStack, Item item, int x, int y, boolean reflect) {
        drawItem(batch, getTextureRegion(dungeon, itemStack, item, reflect), x, y, reflect);
    }
    
    private TextureRegion getImageFromAmount(int count) {
        int value = 1;
        
        for (int i = 0; i < values.length; i++) {
            if (count >= values[i]) {
                value = i;
            } else {
                break;
            }
        }
        
        return images[value];
    }
    
    @Override
    public Drawable getDrawable(ItemStack itemStack, Item item) {
        return new TextureRegionDrawable(getImageFromAmount(itemStack.getCount()));
    }
}
