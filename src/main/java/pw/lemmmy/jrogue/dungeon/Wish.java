package pw.lemmmy.jrogue.dungeon;

import pw.lemmmy.jrogue.JRogue;
import pw.lemmmy.jrogue.dungeon.entities.*;
import pw.lemmmy.jrogue.dungeon.entities.monsters.*;
import pw.lemmmy.jrogue.dungeon.items.*;

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
		} else {
			Matcher wishGoldDroppedMatcher = wishGoldDropped.matcher(wish);

			if (wishGoldDroppedMatcher.find()) {
				int gold = Integer.parseInt(wishGoldDroppedMatcher.group(1));

				dungeon.getLevel().addEntity(new EntityItem(dungeon, dungeon.getLevel(), new ItemStack(
					new ItemGold(),
					gold
				), player.getX(), player.getY()));

				dungeon.turn();
				return;
			}

			Matcher wishGoldMatcher = wishGold.matcher(wish);

			if (wishGoldMatcher.find()) {
				int gold = Integer.parseInt(wishGoldMatcher.group(1));

				player.giveGold(gold);

				dungeon.turn();
				return;
			}

			if (wish.equalsIgnoreCase("godmode")) {
				player.godmode();
				return;
			}

			if (wish.equalsIgnoreCase("chest")) {
				dungeon.getLevel().addEntity(
					new EntityChest(dungeon, dungeon.getLevel(), player.getX(), player.getY())
				);
				dungeon.turn();
				return;
			}

			if (wish.equalsIgnoreCase("fountain")) {
				dungeon.getLevel().addEntity(
					new EntityFountain(dungeon, dungeon.getLevel(), player.getX(), player.getY())
				);
				dungeon.turn();
				return;
			}

			if (wishMonsters(dungeon, player, wish)) {
				dungeon.turn();
				return;
			}

			if (wishItems(dungeon, player, wish)) {
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
			dungeon.getLevel().addEntity(new MonsterHellhound(dungeon, dungeon.getLevel(), player.getX(), player.getY()));
			return true;
		} else if (wish.equalsIgnoreCase("icehound")) {
			dungeon.getLevel().addEntity(new MonsterIcehound(dungeon, dungeon.getLevel(), player.getX(), player.getY()));
			return true;
		} else if (wish.equalsIgnoreCase("spider")) {
			dungeon.getLevel().addEntity(new MonsterSpider(dungeon, dungeon.getLevel(), player.getX(), player.getY()));
			return true;
		} else if (wish.equalsIgnoreCase("rat")) {
			dungeon.getLevel().addEntity(new MonsterRat(dungeon, dungeon.getLevel(), player.getX(), player.getY()));
			return true;
		} else if (wish.equalsIgnoreCase("skeleton")) {
			dungeon.getLevel().addEntity(new MonsterSkeleton(dungeon, dungeon.getLevel(), player.getX(), player.getY()));
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
		}

		if (item != null && player.getContainer().isPresent()) {
			player.getContainer().get().add(new ItemStack(item));

			return true;
		}

		return false;
	}
}
