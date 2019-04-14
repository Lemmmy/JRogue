package jr.rendering.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Matrix4;
import jr.JRogue;
import jr.Settings;
import jr.dungeon.Dungeon;
import jr.dungeon.entities.events.EntityDeathEvent;
import jr.dungeon.entities.player.Player;
import jr.dungeon.events.*;
import jr.rendering.GameAdapter;
import jr.rendering.GameInputProcessor;
import jr.rendering.assets.Assets;
import jr.rendering.assets.RegisterAssetManager;
import jr.rendering.components.*;
import jr.rendering.components.hud.HUDComponent;
import jr.rendering.entities.animations.EntityAnimationData;
import jr.rendering.screens.utils.SlidingTransition;
import jr.rendering.tiles.TileMap;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static jr.rendering.assets.Shaders.shaderFile;

/**
 * The game's renderer. Houses the {@link RendererComponent components} used for rendering, and also handles the main
 * batch and camera.
 */
@Getter
public class GameScreen extends BasicScreen implements EventListener {
    /**
     * The time in seconds to animate movement between turns.
     */
    public static final float TURN_LERP_DURATION = 0.170f;
    
    /**
     * The {@link GameAdapter} instance.
     */
    private GameAdapter game;
    /**
     * The {@link Dungeon} that this renderer should render.
     */
    private Dungeon dungeon;
    /**
     * The user's {@link Settings}.
     */
    private Settings settings;
    
    /**
     * The 'main sprite batch' - the sprite batch that renders the {@link jr.dungeon.Level}'s contents. This is the
     * batch that's inside the game's {@link #camera viewport camera}, and moves along with the player etc.
     */
    private SpriteBatch mainBatch;
    
    /**
     * The 'main camera' - the camera inside the {@link jr.dungeon.Level} that follows the {@link Player}.
     */
    private OrthographicCamera camera, mainBatchCamera;
    
    /**
     * The list of renderer components - components that get a change to render to the screen at their specified
     * Z-indexes.
     */
    private List<RendererComponent> rendererComponents = new ArrayList<>();
    
    private LevelComponent levelComponent;
    private ParticlesComponent particlesBelowComponent;
    private PathComponent pathComponent;
    private EntityComponent entityComponent;
    private ParticlesComponent particlesAboveComponent;
    private LightingComponent lightingComponent;
    private HUDComponent hudComponent;
    private MinimapComponent minimapComponent;
    private FPSCounterComponent fpsCounterComponent;
    
    private FrameBuffer fbo;
    private SpriteBatch fboBatch;
    private ShaderProgram xbr;
    
    private float zoom, zoomRounding, mainBatchZoomRounding;
    
    private float renderTime;
    
    private float turnLerpTime;
    private boolean wasTurnLerping = false;
    private boolean turnLerping = false;
    
    @Getter(AccessLevel.NONE)
    private boolean dontSave = false;
    
    /**
     * The game's main OpenGL renderer using LibGDX.
     *
     * @param game The game adapter instance.
     * @param dungeon The dungeon that should be rendered.
     */
    public GameScreen(GameAdapter game, Dungeon dungeon) {
        this.game = game;
        this.dungeon = dungeon;
        this.dungeon.eventSystem.addListener(this);
        
        JRogue.INSTANCE.setDungeon(dungeon);

        settings = JRogue.getSettings();
        
        updateWindowTitle();
        
        mainBatch = new SpriteBatch();
        
        fbo = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth() * 2, Gdx.graphics.getHeight() * 2, false);
        fboBatch = new SpriteBatch();
        
        xbr = RequiredAssets.xbr;
        if (!xbr.isCompiled()) {
            System.err.println(xbr.getLog());
        }
        
        ShaderProgram.pedantic = false;
        
        initialiseCamera();
        initialiseRendererComponents();
        
        dungeon.start();
    }
    
    private void initialiseCamera() {
        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();
        
        camera = new OrthographicCamera(width, height);
        mainBatchCamera = new OrthographicCamera(width * 2, height * 2);
        
        updateCameraZoom();
    }
    
    private void updateCameraZoom() {
        zoom = 1f / settings.getZoom();
        zoomRounding = 1f / zoom * TileMap.TILE_WIDTH * 8f;
        mainBatchZoomRounding = 1f / zoom * TileMap.TILE_WIDTH * 8f;
        
        camera.zoom = 0.5f;
        camera.update();
        
        mainBatchCamera.zoom = 0.5f;
        mainBatchCamera.update();
    }
    
    private void initialiseRendererComponents() {
        rendererComponents.add(levelComponent = new LevelComponent(this, dungeon, settings));
        rendererComponents.add(particlesBelowComponent = new ParticlesComponent.Below(this, dungeon, settings));
        rendererComponents.add(pathComponent = new PathComponent(this, dungeon, settings));
        rendererComponents.add(entityComponent = new EntityComponent(this, dungeon, settings));
        rendererComponents.add(particlesAboveComponent = new ParticlesComponent.Above(this, dungeon, settings));
        
        if (!settings.isShowLevelDebug()) {
            rendererComponents.add(lightingComponent = new LightingComponent(this, dungeon, settings));
        }
        
        rendererComponents.add(minimapComponent = new MinimapComponent(this, dungeon, settings));
        
        if (settings.isShowFPSCounter()) {
            rendererComponents.add(fpsCounterComponent = new FPSCounterComponent(this, dungeon, settings));
        }
        
        rendererComponents.add(hudComponent = new HUDComponent(this, dungeon, settings));
        
        // add mod components
        
        rendererComponents.sort(Comparator.comparingInt(RendererComponent::getZIndex));
        
        rendererComponents.forEach(r -> r.setCamera(r.useMainBatch() ? mainBatchCamera : camera));
        rendererComponents.forEach(r -> dungeon.eventSystem.addListener(r));
        rendererComponents.forEach(RendererComponent::initialise);
        
        for (TileMap tmap : TileMap.values()) {
            tmap.getRenderer().setRenderer(this);
        }
    }
    
    private void initialiseInputProcessors() {
        clearInputProcessors();
        addInputProcessor(new GameInputProcessor(dungeon, this));
        addInputProcessor(hudComponent.getStage());
    }
    
    private void updateWindowTitle() {
        Gdx.graphics.setTitle(String.format(
            "%s - %s - Turn %,d",
            GameAdapter.WINDOW_TITLE,
            dungeon.getName(),
            dungeon.turnSystem.getTurn()
        ));
    }
    
    /**
     * Updates the in-game camera position.
     */
    public void updateCamera() {
        Player p = dungeon.getPlayer();
        
        if (p != null && !settings.isShowLevelDebug()) {
            EntityAnimationData data = entityComponent.getAnimationProvider().getEntityAnimationData(p);
            
            float worldX = p.getPosition().x + (data != null ? data.cameraX : 0);
            float worldY = p.getPosition().y + (data != null ? data.cameraY : 0);
            
            float camX = (worldX + 0.5f) * TileMap.TILE_WIDTH;
            float camY = (worldY + 0.5f) * TileMap.TILE_HEIGHT;
            
            camera.position.x = Math.round(camX * zoomRounding) / zoomRounding;
            camera.position.y = Math.round(camY * zoomRounding) / zoomRounding;
    
            mainBatchCamera.position.x = Math.round(camX * mainBatchZoomRounding) / mainBatchZoomRounding;
            mainBatchCamera.position.y = Math.round(camY * mainBatchZoomRounding) / mainBatchZoomRounding;
        }
        
        camera.update();
        mainBatchCamera.update();
    }
    
    public void render(float delta) {
        renderTime += delta;
        
        if (turnLerping) {
            turnLerpTime += delta;
        }
        
        if (turnLerpTime >= TURN_LERP_DURATION) {
            turnLerping = false;
            turnLerpTime = 0;
            wasTurnLerping = true;
        } else {
            wasTurnLerping = false;
        }
        
        if (!settings.isShowTurnAnimations()) updateCamera();
        
        rendererComponents.forEach(r -> r.update(delta));
        
        if (settings.isShowTurnAnimations()) updateCamera();
        
        fbo.begin();
        
        mainBatch.setProjectionMatrix(mainBatchCamera.combined);
        
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT |
            (Gdx.graphics.getBufferFormat().coverageSampling?GL20.GL_COVERAGE_BUFFER_BIT_NV:0));
        
        mainBatch.begin();
        mainBatch.enableBlending();
        
        rendererComponents.stream()
            .filter(RendererComponent::useMainBatch)
            .forEach(r -> r.render(delta));
        
        mainBatch.end();
        fbo.end();
    
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        fboBatch.begin();
        fboBatch.disableBlending();
        Texture fboTex = fbo.getColorBufferTexture();
        fboBatch.setShader(xbr);
        xbr.setUniformf("u_size", fbo.getWidth() * 2, fbo.getHeight() * 2);
        fboBatch.draw(
            fboTex,
            -fbo.getWidth() / 4, -fbo.getHeight() / 4,
            fbo.getWidth(), fbo.getHeight(),
            0, 0,
            fboTex.getWidth(), fboTex.getHeight(),
            false, true
        );
        fboBatch.end();
    
        rendererComponents.stream()
            .filter(r -> !r.useMainBatch())
            .forEach(r -> r.render(delta));
        
        if (settings.isShowTurnAnimations()) updateCamera();
    }
    
    
    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    
        fbo = new FrameBuffer(Pixmap.Format.RGBA8888, width * 2, height * 2, false);
        fboBatch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
        
        camera.setToOrtho(false, width, height);
        mainBatchCamera.setToOrtho(false, width * 2, height * 2);
        updateCameraZoom();
        
        rendererComponents.forEach(r -> r.resize(width, height));
    }
    
    @Override
    public void show() {
        super.show();
        
        initialiseInputProcessors();
    }
    
    @Override
    public void dispose() {
        if (settings.isAutosave() && !dontSave && dungeon.getPlayer().isAlive()) {
            dungeon.serialiser.save();
        }
        
        mainBatch.dispose();

        rendererComponents.forEach(RendererComponent::dispose);
    }
    
    @EventHandler
    private void onLevelChange(LevelChangeEvent e) {
        turnLerpTime = 0;
        turnLerping = false;
    }
    
    @EventHandler
    private void onBeforeTurn(BeforeTurnEvent e) {
        if (settings.isShowTurnAnimations()) {
            turnLerpTime = 0;
            turnLerping = true;
        }
    }
    
    @EventHandler
    private void onTurn(TurnEvent e) {
        updateWindowTitle();
    }
    
    @EventHandler
    private void onQuit(QuitEvent e) {
        dontSave = true;
        Gdx.app.exit();
    }
    
    @EventHandler
    private void onSaveAndQuit(SaveAndQuitEvent e) {
        Gdx.app.exit();
    }
    
    @EventHandler
    private void onPlayerDeath(EntityDeathEvent e) {
        if (!e.isVictimPlayer()) return;
        
        game.setScreen(
            new DeathScreen(game, dungeon, e),
            new SlidingTransition(
                SlidingTransition.Direction.DOWN,
                false,
                Interpolation.circle
            ),
            0.5f
        );
    }
    
    public Matrix4 getCombinedTransform() {
        return camera.combined;
    }
    
    @RegisterAssetManager
    public static class RequiredAssets {
        private static ShaderProgram scale2x;
        private static ShaderProgram xbr;
    
        public static void loadAssets(Assets assets) {
            assets.shaders.load(shaderFile("scale2x"), s -> scale2x = s);
            assets.shaders.load(shaderFile("xbr"), s -> xbr = s);
        }
    }
}
