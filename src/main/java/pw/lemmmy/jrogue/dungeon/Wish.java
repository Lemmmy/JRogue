package pw.lemmmy.jrogue.dungeon;

import pw.lemmmy.jrogue.JRogue;
import pw.lemmmy.jrogue.dungeon.entities.*;
import pw.lemmmy.jrogue.dungeon.entities.containers.EntityChest;
import pw.lemmmy.jrogue.dungeon.entities.containers.EntityItem;
import pw.lemmmy.jrogue.dungeon.entities.decoration.EntityCandlestick;
import pw.lemmmy.jrogue.dungeon.entities.decoration.EntityFountain;
import pw.lemmmy.jrogue.dungeon.entities.monsters.canines.*;
import pw.lemmmy.jrogue.dungeon.entities.monsters.critters.MonsterRat;
import pw.lemmmy.jrogue.dungeon.entities.monsters.critters.MonsterSpider;
import pw.lemmmy.jrogue.dungeon.entities.monsters.humanoids.MonsterSkeleton;
import pw.lemmmy.jrogue.dungeon.entities.player.Player;
import pw.lemmmy.jrogue.dungeon.items.*;
import pw.lemmmy.jrogue.dungeon.items.comestibles.*;
import pw.lemmmy.jrogue.dungeon.items.quaffable.potions.ItemPotion;
import pw.lemmmy.jrogue.dungeon.items.quaffable.potions.PotionType;
import pw.lemmmy.jrogue.dungeon.items.valuables.ItemGold;
import pw.lemmmy.jrogue.dungeon.items.weapons.ItemDagger;
import pw.lemmmy.jrogue.dungeon.items.weapons.ItemLongsword;
import pw.lemmmy.jrogue.dungeon.items.weapons.ItemShortsword;
import pw.lemmmy.jrogue.dungeon.tiles.TileType;
import pw.lemmmy.jrogue.utils.RandomUtils;

import java.util.Arrays;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Wish {
	private static final Pattern wishGold = Pattern.compile("^(\\d+) gold$");
	private static final Pattern wishGoldDropped = Pattern.compile("^drop(?:ed)? (\\d+) gold$");
	private static final Pattern wishSword = Pattern
		.compile("^(wood|stone|bronze|iron|steel|silver|gold|mithril|adamantite) (shortsword|longsword|dagger)$");
	
	public static void wish(Dungeon dungeon, String wish) {
		Player player = dungeon.getPlayer();
		
		if (player.isDebugger()) {
			JRogue.getLogger().debug("Player wished for '{}'", wish);
		}
		
		wish = wish.toLowerCase();
		
		if (wish.equalsIgnoreCase("death")) {
			player.kill(DamageSource.WISH_FOR_DEATH, 0, null, false);
		} else if (wish.equalsIgnoreCase("kill all")) {
			dungeon.getLevel().getEntities().stream()
				.filter(e -> e instanceof LivingEntity && !(e instanceof Player))
				.forEach(e -> ((LivingEntity) e).kill(DamageSource.WISH_FOR_DEATH, 0, null, false));
			
			dungeon.turn();
		} else if (wish.equalsIgnoreCase("nutrition")) {
			player.setNutrition(1000);
		} else if (wish.equalsIgnoreCase("downstairs")) {
			Arrays.stream(player.getLevel().getTiles())
				.filter(t -> t.getType() == TileType.TILE_ROOM_STAIRS_DOWN)
				.findFirst()
				.ifPresent(t -> player.teleport(t.getX(), t.getY()));
		} else if (wish.equalsIgnoreCase("godmode")) {
			player.godmode();
		} else if (wish.equalsIgnoreCase("chest")) {
			dungeon.getLevel().addEntity(
				new EntityChest(dungeon, dungeon.getLevel(), player.getX(), player.getY())
			);
			dungeon.turn();
		} else if (wish.equalsIgnoreCase("fountain")) {
			dungeon.getLevel().addEntity(
				new EntityFountain(dungeon, dungeon.getLevel(), player.getX(), player.getY())
			);
			dungeon.turn();
		} else if (wish.equalsIgnoreCase("candlestick")) {
			dungeon.getLevel().addEntity(
				new EntityCandlestick(dungeon, dungeon.getLevel(), player.getX(), player.getY())
			);
			dungeon.turn();
		} else if (wish.equalsIgnoreCase("rug")) {
			Arrays.stream(player.getLevel().getTiles())
					.filter(t -> t.getX() == player.getX())
					.filter(t -> t.getY() == player.getY())
					.findFirst()
					.ifPresent(t -> t.setType(TileType.TILE_ROOM_RUG));
		} else if (wishMonsters(dungeon, player, wish)) {
			dungeon.turn();
		} else if (wishItems(dungeon, player, wish)) {
			dungeon.turn();
		} else {
			Matcher wishGoldDroppedMatcher = wishGoldDropped.matcher(wish);
			
			if (wishGoldDroppedMatcher.find()) {
				int gold = Integer.parseInt(wishGoldDroppedMatcher.group(1));
				
				dungeon.getLevel().addEntity(new EntityItem(dungeon, dungeon.getLevel(),
					player.getX(),
					player.getY(),
					new ItemStack(
						new ItemGold(),
						gold
					)
				));
				
				dungeon.turn();
				return;
			}
			
			Matcher wishGoldMatcher = wishGold.matcher(wish);
			
			if (wishGoldMatcher.find()) {
				int gold = Integer.parseInt(wishGoldMatcher.group(1));
				
				player.giveGold(gold);
				
				dungeon.turn();
			}
		}
	}
	
	private static boolean wishMonsters(Dungeon dungeon, Player player, String wish) {
		if (wish.equalsIgnoreCase("jackal")) {
			dungeon.getLevel().addEntity(new MonsterJackal(dungeon, dungeon.getLevel(), player.getX(), player.getY()));
			return true;
		} else if (wish.equalsIgnoreCase("fox")) {
			dungeon.getLevel().addEntity(new MonsterFox(dungeon, dungeon.getLevel(), player.getX(), player.getY()));
			return true;
		} else if (wish.equalsIgnoreCase("hound")) {
			dungeon.getLevel().addEntity(new MonsterHound(dungeon, dungeon.getLevel(), player.getX(), player.getY()));
			return true;
		} else if (wish.equalsIgnoreCase("hellhound")) {
			dungeon.getLevel()
				.addEntity(new MonsterHellhound(dungeon, dungeon.getLevel(), player.getX(), player.getY()));
			return true;
		} else if (wish.equalsIgnoreCase("icehound")) {
			dungeon.getLevel()
				.addEntity(new MonsterIcehound(dungeon, dungeon.getLevel(), player.getX(), player.getY()));
			return true;
		} else if (wish.equalsIgnoreCase("spider")) {
			dungeon.getLevel().addEntity(new MonsterSpider(dungeon, dungeon.getLevel(), player.getX(), player.getY()));
			return true;
		} else if (wish.equalsIgnoreCase("rat")) {
			dungeon.getLevel().addEntity(new MonsterRat(dungeon, dungeon.getLevel(), player.getX(), player.getY()));
			return true;
		} else if (wish.equalsIgnoreCase("skeleton")) {
			dungeon.getLevel()
				.addEntity(new MonsterSkeleton(dungeon, dungeon.getLevel(), player.getX(), player.getY()));
			return true;
		}
		
		return false;
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
		} else if (wish.equalsIgnoreCase("potion")) {
			ItemPotion.BottleType bottle = RandomUtils.randomFrom(ItemPotion.BottleType.values());
			PotionType potionType = RandomUtils.randomFrom(PotionType.values());
			float potency = RandomUtils.randomFloat(6f);
			
			ItemPotion potion = new ItemPotion();
			potion.setBottleType(bottle);
			potion.setPotionType(potionType);
			potion.setEmpty(false);
			potion.setPotency(potency);
			item = potion;
		}
		
		if (item != null && player.getContainer().isPresent()) {
			player.getContainer().get().add(new ItemStack(item));
			
			return true;
		}
		
		return false;
	}
}
