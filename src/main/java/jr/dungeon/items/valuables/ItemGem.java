package jr.dungeon.items.valuables;

import com.google.gson.annotations.Expose;
import jr.dungeon.Level;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.items.Item;
import jr.dungeon.items.ItemAppearance;
import jr.dungeon.items.ItemCategory;
import jr.dungeon.items.identity.AspectGemValue;
import jr.language.Lexicon;
import jr.language.Noun;
import jr.language.transformers.TransformerType;
import jr.utils.RandomUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.json.JSONObject;

public class ItemGem extends Item {
	@Expose private Gem gem;
	@Expose private boolean worthless = false;
	
	public ItemGem() { // unserialisation constructor
		super();
		
		addAspect(new AspectGemValue());
	}
	
	public ItemGem(Level level) { // chest spawning constructor
		this.gem = RandomUtils.randomFrom(Gem.values());
		this.worthless = RandomUtils.rollD2();
		
		addAspect(new AspectGemValue());
	}
	
	public ItemGem(Gem gem, boolean worthless) {
		this.gem = gem;
		this.worthless = worthless;
		
		addAspect(new AspectGemValue());
	}
	
	@Override
	public Noun getName(EntityLiving observer) {
		String colour = gem.getAppearance().name().replace("APPEARANCE_GEM_", "").toLowerCase();
		
		if (isAspectKnown(observer, AspectGemValue.class)) {
			if (worthless) {
				return Lexicon.worthlessGem.clone()
					.addInstanceTransformer(ColourTransformer.class, (s, m) -> s.replace("glass", colour + " glass"));
			} else {
				return gem.getName().clone();
			}
		} else {
			return Lexicon.gem.clone()
				.addInstanceTransformer(ColourTransformer.class, (s, m) -> colour + " " + s);
		}
	}
	
	@Override
	public float getWeight() {
		return 1;
	}
	
	@Override
	public ItemAppearance getAppearance() {
		return gem.getAppearance();
	}
	
	@Override
	public ItemCategory getCategory() {
		return ItemCategory.GEM;
	}
	
	public Gem getGem() {
		return gem;
	}
	
	public boolean isWorthless() {
		return worthless;
	}
	
	@Override
	public void serialise(JSONObject obj) {
		super.serialise(obj);
		
		obj.put("gem", gem.name());
		obj.put("worthless", worthless);
	}
	
	@Override
	public void unserialise(JSONObject obj) {
		super.unserialise(obj);
		
		worthless = obj.getBoolean("worthless");
		gem = Gem.valueOf(obj.getString("gem"));
	}
	
	@Getter
	@AllArgsConstructor
	public enum Gem {
		// TODO: Gem values
		
		RED_GARNET(ItemAppearance.APPEARANCE_GEM_RED, Lexicon.redGarnet),
		RUBY(ItemAppearance.APPEARANCE_GEM_RED, Lexicon.ruby),
		
		GOLDEN_SAPPHIRE(ItemAppearance.APPEARANCE_GEM_ORANGE, Lexicon.goldenSapphire),
		TOPAZ(ItemAppearance.APPEARANCE_GEM_ORANGE, Lexicon.topaz),
		
		CITRINE(ItemAppearance.APPEARANCE_GEM_YELLOW, Lexicon.citrine),
		YELLOW_DIAMOND(ItemAppearance.APPEARANCE_GEM_YELLOW, Lexicon.yellowDiamond),
		YELLOW_TOPAZ(ItemAppearance.APPEARANCE_GEM_YELLOW, Lexicon.yellowTopaz),
		
		GREEN_AMETHYST(ItemAppearance.APPEARANCE_GEM_LIME, Lexicon.greenAmethyst),
		PERIDOT(ItemAppearance.APPEARANCE_GEM_LIME, Lexicon.peridot),
		
		EMERALD(ItemAppearance.APPEARANCE_GEM_GREEN, Lexicon.emerald),
		JADE(ItemAppearance.APPEARANCE_GEM_GREEN, Lexicon.jade),
		GREEN_GARNET(ItemAppearance.APPEARANCE_GEM_GREEN, Lexicon.greenGarnet),
		
		APATITE(ItemAppearance.APPEARANCE_GEM_CYAN, Lexicon.apatite),
		AQUAMARINE(ItemAppearance.APPEARANCE_GEM_CYAN, Lexicon.aquamarine),
		BLUE_DIAMOND(ItemAppearance.APPEARANCE_GEM_CYAN, Lexicon.blackDiamond),
		SKY_BLUE_TOPAZ(ItemAppearance.APPEARANCE_GEM_CYAN, Lexicon.skyBlueTopaz),
		
		BLUE_APATITE(ItemAppearance.APPEARANCE_GEM_BLUE, Lexicon.blueApatite),
		LAPIS_LAZULI(ItemAppearance.APPEARANCE_GEM_BLUE, Lexicon.lapisLazuli),
		SAPPHIRE(ItemAppearance.APPEARANCE_GEM_BLUE, Lexicon.sapphire),
		
		ALEXANDRITE(ItemAppearance.APPEARANCE_GEM_PURPLE, Lexicon.alexandrite),
		AMETHYST(ItemAppearance.APPEARANCE_GEM_PURPLE, Lexicon.amethyst),
		TANZANITE(ItemAppearance.APPEARANCE_GEM_PURPLE, Lexicon.tanzanite),
		
		DIAMOND(ItemAppearance.APPEARANCE_GEM_WHITE, Lexicon.diamond),
		QUARTZ(ItemAppearance.APPEARANCE_GEM_WHITE, Lexicon.quartz),
		WHITE_TOPAZ(ItemAppearance.APPEARANCE_GEM_WHITE, Lexicon.topaz),
		WHITE_SAPPHIRE(ItemAppearance.APPEARANCE_GEM_WHITE, Lexicon.whiteSapphire),
		
		BLACK_DIAMOND(ItemAppearance.APPEARANCE_GEM_BLACK, Lexicon.blackDiamond),
		BLACK_ONYX(ItemAppearance.APPEARANCE_GEM_BLACK, Lexicon.blackOnyx),
		DIOPSIDE(ItemAppearance.APPEARANCE_GEM_BLACK, Lexicon.diopside),
		HEMATITE(ItemAppearance.APPEARANCE_GEM_BLACK, Lexicon.hematite),
		SPINEL(ItemAppearance.APPEARANCE_GEM_BLACK, Lexicon.spinel);
		
		private ItemAppearance appearance;
		private Noun name;
	}
	
	public class ColourTransformer implements TransformerType {}
}
