package pw.lemmmy.jrogue.dungeon;

import pw.lemmmy.jrogue.JRogue;
import pw.lemmmy.jrogue.Settings;
import pw.lemmmy.jrogue.dungeon.entities.*;
import pw.lemmmy.jrogue.dungeon.entities.monsters.*;
import pw.lemmmy.jrogue.dungeon.entities.roles.RoleWizard;
import pw.lemmmy.jrogue.dungeon.generators.DungeonNameGenerator;
import pw.lemmmy.jrogue.dungeon.generators.StandardDungeonGenerator;
import pw.lemmmy.jrogue.dungeon.items.*;
import pw.lemmmy.jrogue.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Dungeon {
	public static final int NORMAL_SPEED = 12;

	private static final int LEVEL_WIDTH = 80;
	private static final int LEVEL_HEIGHT = 30;
	private static final Pattern wishGold = Pattern.compile("^(\\d+) gold$");
	private static final Pattern wishGoldDropped = Pattern.compile("^drop(?:ed)? (\\d+) gold$");
	private static final Pattern wishSword = Pattern
		.compile("^(wood|stone|bronze|iron|steel|silver|gold|mithril|adamantite) (shortsword|longsword|dagger)$");
	private final List<Listener> listeners = new ArrayList<>();
	/**
	 * Randomly generated name of this dungeon
	 */
	private String originalName;
	/**
	 * User-chosen name of this dungeon
	 */
	private String name;
	private Level level;
	private Player player;
	private long turn = 0;
	private long nextExerciseCounter = 500;
	private Prompt prompt;
	private Settings settings;

	public Dungeon(Settings settings) {
		this.originalName = DungeonNameGenerator.generate();
		this.name = this.originalName;
		this.settings = settings;

		generateLevel();
	}

	public void generateLevel() {
		if (level != null) {
			level.removeEntity(player);
		}

		boolean gotLevel = false;

		do {
			level = new Level(this, LEVEL_WIDTH, LEVEL_HEIGHT, -1);

			if (!(new StandardDungeonGenerator(level).generate())) {
				continue;
			}

			level.buildLight();

			gotLevel = true;
		} while (!gotLevel);

		if (player == null) {
			player = new Player(
				this,
				level,
				level.getSpawnX(),
				level.getSpawnY(),
				settings.getPlayerName(),
				new RoleWizard()
			);
		} else {
			player.setPosition(level.getSpawnX(), level.getSpawnY());
		}

		player.setLevel(level);
		level.addEntity(player);

		listeners.forEach(l -> l.onLevelChange(level));
	}

	public void addListener(Listener listener) {
		listeners.add(listener);
	}

	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}

	public String getOriginalName() {
		return originalName;
	}

	public String getName() {
		return name;
	}

	public void rerollName() {
		this.originalName = DungeonNameGenerator.generate();
		this.name = this.originalName;
	}

	public void Your(String s, Object... objects) {
		log("Your " + s, objects);
	}

	public void log(String s, Object... objects) {
		JRogue.getLogger().info(String.format(s, objects));

		listeners.forEach(l -> l.onLog(String.format(s, objects)));
	}

	public void The(String s, Object... objects) {
		log("The " + s, objects);
	}

	public void logRandom(String... strings) {
		log(Utils.randomFrom(strings));
	}

	public void start() {
		You("descend the stairs into [CYAN]%s[].", this.name);
		turn();
	}

	public void You(String s, Object... objects) {
		log("You " + s, objects);
	}

	public void turn() {
		listeners.forEach(l -> l.onBeforeTurn(turn + 1));
		level.processEntityQueues();

		player.setMovementPoints(player.getMovementPoints() - NORMAL_SPEED);

		do {
			boolean entitiesCanMove = false;

			do {
				if (!player.isAlive()) {
					break;
				}

				entitiesCanMove = moveEntities();

				if (player.getMovementPoints() > NORMAL_SPEED) {
					break;
				}

			} while (entitiesCanMove);

			if (!entitiesCanMove && player.getMovementPoints() < NORMAL_SPEED) {
				for (Entity entity : level.getEntities()) {
					if (!player.isAlive()) {
						break;
					}

					if (entity instanceof LivingEntity && !((LivingEntity) entity).isAlive()) {
						continue;
					}

					entity.update();

					if (entity instanceof EntityTurnBased) {
						EntityTurnBased turnBasedEntity = (EntityTurnBased) entity;

						turnBasedEntity.setMovementPoints(turnBasedEntity.getMovementSpeed());
					}
				}

				if (player.getMovementPoints() < 0) {
					player.setMovementPoints(0);
				}

				turn++;

				update();

				break; // FIXME: this was a lazy infinite loop prevention, but now there is no fun
			}
		} while (player.isAlive() && player.getMovementPoints() < NORMAL_SPEED);

		if (player.isAlive()) {
			player.move();
		} else {
			return;
		}

		level.processEntityQueues();

		getLevel().buildLight();
		getLevel().updateSight(getPlayer());

		listeners.forEach(l -> l.onTurn(turn));
	}

	private boolean moveEntities() {
		AtomicBoolean somebodyCanMove = new AtomicBoolean(false);

		level.getEntities().stream()
			 .filter(e -> e instanceof EntityTurnBased && !(e instanceof Player) &&
				 (e instanceof LivingEntity && ((LivingEntity) e).isAlive()) &&
				 !(((EntityTurnBased) e).getMovementPoints() < NORMAL_SPEED))
			 .forEach(e -> {
				 EntityTurnBased tbe = (EntityTurnBased) e;
				 tbe.setMovementPoints(tbe.getMovementPoints() - NORMAL_SPEED);

				 if (tbe.getMovementPoints() >= NORMAL_SPEED) {
					 somebodyCanMove.set(true);
				 }

				 tbe.move();
			 });

		return somebodyCanMove.get();
	}

	private void update() {
		// random dungeon updates
	}

	public Level getLevel() {
		return level;
	}

	public Player getPlayer() {
		return player;
	}

	public long getTurn() {
		return turn;
	}

	public void setTurn(long turn) {
		this.turn = turn;
	}

	public void prompt(Prompt prompt) {
		this.prompt = prompt;
		listeners.forEach(l -> l.onPrompt(prompt));
	}

	public void promptRespond(char response) {
		if (prompt != null) {
			Prompt prompt = this.prompt;
			this.prompt = null;
			prompt.respond(response);

			listeners.forEach(l -> l.onPrompt(null));
		}
	}

	public void escapePrompt() {
		if (prompt != null) {
			Prompt prompt = this.prompt;
			this.prompt = null;
			prompt.escape();

			listeners.forEach(l -> l.onPrompt(null));
		}
	}

	public boolean hasPrompt() {
		return prompt != null;
	}

	public boolean isPromptEscapable() {
		return prompt != null && prompt.isEscapable();
	}

	public void wish(String wish) {
		if (player.isDebugger()) {
			JRogue.getLogger().debug("Player wished for '{}'", wish);
		}

		wish = wish.toLowerCase();

		if (wish.equalsIgnoreCase("death")) {
			player.kill(DamageSource.WISH_FOR_DEATH);
		} else {
			Matcher wishGoldDroppedMatcher = wishGoldDropped.matcher(wish);

			if (wishGoldDroppedMatcher.find()) {
				int gold = Integer.parseInt(wishGoldDroppedMatcher.group(1));

				getLevel().addEntity(new EntityItem(this, getLevel(), new ItemStack(
					new ItemGold(),
					gold
				), player.getX(), player.getY()));

				turn();
				return;
			}

			Matcher wishGoldMatcher = wishGold.matcher(wish);

			if (wishGoldMatcher.find()) {
				int gold = Integer.parseInt(wishGoldMatcher.group(1));

				player.giveGold(gold);

				turn();
				return;
			}

			if (wish.equalsIgnoreCase("godmode")) {
				player.godmode();
				return;
			}

			if (wish.equalsIgnoreCase("chest")) {
				getLevel().addEntity(new EntityChest(this, getLevel(), player.getX(), player.getY()));
				turn();
				return;
			}

			if (wish.equalsIgnoreCase("fountain")) {
				getLevel().addEntity(new EntityFountain(this, getLevel(), player.getX(), player.getY()));
				turn();
				return;
			}

			if (wishMonsters(wish)) {
				turn();
				return;
			}

			if (wishItems(wish)) {
				return;
			}
		}
	}

	private boolean wishMonsters(String wish) {
		if (wish.equalsIgnoreCase("jackal")) {
			getLevel().addEntity(new MonsterJackal(this, getLevel(), player.getX(), player.getY()));
			return true;
		} else if (wish.equalsIgnoreCase("fox")) {
			getLevel().addEntity(new MonsterFox(this, getLevel(), player.getX(), player.getY()));
			return true;
		} else if (wish.equalsIgnoreCase("hound")) {
			getLevel().addEntity(new MonsterHound(this, getLevel(), player.getX(), player.getY()));
			return true;
		} else if (wish.equalsIgnoreCase("hellhound")) {
			getLevel().addEntity(new MonsterHellhound(this, getLevel(), player.getX(), player.getY()));
			return true;
		} else if (wish.equalsIgnoreCase("icehound")) {
			getLevel().addEntity(new MonsterIcehound(this, getLevel(), player.getX(), player.getY()));
			return true;
		} else if (wish.equalsIgnoreCase("spider")) {
			getLevel().addEntity(new MonsterSpider(this, getLevel(), player.getX(), player.getY()));
			return true;
		}

		return false;
	}

	private boolean wishItems(String wish) {
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

		return false;
	}

	public void entityAdded(Entity entity) {
		listeners.forEach(l -> l.onEntityAdded(entity));
	}

	public void entityMoved(Entity entity, int lastX, int lastY, int newX, int newY) {
		listeners.forEach(l -> l.onEntityMoved(entity, lastX, lastY, newX, newY));
	}

	public void entityRemoved(Entity entity) {
		listeners.forEach(l -> l.onEntityRemoved(entity));
	}

	public interface Listener {
		void onLevelChange(Level level);

		void onBeforeTurn(long turn);

		void onTurn(long turn);

		void onLog(String log);

		void onPrompt(Prompt prompt);

		void onEntityAdded(Entity entity);

		void onEntityMoved(Entity entity, int lastX, int lastY, int newX, int newY);

		void onEntityRemoved(Entity entity);
	}
}
