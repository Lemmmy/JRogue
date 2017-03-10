package jr.dungeon.wishes;

import jr.dungeon.Dungeon;
import jr.dungeon.entities.DamageSource;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.containers.EntityChest;
import jr.dungeon.entities.containers.EntityWeaponRack;
import jr.dungeon.entities.decoration.EntityCandlestick;
import jr.dungeon.entities.decoration.EntityFountain;
import jr.dungeon.entities.effects.*;
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
	private static final String wishSword =
		"(wood|stone|bronze|iron|steel|silver|gold|mithril|adamantite) (shortsword|longsword|dagger)";

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
		registerWish("death", (d, p, a) -> p.kill(DamageSource.WISH_FOR_DEATH, 0, null));
		registerWish("kill\\s+all", (d, p, a) ->
			d.getLevel().getEntityStore().getEntities().stream()
				.filter(e -> e instanceof EntityLiving && !(e instanceof Player))
				.map(e -> (EntityLiving) e)
				.forEach(e -> e.kill(DamageSource.WISH_FOR_DEATH, 0, null)));
		registerWish("nutrition", (d, p, a) -> p.setNutrition(1000));
		registerWish("health", (d, p, a) -> p.setHealth(p.getMaxHealth()));
		registerWish("(?:ds|downstairs)", (d, p, a) ->
			Arrays.stream(p.getLevel().getTileStore().getTiles())
				.filter(t -> t.getType() == TileType.TILE_ROOM_STAIRS_DOWN)
				.findFirst()
				.ifPresent(t -> {
					p.defaultVisitors.teleport(t.getX(), t.getY());
					p.defaultVisitors.climbDown();
				}));
		registerWish("godmode", (d, p, a) -> p.setGodmode(true));
		registerWish("chest", new WishSpawn<>(EntityChest.class));
		registerWish("fountain", new WishSpawn<>(EntityFountain.class));
		registerWish("candlestick", new WishSpawn<>(EntityCandlestick.class));
		registerWish("weapon rack", new WishSpawn<>(EntityWeaponRack.class));
		registerWish("altar", new WishSpawn<>(EntityAltar.class));

		// Tiles
		registerWish("rug", new WishTile(TileType.TILE_ROOM_RUG));
		registerWish("dirt", new WishTile(TileType.TILE_ROOM_DIRT));
		registerWish("water", new WishTile(TileType.TILE_ROOM_WATER));
		registerWish("ice", new WishTile(TileType.TILE_ROOM_ICE));


		// Status effects
		// NOTE: Please add a new wish here for any status effects you implement.
		registerWish("paralysis", new WishEffect(Paralysis.class));
		registerWish("food poisoning", new WishEffect(FoodPoisoning.class));
		registerWish("injured foot", new WishEffect(InjuredFoot.class));
		registerWish("mercury poisoning", new WishEffect(MercuryPoisoning.class));
		registerWish("strained leg", new WishEffect(StrainedLeg.class));
		registerWish("fire", new WishEffect(Ablaze.class));

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

		// Items
		registerWish(wishSword, (d, p, a) -> {
			if (a.length < 2) return;

			Material m = Material.valueOf(a[0].toUpperCase());
			String type = a[1];

			Item item = null;

			switch (type.toLowerCase()) {
				case "shortsword": item = new ItemShortsword(m); break;
				case "longsword": item = new ItemLongsword(m); break;
				case "dagger": item = new ItemDagger(m); break;
				default: break;
			}

			if (item != null && p.getContainer().isPresent()) {
				p.getContainer().get().add(new ItemStack(item));
				d.turn();
			}
		});

		registerWish("potion", new WishItem<>(ItemPotion.class, p -> {
			BottleType bottle = RandomUtils.randomFrom(BottleType.values());
			PotionType potionType = RandomUtils.randomFrom(PotionType.values());
			float potency = RandomUtils.randomFloat(6f);

			p.setBottleType(bottle);
			p.setPotionType(potionType);
			p.setEmpty(false);
			p.setPotency(potency);
		}));

		registerWish("bread", new WishItem<>(ItemBread.class));
		registerWish("apple", new WishItem<>(ItemApple.class));
		registerWish("orange", new WishItem<>(ItemOrange.class));
		registerWish("lemon", new WishItem<>(ItemLemon.class));
		registerWish("banana", new WishItem<>(ItemBanana.class));
		registerWish("carrot", new WishItem<>(ItemCarrot.class));
		registerWish("cherries", new WishItem<>(ItemCherries.class));
		registerWish("corn", new WishItem<>(ItemCorn.class));
		registerWish("staff", new WishItem<>(ItemStaff.class));
		registerWish("spellbook", new WishItem<>(ItemSpellbook.class, s -> s.setSpell(new SpellLightOrb())));
		registerWish("bow", new WishItem<>(ItemBow.class));
		registerWish("arrow", new WishItem<>(ItemArrow.class));
		registerWish("thermometer", new WishItem<>(ItemThermometer.class));
	}

	public void registerWish(Pattern pattern, Wish wish) {
		wishes.add(Pair.of(pattern, wish));
	}

	public void registerWish(String pattern, Wish wish) {
		registerWish(Pattern.compile("^" + pattern + "$"), wish);
	}

	public boolean makeWish(Dungeon dungeon, String _wish) {
		final String wish = _wish.toLowerCase();
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
}
