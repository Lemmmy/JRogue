package jr.dungeon.wishes;

import jr.JRogue;
import jr.dungeon.Dungeon;
import jr.dungeon.entities.DamageSource;
import jr.dungeon.entities.DamageType;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.actions.ActionTeleport;
import jr.dungeon.entities.containers.EntityChest;
import jr.dungeon.entities.containers.EntityWeaponRack;
import jr.dungeon.entities.decoration.EntityCandlestick;
import jr.dungeon.entities.decoration.EntityFountain;
import jr.dungeon.entities.effects.*;
import jr.dungeon.entities.magic.EntityAltar;
import jr.dungeon.entities.player.Player;
import jr.dungeon.items.Item;
import jr.dungeon.items.ItemStack;
import jr.dungeon.items.Material;
import jr.dungeon.items.identity.AspectBeatitude;
import jr.dungeon.items.quaffable.potions.BottleType;
import jr.dungeon.items.quaffable.potions.ItemPotion;
import jr.dungeon.items.quaffable.potions.PotionType;
import jr.dungeon.items.weapons.ItemDagger;
import jr.dungeon.items.weapons.ItemLongsword;
import jr.dungeon.items.weapons.ItemShortsword;
import jr.dungeon.tiles.TileType;
import jr.utils.Profiler;
import jr.utils.RandomUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
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
		findWishes();
		
		// Basic wishes
		registerWish("death", (d, p, a) -> p.kill(new DamageSource(null, null, DamageType.WISH_FOR_DEATH), 0));
		registerWish("kill\\s+all", (d, p, a) ->
			d.getLevel().entityStore.getEntities().stream()
				.filter(e -> e instanceof EntityLiving && !(e instanceof Player))
				.map(e -> (EntityLiving) e)
				.forEach(e -> e.kill(new DamageSource(null, null, DamageType.WISH_FOR_DEATH), 0)));
		registerWish("nutrition", (d, p, a) -> p.setNutrition(1000));
		registerWish("health", (d, p, a) -> p.setHealth(p.getMaxHealth()));
		registerWish("energy", (d, p, a) -> p.setEnergy(p.getMaxEnergy()));
		registerWish("(?:discover|see all)", (d, p, a) -> {
			p.getLevel().visibilityStore.seeAll();
			p.getLevel().lightStore.buildLight(false);
		});
		registerWish("(?:us|upstairs)", (d, p, a) ->
			Arrays.stream(p.getLevel().tileStore.getTiles())
				.filter(t -> t.getType() == TileType.TILE_ROOM_STAIRS_UP)
				.findFirst().ifPresent(t -> {
					p.setPosition(t.position);
					p.defaultVisitors.climbDown();
					d.greenYou("traverse to [CYAN]%s[].", d.getLevel());
				}));
		registerWish("(?:ds|downstairs)", (d, p, a) ->
			Arrays.stream(p.getLevel().tileStore.getTiles())
				.filter(t -> t.getType() == TileType.TILE_ROOM_STAIRS_DOWN)
				.findFirst().ifPresent(t -> {
					p.setPosition(t.position);
					p.defaultVisitors.climbDown();
					d.greenYou("traverse to [CYAN]%s[].", d.getLevel());
				}));
		registerWish("explore", new WishExplore());
		registerWish("find chest", (d, p, a) -> {
			AtomicBoolean found = new AtomicBoolean(false);
			
			for (int i = 0; i < 40; i++) {
				d.wish("ds");
				
				p.getLevel().entityStore.getEntities().stream()
					.filter(EntityChest.class::isInstance)
					.findFirst().ifPresent(e -> {
						p.defaultVisitors.teleport(e.getPosition());
						found.set(true);
					});
				
				if (found.get()) break;
			}
		});
		registerWish("godmode", (d, p, a) -> p.setGodmode(true));
		registerWish("chest", new WishSpawn<>(EntityChest.class));
		registerWish("fountain", new WishSpawn<>(EntityFountain.class));
		registerWish("candlestick", new WishSpawn<>(EntityCandlestick.class));
		registerWish("weapon rack", new WishSpawn<>(EntityWeaponRack.class));
		registerWish("altar", new WishSpawn<>(EntityAltar.class));
		registerWish("fill inventory", (d, p, a) -> {
			for (int i = 0; i < 40; i++) {
				makeWish(d, "bread");
			}
		});
		registerWish("fill log", (d, p, a) -> {
			for (int i = 0; i < 200; i++) {
				d.log("Message " + i);
			}
		});
		registerWish("identify all", (d, p, a) -> p.getContainer().ifPresent(c -> c.getItems().forEach((ch, i) -> {
			i.getItem().getAspects().forEach((aspectID, aspect) -> i.getItem().observeAspect(p, aspectID));
			i.getItem().getPersistentAspects().forEach(aspect -> p.observeAspect(i.getItem(), aspect.getClass()));
		})));
		registerWish("bless (.)", (d, p, a) -> {
			d.log("Blessing [YELLOW]%s[].", a[0].charAt(0));
			
			p.getContainer().ifPresent(c -> c.get(a[0].charAt(0)).ifPresent(i -> {
				i.getItem().getAspect(AspectBeatitude.class).ifPresent(as ->
					((AspectBeatitude) as).setBeatitude(AspectBeatitude.Beatitude.BLESSED));
				i.getItem().observeAspect(p, AspectBeatitude.class);
			}));
		});
		registerWish("curse (.)", (d, p, a) -> {
			d.log("Cursing [YELLOW]%s[].", a[0].charAt(0));
			
			p.getContainer().ifPresent(c -> c.get(a[0].charAt(0)).ifPresent(i -> {
				i.getItem().getAspect(AspectBeatitude.class).ifPresent(as ->
					((AspectBeatitude) as).setBeatitude(AspectBeatitude.Beatitude.CURSED));
				i.getItem().observeAspect(p, AspectBeatitude.class);
			}));
		});
		registerWish("debug (.)", (d, p, a) -> p.getContainer().ifPresent(c -> c.get(a[0].charAt(0)).ifPresent(i -> {
			d.log(i.getItem().toString());
		})));
		registerWish("summon familiar", (d, p, a) -> p.getFamiliar().ifPresent(p.getLevel(), f -> {
			f.setAction(new ActionTeleport(p.getPosition(), null));
			d.turnSystem.turn();
		}));
		
		// Tiles
		registerWish("rug", new WishTile(TileType.TILE_ROOM_RUG));
		registerWish("dirt", new WishTile(TileType.TILE_ROOM_DIRT));
		registerWish("water", new WishTile(TileType.TILE_ROOM_WATER));
		registerWish("puddle", new WishTile(TileType.TILE_ROOM_PUDDLE));
		registerWish("ice", new WishTile(TileType.TILE_ROOM_ICE));
		registerWish("trap", new WishTile(TileType.TILE_TRAP));

		// Status effects
		// NOTE: Please add a new wish here for any status effects you implement.
		registerWish("paralysis", new WishEffect(Paralysis.class));
		registerWish("food poisoning", new WishEffect(FoodPoisoning.class));
		registerWish("injured foot", new WishEffect(InjuredFoot.class));
		registerWish("mercury poisoning", new WishEffect(MercuryPoisoning.class));
		registerWish("strained leg", new WishEffect(StrainedLeg.class));
		registerWish("fire", new WishEffect(Ablaze.class));

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
				d.turnSystem.turn();
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
	}

	public void registerWish(Pattern pattern, Wish wish) {
		wishes.add(Pair.of(pattern, wish));
	}

	public void registerWish(String pattern, Wish wish) {
		registerWish(Pattern.compile("^" + pattern + "$"), wish);
	}
	
	private void findWishes() {
		JRogue.getReflections().getTypesAnnotatedWith(Wishable.class).stream()
			.filter(clazz -> clazz.getAnnotation(Wishable.class) != null)
			.forEach(clazz -> {
				Wishable wishable = clazz.getAnnotation(Wishable.class);
				String name = wishable.name();
				
				if (Entity.class.isAssignableFrom(clazz)) {
					registerWish(name, new WishSpawn<>((Class<? extends Entity>) clazz));
				} else if (Item.class.isAssignableFrom(clazz)) {
					registerWish(name, new WishItem<>((Class<? extends Item>) clazz));
				}
			});
	}

	private boolean wish(Dungeon dungeon, String _wish) {
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

			for (int i = 1; i < gc + 1; ++i)
				args[i - 1] = m.group(i);

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

	public boolean makeWish(Dungeon dungeon, String wish) {
		String[] wishes = wish.split("\\s*;\\s*");

		long start = System.nanoTime();
		boolean result = Arrays.stream(wishes)
			.filter(w -> !w.isEmpty())
			.map(w -> wish(dungeon, w))
			.reduce(true, (a, b) -> a && b);
		long end = System.nanoTime();
		
		JRogue.getLogger().log(Profiler.LEVEL, String.format("Wish %s took %,d ms", wish, (end - start) / 1_000_000));
		
		return result;
	}
}
