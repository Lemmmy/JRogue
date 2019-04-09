package jr.rendering.assets;

import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader.FreeTypeFontLoaderParameter;
import jr.JRogue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Fonts extends AssetHandler<BitmapFont, FreeTypeFontLoaderParameter> {
    private Map<String, Set<FontCallback>> callbacks = new HashMap<>();
    
    public Fonts(Assets assets) {
        super(assets);
        
        FileHandleResolver resolver = new InternalFileHandleResolver();
        assets.manager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        assets.manager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));
    }
    
    public static String fontFile(String fileName) {
        return fileName + ".ttf";
    }
    
    @Override
    public void onLoaded() {
        super.onLoaded();
        
        callbacks.forEach((fileName, callbackSet) -> {
            BitmapFont font = prepareFont(assets.manager.get(fileName));
            callbackSet.forEach(c -> c.onLoad(font));
        });
        callbacks.clear();
    }
    
    @Override
    protected Class<BitmapFont> getAssetClass() {
        return BitmapFont.class;
    }
    
    @Override
    public String getFileNamePrefix() {
        return "fonts/";
    }
    
    @Override
    public void load(String rawFileName) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void load(String rawFileName, AssetCallback<BitmapFont> callback) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public BitmapFont loadImmediately(String rawFileName) {
        throw new UnsupportedOperationException();
    }
    
    private BitmapFont prepareFont(BitmapFont font) {
        font.getData().markupEnabled = true;
        return font;
    }
    
    public void load(String rawFileName, int fontSize, boolean shadow, FontCallback callback) {
        String fileName = getPrefixedFileName(rawFileName)
            .replaceAll("\\.ttf$", "")
            + "_" + fontSize + (shadow ? "_shadow" : "") + ".ttf";
        
        if (assets.manager.isLoaded(fileName)) {
            callback.onLoad(prepareFont(assets.manager.get(fileName)));
            return;
        }
        
        JRogue.getLogger().debug("Loading font {} ({}px, shadow: {})", fileName, fontSize, shadow);
        
        FreeTypeFontLoaderParameter font = new FreeTypeFontLoaderParameter();
        font.fontFileName = getPrefixedFileName(rawFileName);
        font.fontParameters.size = fontSize;
        
        if (shadow) {
            font.fontParameters.shadowColor = new Color(0.0f, 0.0f, 0.0f, 0.75f);
            font.fontParameters.shadowOffsetX = fontSize / 16;
            font.fontParameters.shadowOffsetY = fontSize / 16;
        }
        
        assets.manager.load(fileName, getAssetClass(), font);
        
        if (assets.manager.isLoaded(fileName)) {
            callback.onLoad(prepareFont(assets.manager.get(fileName)));
        } else {
            if (!callbacks.containsKey(fileName))
                callbacks.put(fileName, new HashSet<>());
            callbacks.get(fileName).add(callback);
        }
    }
    
    @FunctionalInterface
    public interface FontCallback {
        void onLoad(BitmapFont font);
    }
}
