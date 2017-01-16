package pw.lemmmy.jrogue.dungeon;

import pw.lemmmy.jrogue.JRogue;
import pw.lemmmy.jrogue.dungeon.entities.DamageSource;
import pw.lemmmy.jrogue.dungeon.entities.LivingEntity;
import pw.lemmmy.jrogue.dungeon.entities.containers.EntityChest;
import pw.lemmmy.jrogue.dungeon.entities.containers.EntityItem;
import pw.lemmmy.jrogue.dungeon.entities.containers.EntityWeaponRack;
import pw.lemmmy.jrogue.dungeon.entities.decoration.EntityCandlestick;
import pw.lemmmy.jrogue.dungeon.entities.decoration.EntityFountain;
import pw.lemmmy.jrogue.dungeon.entities.magic.EntityAltar;
import pw.lemmmy.jrogue.dungeon.entities.monsters.canines.*;
import pw.lemmmy.jrogue.dungeon.entities.monsters.critters.MonsterLizard;
import pw.lemmmy.jrogue.dungeon.entities.monsters.critters.MonsterRat;
import pw.lemmmy.jrogue.dungeon.entities.monsters.critters.MonsterSpider;
import pw.lemmmy.jrogue.dungeon.entities.monsters.humanoids.MonsterSkeleton;
import pw.lemmmy.jrogue.dungeon.entities.player.Player;
import pw.lemmmy.jrogue.dungeon.items.Item;
import pw.lemmmy.jrogue.dungeon.items.ItemStack;
import pw.lemmmy.jrogue.dungeon.items.Material;
import pw.lemmmy.jrogue.dungeon.items.comestibles.*;
import pw.lemmmy.jrogue.dungeon.items.magical.ItemSpellbook;
import pw.lemmmy.jrogue.dungeon.items.magical.spells.SpellLightOrb;
import pw.lemmmy.jrogue.dungeon.items.projectiles.ItemArrow;
import pw.lemmmy.jrogue.dungeon.items.quaffable.potions.BottleType;
import pw.lemmmy.jrogue.dungeon.items.quaffable.potions.ItemPotion;
import pw.lemmmy.jrogue.dungeon.items.quaffable.potions.PotionType;
import pw.lemmmy.jrogue.dungeon.items.valuables.ItemGold;
import pw.lemmmy.jrogue.dungeon.items.valuables.ItemThermometer;
import pw.lemmmy.jrogue.dungeon.items.weapons.*;
import pw.lemmmy.jrogue.dungeon.tiles.TileType;
import pw.lemmmy.jrogue.utils.RandomUtils;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Wish {
	private static final Pattern wishDRoll = Pattern.compile("^roll (\\d+)?d(\\d+)(?:\\+(\\d+))?");
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
		} else if (wish.equalsIgnoreCase("weapon rack")) {
			dungeon.getLevel().addEntity(
				new EntityWeaponRack(dungeon, dungeon.getLevel(), player.getX(), player.getY())
			);
			dungeon.turn();
		} else if (wish.equalsIgnoreCase("altar")) {
			dungeon.getLevel().addEntity(
				new EntityAltar(dungeon, dungeon.getLevel(), player.getX(), player.getY())
			);
			dungeon.turn();
		} else if (wish.equalsIgnoreCase("rug")) {
			player.getLevel().setTileType(player.getX(), player.getY(), TileType.TILE_ROOM_RUG);
		} else if (wishMonsters(dungeon, player, wish)) {
			dungeon.turn();
		} else if (wishItems(dungeon, player, wish)) {
			dungeon.turn();
		} else {
			Matcher wishDRollMatcher = wishDRoll.matcher(wish);
			
			if (wishDRollMatcher.find()) {
				String a = wishDRollMatcher.group(1);
				
				if (a == null) {
					a = "1";
				}
				
				String x = wishDRollMatcher.group(2);
				
				String b = wishDRollMatcher.group(3);
				
				if (b == null) {
					b = "0";
				}
				
				int ia = Integer.parseInt(a);
				int ix = Integer.parseInt(x);
				int ib = Integer.parseInt(b);
				
				int roll = RandomUtils.roll(ia, ix, ib);
				dungeon.greenYou("roll a %,dd%,d+%,d. Result: %,d.", ia, ix, ib, roll);
			}
			
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
		} else if (wish.equalsIgnoreCase("lizard")) {
			dungeon.getLevel().addEntity(new MonsterLizard(dungeon, dungeon.getLevel(), player.getX(), player.getY()));
			return true;
		}  else if (wish.equalsIgnoreCase("hound")) {
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
