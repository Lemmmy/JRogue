package pw.lemmmy.jrogue.rendering.gdx.items;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.items.Item;
import pw.lemmmy.jrogue.dungeon.items.ItemStack;
import pw.lemmmy.jrogue.rendering.gdx.utils.ImageLoader;
import pw.lemmmy.jrogue.rendering.gdx.utils.CompositeDrawable;

public class ItemRendererPotion extends ItemRenderer {
    private final TextureRegion fluidTex;
    private final TextureRegion bottleTex;
    private final CompositeDrawable potionDrawable;

    public ItemRendererPotion(int sheetX, int sheetY, int liquidX, int liquidY) {
        bottleTex = ImageLoader.getImageFromSheet("items.png", sheetX, sheetY);
        fluidTex = ImageLoader.getImageFromSheet("items.png", liquidX, liquidY);

        potionDrawable = new CompositeDrawable(
            ItemMap.ITEM_WIDTH, ItemMap.ITEM_HEIGHT,
            new TextureRegionDrawable(ImageLoader
                    .getImageFromSheet("items.png", liquidX, liquidY, ItemMap.ITEM_WIDTH, ItemMap.ITEM_HEIGHT, false)),
            new TextureRegionDrawable(ImageLoader
                    .getImageFromSheet("items.png", sheetX, sheetY, ItemMap.ITEM_WIDTH, ItemMap.ITEM_HEIGHT, false))
        );
    }

    @Override
    public void draw(SpriteBatch batch, Dungeon dungeon, ItemStack itemStack, Item item, int x, int y) {
        batch.draw(fluidTex, x, y);
        batch.draw(bottleTex, x, y);
    }

    @Override
    public Drawable getDrawable(ItemStack itemStack, Item item) {
        return potionDrawable;
    }
}
