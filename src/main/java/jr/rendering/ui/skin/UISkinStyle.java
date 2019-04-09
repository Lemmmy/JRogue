package jr.rendering.ui.skin;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import jr.rendering.assets.Assets;
import jr.rendering.assets.UsesAssets;
import jr.rendering.ui.utils.TiledNinePatchDrawable;

import java.util.function.Consumer;

import static jr.rendering.assets.Textures.hudFile;

public abstract class UISkinStyle implements UsesAssets {
    protected UISkin skin;
    
    public UISkinStyle(UISkin skin) {
        this.skin = skin;
    }
    
    protected void loadNinePatch(Assets assets, String fileName, int left, int right, int top, int bottom, Consumer<NinePatchDrawable> consumer) {
        assets.textures.loadPacked(hudFile(fileName), t -> consumer.accept(new NinePatchDrawable(new NinePatch(
            t, left, right, top, bottom
        ))));
    }
    
    protected void loadNinePatch(Assets assets, String fileName, Consumer<NinePatchDrawable> consumer) {
        loadNinePatch(assets, fileName, 0, 0, 0, 0, consumer);
    }
    
    protected void loadTiledDrawable(Assets assets, String fileName, Consumer<TiledDrawable> consumer) {
        assets.textures.loadPacked(hudFile(fileName), t -> consumer.accept(new TiledDrawable(t)));
    }
    
    protected void loadTiledNinePatch(Assets assets, String fileName, int left, int right, int top, int bottom, Consumer<TiledNinePatchDrawable> consumer) {
        assets.textures.loadPacked(hudFile(fileName), t -> consumer.accept(new TiledNinePatchDrawable(
            t, left, right, top, bottom
        )));
    }
    
    public void loadTextureRegion(Assets assets, String fileName, Consumer<TextureRegionDrawable> consumer) {
        assets.textures.loadPacked(hudFile(fileName), t -> consumer.accept(new TextureRegionDrawable(t)));
    }
}
