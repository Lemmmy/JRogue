package jr.dungeon.wishes;

import jr.dungeon.Dungeon;
import jr.dungeon.entities.DamageSource;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.containers.EntityChest;
import jr.dungeon.entities.containers.EntityWeaponRack;
import jr.dungeon.entities.decoration.EntityCandlestick;
import jr.dungeon.entities.decoration.EntityFountain;
import jr.dungeon.entities.magic.EntityAltar;
import jr.dungeon.entities.monsters.canines.*;
import jr.dungeon.entities.monsters.critters.MonsterLizard;
import jr.dungeon.entities.monsters.critters.MonsterRat;
import jr.dungeon.entities.monsters.critters.MonsterSpider;
import jr.dungeon.entities.monsters.humanoids.MonsterSkeleton;
import jr.dungeon.entities.player.Player;
import jr.dungeon.items.Item;
import jr.dungeon.items.ItemStack;
import jr.dungeon.items.Material;
import jr.dungeon.items.comestibles.*;
import jr.dungeon.items.magical.ItemSpellbook;
import jr.dungeon.items.magical.spells.SpellLightOrb;
import jr.dungeon.items.projectiles.ItemArrow;
import jr.dungeon.items.quaffable.potions.BottleType;
import jr.dungeon.items.quaffable.potions.ItemPotion;
import jr.dungeon.items.quaffable.potions.PotionType;
import jr.dungeon.items.valuables.ItemThermometer;
import jr.dungeon.items.weapons.*;
import jr.dungeon.tiles.TileType;
import jr.utils.RandomUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Wishes {
	private static final Pattern wishDRoll = Pattern.compile("^roll (\\d+)?d(\\d+)(?:\\+(\\d+))?");
	private static final Pattern wishGold = Pattern.compile("^(\\d+) gold$");
	private static final Pattern wishGoldDropped = Pattern.compile("^drop(?:ed)? (\\d+) gold$");
	private static final Pattern wishSword = Pattern
		.compile("^(wood|stone|bronze|iron|steel|silver|gold|mithril|adamantite) (shortsword|longsword|dagger)$");

	private final Set<Pair<Pattern, Wish>> wishes = new HashSet<>();

	private static Wishes instance;

	public static Wishes get() {
		if (instance == null) {
			instance = new Wishes();
		}

		return instance;
	}

	private Wishes() {
		// Basic wishes
		registerWish("death", (d, p, a) -> p.kill(DamageSource.WISH_FOR_DEATH, 0, null, false));
		registerWish("kill\\s+all", (d, p, a) -> d.getLevel().getEntityStore().getEntities().stream()
													.filter(e -> e instanceof EntityLiving && !(e instanceof Player))
													.map(e -> (EntityLiving) e)
													.forEach(e -> e.kill(DamageSource.WISH_FOR_DEATH, 0, null, false)));
		registerWish("nutrition", (d, p, a) -> p.setNutrition(1000));
		registerWish("downstairs", (d, p, a) -> Arrays.stream(p.getLevel().getTileStore().getTiles())
													.filter(t -> t.getType() == TileType.TILE_ROOM_STAIRS_DOWN)
													.findFirst()
													.ifPresent(t -> p.teleport(t.getX(), t.getY())));
		registerWish("godmode", (d, p, a) -> p.godmode());
		registerWish("chest", new WishSpawn<>(EntityChest.class));
		registerWish("fountain", new WishSpawn<>(EntityFountain.class));
		registerWish("candlestick", new WishSpawn<>(EntityCandlestick.class));
		registerWish("weapon rack", new WishSpawn<>(EntityWeaponRack.class));
		registerWish("altar", new WishSpawn<>(EntityAltar.class));
		registerWish("rug", new WishTile(TileType.TILE_ROOM_RUG));
		registerWish("dirt", new WishTile(TileType.TILE_ROOM_DIRT));

		// Monsters
		registerWish("jackal", new WishSpawn<>(MonsterJackal.class));
		registerWish("fox", new WishSpawn<>(MonsterFox.class));
		registerWish("lizard", new WishSpawn<>(MonsterLizard.class));
		registerWish("hound", new WishSpawn<>(MonsterHound.class));
		registerWish("hellhound", new WishSpawn<>(MonsterHellhound.class));
		registerWish("icehound", new WishSpawn<>(MonsterIcehound.class));
		registerWish("spider", new WishSpawn<>(MonsterSpider.class));
		registerWish("rat", new WishSpawn<>(MonsterRat.class));
		registerWish("skeleton", new WishSpawn<>(MonsterSkeleton.class));
	}

	public void registerWish(Pattern pattern, Wish wish) {
		wishes.add(Pair.of(pattern, wish));
	}

	public void registerWish(String pattern, Wish wish) {
		registerWish(Pattern.compile("^" + pattern + "$"), wish);
	}

	public boolean makeWish(Dungeon dungeon, String wish) {
		final Player player = dungeon.getPlayer();

		if (player == null || !player.isDebugger()) {
			dungeon.redYou("can't do that.");
			return false;
		}

		Optional<Pair<Matcher, Wish>> optP = wishes.stream()
			.map(p -> Pair.of(p.getKey().matcher(wish), p.getValue()))
			.filter(p -> p.getKey().matches())
			.findFirst();

		if (optP.isPresent()) {
			Pair<Matcher, Wish> p = optP.get();
			Matcher m = p.getKey();
			Wish w = p.getValue();

			int gc = m.groupCount();
			String[] args = new String[gc];

			for (int i = 1; i < gc + 1; ++i) {
				args[i - 1] = m.group(i);
			}

			w.grant(dungeon, player, args);

			return true;
		} else {
			dungeon.logRandom(
				"[RED]You have extraordinary needs.",
				"[RED]You speak in mysteries.",
				"[RED]You speak in riddles.",
				"[RED]You are undecipherable."
			);

			return false;
		}
	}

	private static boolean wishItems(Dungeon dungeon, Player player, String wish) {
		Matcher wishSwordMatcher = wishSword.matcher(wish);
		
		if (wishSwordMatcher.find()) {
			Material material = Material.valueOf(wishSwordMatcher.group(1).toUpperCase());
			String type = wishSwordMatcher.group(2);
			
			Item item = null;
			
			if (type.equalsIgnoreCase("shortsword")) {
				item = new ItemShortsword(material);
			} else if (type.equalsIgnoreCase("longsword")) {
				item = new ItemLongsword(material);
			} else if (type.equalsIgnoreCase("dagger")) {
				item = new ItemDagger(material);
			}
			
			if (item != null && player.getContainer().isPresent()) {
				player.getContainer().get().add(new ItemStack(item));
				
				return true;
			}
		}
		
		Item item = null;
		
		if (wish.equalsIgnoreCase("bread")) {
			item = new ItemBread();
		} else if (wish.equalsIgnoreCase("apple")) {
			item = new ItemApple();
		} else if (wish.equalsIgnoreCase("orange")) {
			item = new ItemOrange();
		} else if (wish.equalsIgnoreCase("lemon")) {
			item = new ItemLemon();
		} else if (wish.equalsIgnoreCase("banana")) {
			item = new ItemBanana();
		} else if (wish.equalsIgnoreCase("carrot")) {
			item = new ItemCarrot();
		} else if (wish.equalsIgnoreCase("cherries")) {
			item = new ItemCherries();
		} else if (wish.equalsIgnoreCase("corn")) {
			item = new ItemCorn();
		} else if (wish.equalsIgnoreCase("staff")) {
			item = new ItemStaff();
		} else if (wish.equalsIgnoreCase("potion")) {
			BottleType bottle = RandomUtils.randomFrom(BottleType.values());
			PotionType potionType = RandomUtils.randomFrom(PotionType.values());
			float potency = RandomUtils.randomFloat(6f);
			
			ItemPotion potion = new ItemPotion();
			potion.setBottleType(bottle);
			potion.setPotionType(potionType);
			potion.setEmpty(false);
			potion.setPotency(potency);
			item = potion;
		} else if (wish.equalsIgnoreCase("spellbook")) {
			item = new ItemSpellbook();
			((ItemSpellbook) item).setSpell(new SpellLightOrb());
		}  else if (wish.equalsIgnoreCase("bow")) {
			item = new ItemBow();
		} else if (wish.equalsIgnoreCase("arrow")) {
			item = new ItemArrow();
		} else if (wish.equalsIgnoreCase("thermometer")) {
			item = new ItemThermometer();
		}
		
		if (item != null && player.getContainer().isPresent()) {
			player.getContainer().get().add(new ItemStack(item));
			
			return true;
		}
		
		return false;
	}
}
