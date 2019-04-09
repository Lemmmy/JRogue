package jr.rendering.ui.skin;

import jr.rendering.assets.Assets;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.text.WordUtils;

import static jr.rendering.assets.Fonts.fontFile;

@UISkinStyleHandler(priority = 450)
public class UIFonts extends UISkinStyle {
    public UIFonts(UISkin skin) {
        super(skin);
    }
    
    private void addFont(Assets assets, String fileName, String namePrefix) {
        for (FontSizes size : FontSizes.values()) {
            String baseName = namePrefix.isEmpty()
                              ? size.name().toLowerCase()
                              : WordUtils.capitalize(size.name().toLowerCase());
            
            assets.fonts.load(fontFile(fileName), size.size, true, f -> skin.add(baseName, f));
            assets.fonts.load(fontFile(fileName), size.size, false, f -> skin.add(baseName + "NoShadow", f));
        }
    }
    
    @Override
    public void onLoad(Assets assets) {
        addFont(assets, "PixelOperator", "");
    }
    
    @AllArgsConstructor
    @Getter
    public enum FontSizes {
        DEFAULT(16), LARGE(32);
        
        private int size;
    }
}
