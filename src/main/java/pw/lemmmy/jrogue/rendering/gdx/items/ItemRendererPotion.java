package pw.lemmmy.jrogue.rendering.gdx.items;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.items.Item;
import pw.lemmmy.jrogue.dungeon.items.ItemStack;
import pw.lemmmy.jrogue.rendering.gdx.utils.ImageLoader;

public class ItemRendererPotion extends ItemRendererBasic {
    private final TextureRegion fluidTex;

    public ItemRendererPotion(int sheetX, int sheetY, int liquidX, int liquidY) {
        super("items.png", sheetX, sheetY);
        fluidTex = ImageLoader.getImageFromSheet("items.png", liquidX, liquidY);
    }

    @Override
    public void draw(SpriteBatch batch, Dungeon dungeon, ItemStack itemStack, Item item, int x, int y) {
        batch.draw(fluidTex, x, y);
        super.draw(batch, dungeon, itemStack, item, x, y);
    }
}
