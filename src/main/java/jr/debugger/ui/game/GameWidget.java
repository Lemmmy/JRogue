package jr.debugger.ui.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import jr.debugger.ui.DebugUI;
import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.events.EventHandler;
import jr.dungeon.events.EventListener;
import jr.dungeon.events.LevelChangeEvent;
import jr.rendering.tiles.TileMap;
import jr.utils.Point;
import lombok.Setter;

import static jr.utils.QuickMaths.ifloor;

public class GameWidget extends Image implements EventListener {
    private DebugUI ui;
    private Skin skin;
    
    private Dungeon dungeon;
    private Level level;
    
    private LevelComponent levelComponent;
    private EntityComponent entityComponent;
    
    private FrameBuffer fbo;
    private TextureRegionDrawable fboRegion;
    private SpriteBatch spriteBatch;
    private OrthographicCamera camera;
    
    @Setter private LevelUtilPopup levelUtilPopup;
    
    public GameWidget(DebugUI ui, Skin skin, Dungeon dungeon) {
        this.ui = ui;
        this.skin = skin;
        
        this.dungeon = dungeon;
        this.dungeon.eventSystem.addListener(this);
        this.level = dungeon.getLevel();
        
        initialise();
    }
    
    private void initialise() {
        initialiseCamera();
        initialiseFBO();
        initialiseSpriteBatch();
        initialiseComponents();
        initialiseDrawable();
        initialiseListeners();
    }
    
    private void initialiseCamera() {
        // TODO: currently, we draw the camera y-down here. this is different to the rest of the game, but it seems that
        //       the FBO itself is drawn flipped. we probably shouldn't be drawing y-down here, so this needs to be
        //       investigated.
        camera = new OrthographicCamera(getMinWidth(), getMinHeight());
        camera.setToOrtho(true, getMinWidth(), getMinHeight());
        camera.update();
    }
    
    private void initialiseFBO() {
        fbo = new FrameBuffer(Pixmap.Format.RGBA8888, (int) getMinWidth(), (int) getMinHeight(), false);
        fboRegion = new TextureRegionDrawable(new TextureRegion(fbo.getColorBufferTexture()));
    }
    
    private void initialiseSpriteBatch() {
        spriteBatch = new SpriteBatch();
    }
    
    private void initialiseComponents() {
        levelComponent = new LevelComponent(dungeon);
        entityComponent = new EntityComponent(dungeon);
    }
    
    private void initialiseDrawable() {
        setDrawable(fboRegion);
    }
    
    private void initialiseListeners() {
        addListener(new GameWidgetClickListener());
    }
    
    @Override
    public void draw(Batch batch, float parentAlpha) {
        boolean blendingEnabled = batch.isBlendingEnabled();
        batch.disableBlending();
        
        super.draw(batch, parentAlpha);
        
        if (blendingEnabled) batch.enableBlending();
    }
    
    public void drawComponents() {
        fbo.begin();
        spriteBatch.setProjectionMatrix(camera.combined);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        spriteBatch.begin();
        spriteBatch.enableBlending();
        
        levelComponent.draw(spriteBatch);
        entityComponent.draw(spriteBatch);
        
        spriteBatch.end();
        fbo.end();
    }
    
    @EventHandler
    private void onLevelChange(LevelChangeEvent e) {
        level = dungeon.getLevel();
    }
    
    @Override
    public float getMinWidth() {
        if (dungeon.getLevel() == null) return 0;
        return dungeon.getLevel().getWidth() * TileMap.TILE_WIDTH;
    }
    
    @Override
    public float getMinHeight() {
        if (dungeon.getLevel() == null) return 0;
        return dungeon.getLevel().getHeight() * TileMap.TILE_HEIGHT;
    }
    
    private class GameWidgetClickListener extends ClickListener {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            if (levelUtilPopup != null) return;
            
            Point worldPos = Point.get(ifloor(x / TileMap.TILE_WIDTH), ifloor(y / TileMap.TILE_HEIGHT));
            if (!worldPos.insideLevel(level)) return;
            
            levelUtilPopup = new LevelUtilPopup(ui, GameWidget.this, skin, dungeon, worldPos);
            getStage().addActor(levelUtilPopup);
            levelUtilPopup.setPosition(event.getStageX(), event.getStageY());
        }
    }
}
