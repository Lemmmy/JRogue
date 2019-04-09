package jr.rendering.ui.skin;

import jr.rendering.assets.Assets;

@UISkinStyleHandler
public class UIStatsIcons extends UIIconStyle {
    public UIStatsIcons(UISkin skin) {
        super(skin);
    }
    
    @Override
    public void onLoad(Assets assets) {
        super.onLoad(assets);
        addIcon(assets, "armour", "armourIcon");
        addIcon(assets, "brightness_bright", "brightnessBrightIcon");
        addIcon(assets, "brightness_dark", "brightnessDarkIcon");
        addIcon(assets, "dice", "diceIcon");
        addIcon(assets, "d20", "d20Icon");
        addIcon(assets, "depth", "depthIcon");
        addIcon(assets, "energy", "energyIcon");
        addIcon(assets, "experience_level", "experienceLevelIcon");
        addIcon(assets, "gold", "goldIcon");
        addIcon(assets, "gold_bag", "goldBagIcon");
        addIcon(assets, "health", "healthIcon");
        addIcon(assets, "nutrition", "nutritionIcon");
        addIcon(assets, "target", "targetIcon");
    }
}
