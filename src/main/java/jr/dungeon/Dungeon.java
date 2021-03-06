package jr.dungeon;

import com.google.gson.annotations.Expose;
import jr.JRogue;
import jr.Settings;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.events.BeforePlayerChangeLevelEvent;
import jr.dungeon.entities.events.EntityAddedEvent;
import jr.dungeon.entities.player.Player;
import jr.dungeon.events.*;
import jr.dungeon.generators.DungeonGenerator;
import jr.dungeon.generators.DungeonNameGenerator;
import jr.dungeon.generators.GeneratorStandard;
import jr.dungeon.io.Messenger;
import jr.dungeon.io.Prompt;
import jr.dungeon.io.YesNoPrompt;
import jr.dungeon.serialisation.DungeonSerialiser;
import jr.dungeon.serialisation.Serialisable;
import jr.dungeon.tiles.Tile;
import jr.dungeon.wishes.Wishes;
import jr.utils.Point;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

/**
 * The entire Dungeon object. This object contains all information about the actual game state, including the turn,
 * levels, and player.
 */
public class Dungeon implements Serialisable, Messenger {
    /**
     * The amount of 'ticks' in a turn.
     *
     * @see EntityLiving
     */
    public static final int NORMAL_SPEED = 12;
    
    /**
     * The default {@link Level} width. This may not be the Level's actual width. Use {@link TileStore#getWidth()}
     * for that instead.
     */
    public static final int LEVEL_WIDTH = 90;
    /**
     * The default {@link Level} height. This may not be the Level's actual height. Use {@link TileStore#getHeight()}
     * for that instead.
     */
    public static final int LEVEL_HEIGHT = 40;
    
    /**
     * The 'GAME' log level.
     */
    private static org.apache.logging.log4j.Level gameLogLevel = org.apache.logging.log4j.Level.forName("GAME", 350);
    
    /**
     * The version of the game this dungeon was last saved on
     */
    @Expose @Getter @Setter private String version = JRogue.VERSION;
    
    /**
     * Randomly generated name of this dungeon
     */
    @Expose @Getter @Setter private String originalName;
    
    /**
     * User-chosen name of this dungeon
     */
    @Expose @Getter @Setter private String name;
    
    /**
     * Map of the Dungeon's {@link Level}s with unique UUIDs as keys for serialisation reference.
     */
    @Expose @Getter private Map<UUID, Level> levels = new HashMap<>();
    
    @Expose @Getter private UUID firstLevelUUID;
    
    /**
     * The {@link Level} that the {@link Player} is currently on.
     */
    @Getter Level level;
    /**
     * The actual {@link Player} entity.
     */
    @Getter @Setter private Player player;
    
    public EventSystem eventSystem;
    
    @Expose public final TurnSystem turnSystem;
    
    public DungeonSerialiser serialiser;
    
    /**
     * @see Prompt
     *
     * @return The current {@link Prompt} or null.
     */
    @Getter private Prompt prompt;
    /**
     * The current user-specified {@link Settings}.
     */
    private Settings settings;
    
    private List<String> logHistory = new LinkedList<>();
    
    /**
     * The entire Dungeon object. This object contains all information about the actual game state, including the turn,
     * levels, and player.
     */
    public Dungeon() {
        this.settings = JRogue.getSettings();
        
        serialiser = new DungeonSerialiser(this);
        eventSystem = new EventSystem(this);
        turnSystem = new TurnSystem(this);
    }
    
    public void initialise() {
        // gameLogLevel = org.apache.logging.log4j.Level.getLevel("GAME");
    }
    
    @Override
    public void afterDeserialise() {
        serialiser = new DungeonSerialiser(this);
        eventSystem = new EventSystem(this);
        turnSystem.setDungeon(this);
    }
    
    /**
     * Starts/resumes a game. Should be called after the first level in the session is loaded.
     */
    public void start() {
        eventSystem.triggerEvent(new LevelChangeEvent(level));
        eventSystem.triggerEvent(new BeforeGameStartedEvent(turnSystem.getTurn() <= 0));
        
        if (turnSystem.getTurn() <= 0) {
            You("drop down into [CYAN]%s[].", this.name);
            turnSystem.turn(true);
            eventSystem.triggerEvent(new GameStartedEvent(true));
        } else {
            eventSystem.triggerEvent(new BeforeTurnEvent(turnSystem.getTurn()));
            log("Welcome back to [CYAN]%s[].", this.name);
            level.entityStore.processEntityQueues(false);
            eventSystem.triggerEvent(new TurnEvent(turnSystem.getTurn()));
            eventSystem.triggerEvent(new GameStartedEvent(false));
        }
    }

    /**
     * Creates a new {@link Level}. The {@link DungeonGenerator generatorClass} is invoked to generate the level. The
     * {@link Tile source tile} is the tile that the {@link Player} came from before entering this new level. The
     * generator will typically generate a staircase or ladder at the beginning, so this source tile is used to know
     * where to send the player <em>back</em> to when they climb back up.
     *
     * For alternative  types of generation, such as from traps, you may want to pass something different to the source
     * tile. For example, if you have a broken floor trap, where the player drops into the level below when they step
     * into it, you will instead want to search for the natural downstairs in the source level and use that as the
     * source tile, so that when the player climbs back up the stairs, they arrive at the source level's downstairs
     * rather than back at the trap.
     *
     * @param depth The depth the level is located at.
     * @param sourceTile The {@link Tile} the {@link Player} came from (usually a staircase).
     * @param generatorClass The {@link jr.dungeon.generators.DungeonGenerator} to use to generate this level.
     * @return The generated level.
     */
    public Level newLevel(int depth, Tile sourceTile, Class<? extends DungeonGenerator> generatorClass) {
        Level level = new Level(UUID.randomUUID(), this, LEVEL_WIDTH, LEVEL_HEIGHT, depth);
        levels.put(level.getUUID(), level);
        level.generate(sourceTile, generatorClass);
        return level;
    }
    
    /**
     * Randomly generates a level and switches the dungeon to it.
     *
     * @return The level that was generated.
     */
    public Level generateFirstLevel() {
        this.originalName = DungeonNameGenerator.generate();
        this.name = this.originalName;
        
        if (level != null) {
            player.remove();
        } else {
            level = new Level(this, LEVEL_WIDTH, LEVEL_HEIGHT, -1);
            levels.put(level.getUUID(), level);
        }
        
        firstLevelUUID = level.getUUID();
        
        level.generate(null, GeneratorStandard.class);
        
        if (player == null) {
            player = new Player(
                this,
                level,
                level.getSpawnPoint(),
                settings.getPlayerName(),
                settings.getRole()
            );
        }
        
        player.setLevel(level, level.getSpawnPoint());
        level.entityStore.addEntity(player);
        
        eventSystem.triggerEvent(new LevelChangeEvent(level));
        
        return level;
    }

    /**
     * Switches the level to {@code level}.
     *
     * @param level The level to switch to.
     * @param point The position to spawn the player at.
     */
    public void changeLevel(Level level, Point point) {
        eventSystem.triggerEvent(new BeforePlayerChangeLevelEvent(
            player,
            this.level.tileStore.getTile(player.getPosition()),
            level.tileStore.getTile(point)
        ), EventInvocationTime.IMMEDIATELY);
        
        getPlayer().setLevel(this.level = level, point);
        
        turnSystem.turn(true);
        
        eventSystem.triggerEvent(new LevelChangeEvent(level));
        level.entityStore.getEntities().forEach(e -> eventSystem.triggerEvent(new EntityAddedEvent(e, false)));
    }
    
    @Override
    public List<String> getLogHistory() {
        return logHistory;
    }
    
    /**
     * Displays a message in the dungeon's event log.
     * All messages are formatted using {@link java.lang.String}'s {@code format} method.
     *
     * @param s The format used for the message.
     * @param objects The format parameters.
     */
    public void log(String s, Object... objects) {
        String logString = String.format(s, objects);
        
        JRogue.getLogger().log(gameLogLevel, logString);
        logHistory.add(logString);
        eventSystem.triggerEvent(new LogEvent(logString));
    }

    /**
     * Triggers a {@link Prompt}.
     *
     * @param prompt The prompt to trigger.
     */
    public void prompt(Prompt prompt) {
        this.prompt = prompt;
        eventSystem.triggerEvent(new PromptEvent(prompt));
    }

    /**
     * Responds to a prompt.
     *
     * @param response The char response to send.
     */
    public void promptRespond(char response) {
        if (prompt != null) {
            Prompt prompt = this.prompt;
            prompt.respond(response);
            
            if (prompt == this.prompt) {
                this.prompt = null;
                eventSystem.triggerEvent(new PromptEvent(null));
            }
        }
    }

    /**
     * Ends a prompt, behaves like pressing ESC in the game.
     */
    public void escapePrompt() {
        if (prompt != null) {
            Prompt prompt = this.prompt;
            this.prompt = null;
            prompt.escape();
            
            eventSystem.triggerEvent(new PromptEvent(null));
        }
    }

    /**
     * @return true if there is an active prompt.
     */
    public boolean hasPrompt() {
        return prompt != null;
    }

    /**
     * @return true if there is an active prompt that cannot be forcefully ended (escaped).
     */
    public boolean isPromptEscapable() {
        return prompt != null && prompt.isEscapable();
    }
    
    /**
     * Make a wish, used in debug mode.
     *
     * @see jr.dungeon.wishes.Wishes
     * @param wish The wish to make.
     */
    public void wish(String wish) {
        if (!Wishes.get().makeWish(this, wish))
            log("[RED]One or more wishes have failed.");
    }

    /**
     * @param uuid A level UUID.
     * @return The level with the specified UUID.
     */
    public Level getLevelFromUUID(UUID uuid) {
        return levels.get(uuid);
    }
    
    /**
     * Triggers the "Really quit without saving?" prompt.
     */
    public void quit() {
        prompt(new YesNoPrompt("Really quit without saving?", true, yes -> {
            if (yes) {
                serialiser.deleteSave();
                eventSystem.triggerEvent(new QuitEvent());
            }
        }));
    }
    
    /**
     * Triggers the "Really save and quit?" prompt.
     */
    public void saveAndQuit() {
        prompt(new YesNoPrompt("Really save and quit?", true, yes -> {
            if (yes && player.isAlive()) {
                serialiser.save();
                eventSystem.triggerEvent(new SaveAndQuitEvent());
            }
        }));
    }
    
    /**
     * Sets the level for internal usage from the {@link DungeonSerialiser}. Do not use this method.
     *
     * @param level The level to set as the current level.
     */
    public void setLevelInternal(Level level) {
        this.level = level;
    }
    
    public void searchForPlayer() {
        levels.values().stream()
            .map(l -> l.entityStore.getEntities())
            .flatMap(Collection::stream)
            .filter(Player.class::isInstance)
            .findFirst()
            .map(Player.class::cast)
            .ifPresent(this::setPlayer);
    }
}
