package jr.dungeon;

import jr.JRogue;
import jr.dungeon.entities.DamageSource;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.containers.EntityChest;
import jr.dungeon.entities.containers.EntityItem;
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
import jr.dungeon.items.valuables.ItemGold;
import jr.dungeon.items.valuables.ItemThermometer;
import jr.dungeon.items.weapons.*;
import jr.dungeon.tiles.TileType;
import jr.utils.RandomUtils;

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
			dungeon.getLevel().getEntityStore().getEntities().stream()
				.filter(e -> e instanceof EntityLiving && !(e instanceof Player))
				.forEach(e -> ((EntityLiving) e).kill(DamageSource.WISH_FOR_DEATH, 0, null, false));
			
			dungeon.turn();
		} else if (wish.equalsIgnoreCase("nutrition")) {
			player.setNutrition(1000);
		} else if (wish.equalsIgnoreCase("downstairs")) {
			Arrays.stream(player.getLevel().getTileStore().getTiles())
				.filter(t -> t.getType() == TileType.TILE_ROOM_STAIRS_DOWN)
				.findFirst()
				.ifPresent(t -> player.teleport(t.getX(), t.getY()));
		} else if (wish.equalsIgnoreCase("godmode")) {
			player.godmode();
		} else if (wish.equalsIgnoreCase("chest")) {
			dungeon.getLevel().getEntityStore().addEntity(
				new EntityChest(dungeon, dungeon.getLevel(), player.getX(), player.getY())
			);
			dungeon.turn();
		} else if (wish.equalsIgnoreCase("fountain")) {
			dungeon.getLevel().getEntityStore().addEntity(
				new EntityFountain(dungeon, dungeon.getLevel(), player.getX(), player.getY())
			);
			dungeon.turn();
		} else if (wish.equalsIgnoreCase("candlestick")) {
			dungeon.getLevel().getEntityStore().addEntity(
				new EntityCandlestick(dungeon, dungeon.getLevel(), player.getX(), player.getY())
			);
			dungeon.turn();
		} else if (wish.equalsIgnoreCase("weapon rack")) {
			dungeon.getLevel().getEntityStore().addEntity(
				new EntityWeaponRack(dungeon, dungeon.getLevel(), player.getX(), player.getY())
			);
			dungeon.turn();
		} else if (wish.equalsIgnoreCase("altar")) {
			dungeon.getLevel().getEntityStore().addEntity(
				new EntityAltar(dungeon, dungeon.getLevel(), player.getX(), player.getY())
			);
			dungeon.turn();
		} else if (wish.equalsIgnoreCase("rug")) {
			player.getLevel().getTileStore()
				.setTileType(player.getX(), player.getY(), TileType.TILE_ROOM_RUG);
		} else if (wish.equalsIgnoreCase("dirt")) {
			player.getLevel().getTileStore()
				.setTileType(player.getX(), player.getY(), TileType.TILE_ROOM_DIRT);
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
				
				dungeon.getLevel().getEntityStore().addEntity(new EntityItem(dungeon, dungeon.getLevel(),
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
			dungeon.getLevel().getEntityStore()
				.addEntity(new MonsterJackal(dungeon, dungeon.getLevel(), player.getX(), player.getY()));
			return true;
		} else if (wish.equalsIgnoreCase("fox")) {
			dungeon.getLevel().getEntityStore()
				.addEntity(new MonsterFox(dungeon, dungeon.getLevel(), player.getX(), player.getY()));
			return true;
		} else if (wish.equalsIgnoreCase("lizard")) {
			dungeon.getLevel().getEntityStore()
				.addEntity(new MonsterLizard(dungeon, dungeon.getLevel(), player.getX(), player.getY()));
			return true;
		}  else if (wish.equalsIgnoreCase("hound")) {
			dungeon.getLevel().getEntityStore()
				.addEntity(new MonsterHound(dungeon, dungeon.getLevel(), player.getX(), player.getY()));
			return true;
		} else if (wish.equalsIgnoreCase("hellhound")) {
			dungeon.getLevel().getEntityStore()
				.addEntity(new MonsterHellhound(dungeon, dungeon.getLevel(), player.getX(), player.getY()));
			return true;
		} else if (wish.equalsIgnoreCase("icehound")) {
			dungeon.getLevel().getEntityStore()
				.addEntity(new MonsterIcehound(dungeon, dungeon.getLevel(), player.getX(), player.getY()));
			return true;
		} else if (wish.equalsIgnoreCase("spider")) {
			dungeon.getLevel().getEntityStore()
				.addEntity(new MonsterSpider(dungeon, dungeon.getLevel(), player.getX(), player.getY()));
			return true;
		} else if (wish.equalsIgnoreCase("rat")) {
			dungeon.getLevel().getEntityStore()
				.addEntity(new MonsterRat(dungeon, dungeon.getLevel(), player.getX(), player.getY()));
			return true;
		} else if (wish.equalsIgnoreCase("skeleton")) {
			dungeon.getLevel().getEntityStore()
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
